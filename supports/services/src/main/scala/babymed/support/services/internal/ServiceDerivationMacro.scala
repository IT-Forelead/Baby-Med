package babymed.support.services.internal

import scala.reflect.macros.blackbox

import higherkindness.mu.rpc

class ServiceDerivationMacro(val c: blackbox.Context) {
  import c.universe._

  val serviceImpl: rpc.internal.serviceImpl = new higherkindness.mu.rpc.internal.serviceImpl(c)

  /** Derives the traits and classes required to add RPC capabilities to a service trait. */
  @annotation.nowarn
  def deriveService(annottees: c.Expr[Any]*): c.Expr[Any] = {

    val q"new service(..${_}).macroTransform(..${_})" = c.macroApplication

    def modifiedTrait(classDecl: ClassDef, compDeclOpt: Option[ModuleDef] = None): c.Expr[Any] = {
      val (serviceProtoTypeName: TypeName, declarations) =
        try {
          val q"..${_} trait $traitName[..${_}] extends ..${_} { ..$decls }" = classDecl
          (traitName, decls)
        }
        catch {
          case _: MatchError =>
            c.abort(c.enclosingPosition, "Annotation is only supported on traits")
        }

      // Collect all methods exposed by the service protocol
      val serviceMethods = declarations.collect {
        case q"..${_} def $methodName[..${_}](...$paramss): ${_}[${rtpe}]" =>
          (methodName, paramss, rtpe)
        case q"..${_} def ${_}(...${_}): ${_} = { ..${_} }" =>
          c.abort(c.enclosingPosition, "Methods with default implementation aren't supported.")
      }

      val (rpcTraitTypeName, rpcTrait) = generateRpcTrait(serviceProtoTypeName, serviceMethods)
      val rpcClient = generateServiceClient(serviceProtoTypeName, rpcTraitTypeName, serviceMethods)
      val serviceHandler =
        generateServiceHandler(serviceProtoTypeName, rpcTraitTypeName, serviceMethods)

      // Create companion object
      val result = compDeclOpt
        .map {
          case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$existingBody }" =>
            q"""
              $classDecl

              $mods object $tname extends { ..$earlydefns } with ..$parents { $self =>
                ..$existingBody

                ..$rpcTrait
                ..$rpcClient
                ..$serviceHandler
              }
            """
        }
        .getOrElse {
          q"""
            $classDecl

            object ${serviceProtoTypeName.toTermName} {
              ..$rpcTrait
              ..$rpcClient
              ..$serviceHandler
            }
          """
        }

      c.Expr(result)
    }

    val result = annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil if classDecl.mods.hasFlag(Flag.TRAIT) =>
        modifiedTrait(classDecl)
      case (classDecl: ClassDef) :: (compDecl: ModuleDef) :: Nil
           if classDecl.mods.hasFlag(Flag.TRAIT) =>
        modifiedTrait(classDecl, Some(compDecl))
      case _ => c.abort(c.enclosingPosition, "@service can only be used on traits")
    }

    if (sys.props.exists { case (key, value) => key == "babymed.service.macro" && value == "true" })
      c.info(c.enclosingPosition, showCode(result.tree), force = true)

    result
  }

  /** Generates the underlying mu-scala infrastructure. */
  def generateRpcTrait(
      serviceProtoTypeName: TypeName,
      serviceMethods: List[(TermName, List[List[ValDef]], Tree)],
    ): (TypeName, Tree) = {
    // Generate case classes that will wrap the method params
    val methodRequestCaseClasses = serviceMethods.zipWithIndex.map {
      case ((name, paramss, _), idx) =>
        val caseClassTypeName = requestCaseClassName(name, idx)
        val constructorParams = paramss.flatMap(_.map {
          case ValDef(_, name, tpt, _) =>
            q"$name: $tpt"
        })

        q"@derevo.derive(_root_.derevo.circe.magnolia.decoder, _root_.derevo.circe.magnolia.encoder) case class $caseClassTypeName(..$constructorParams)"
    }

    // Generate the wrapping mu-Scala RPC trait
    val rpcTraitMethodDefinitions = serviceMethods.zipWithIndex.map {
      case ((name, _, rtpe), idx) =>
        val caseClassTypeName = requestCaseClassName(name, idx)
        q"def $name(request: babymed.support.services.rpc.RpcRequest[$caseClassTypeName]): F[$rtpe]"
    }

    val rpcTraitTypeName = TypeName(s"${serviceProtoTypeName}Rpc")
    val rpcTrait = q"""
      trait $rpcTraitTypeName[F[_]] {
        ..$rpcTraitMethodDefinitions
      }
    """

    (
      rpcTraitTypeName,
      q"""
        ..$methodRequestCaseClasses
        ..${serviceImpl
          .service(c.Expr[Any](rpcTrait).asInstanceOf[serviceImpl.c.universe.Expr[Any]])
          .asInstanceOf[c.universe.Expr[Any]]}
      """,
    )
  }

  /** Generates the client implementation of the service protocol. */
  def generateServiceClient(
      serviceProtoTypeName: TypeName,
      rpcTraitTypeName: TypeName,
      serviceMethods: List[(TermName, List[List[ValDef]], Tree)],
    ): Tree = {
    val rpcClientTypeName = TypeName(s"${serviceProtoTypeName}RpcClient")
    val rpcClientBindMethod: DefDef =
      q"""
        def client[F[_]: _root_.cats.effect.Async](
          channelFor: _root_.higherkindness.mu.rpc.ChannelFor,
          channelConfigList: List[_root_.higherkindness.mu.rpc.channel.ManagedChannelConfig] = List(
            _root_.higherkindness.mu.rpc.channel.UsePlaintext()),
          options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT
        ): _root_.cats.effect.Resource[F, $serviceProtoTypeName[F]] = {
          ${rpcTraitTypeName.toTermName}.client[F](channelFor, channelConfigList, options).map { muClient =>
            new $rpcClientTypeName(muClient)
          }
        }
      """

    q"""
      class $rpcClientTypeName[F[_]: _root_.cats.effect.Async](
        service: $rpcTraitTypeName[F]
      ) extends $serviceProtoTypeName[F] {
        import _root_.cats.implicits._

        ..${generateServiceClientMethods(serviceMethods)}
      }

      $rpcClientBindMethod
    """
  }

  /** Generates the client methods. */
  def generateServiceClientMethods(
      serviceMethods: List[(TermName, List[List[ValDef]], Tree)]
    ): Seq[Tree] =
    serviceMethods.zipWithIndex.map {
      case ((name, paramss, rtpe), idx) =>
        val caseClassTypeName = requestCaseClassName(name, idx)
        val paramNames = paramss.flatMap(_.map { param: ValDef =>
          param.name
        })

        val methodParamLists = paramss.map(_.map { param: ValDef =>
          q"${param.name}: ${param.tpt}"
        })

        q"""
          override def $name(...$methodParamLists): F[$rtpe] =
            service.$name(_root_.babymed.support.services.rpc.RpcRequest(Map.empty[String, String], ${caseClassTypeName.toTermName}(..$paramNames)))

        """
    }

  /** Generates the mu-scala handler infrastructure. */
  def generateServiceHandler(
      serviceProtoTypeName: TypeName,
      rpcTraitTypeName: TypeName,
      serviceMethods: List[(TermName, List[List[ValDef]], Tree)],
    ): Tree = {
    val serviceHandlerTypeName = TypeName(s"${serviceProtoTypeName}RpcHandler")

    val bindMethodImpl = q"""
      def bindService[F[_]: _root_.cats.effect.Async](
        service: $serviceProtoTypeName[F]
      ): _root_.cats.effect.Resource[F,io.grpc.ServerServiceDefinition] = {

        implicit val rpcService: $rpcTraitTypeName[F] = new $serviceHandlerTypeName(
          service
        )

        ${rpcTraitTypeName.toTermName}.bindService[F]
      }
    """

    q"""
      class $serviceHandlerTypeName[F[_]: _root_.cats.effect.Async](
        handler: $serviceProtoTypeName[F],
      ) extends $rpcTraitTypeName[F] {
        import _root_.cats.implicits._

        ..${generateServiceHandlerMethods(serviceMethods)}
      }

      $bindMethodImpl
    """
  }

  /** Generates the handler methods. */
  def generateServiceHandlerMethods(
      serviceMethods: List[(TermName, List[List[ValDef]], Tree)]
    ): List[Tree] =
    serviceMethods.zipWithIndex.map {
      case ((name, paramss, rtpe), idx) =>
        val caseClassTypeName = requestCaseClassName(name, idx)

        val tupleToMethodCall = paramss.map { paramList =>
          paramList.map { param: ValDef =>
            q"${param.name}"
          }
        }

        q"""
          override def $name(
            params: _root_.babymed.support.services.rpc.RpcRequest[$caseClassTypeName]
          ): F[$rtpe] = {
            import params.request._
            handler.$name(...$tupleToMethodCall)
          }
        """
    }

  /** Generates a standardized case class name responsible to carry method params and the context map. */
  def requestCaseClassName[A](methodName: A, idx: Int): c.TypeName =
    c.universe.TypeName(s"Request_${methodName}_$$$idx")
}
