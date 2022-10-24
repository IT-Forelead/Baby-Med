package babymed.support.skunk.syntax

import scala.language.implicitConversions

import cats.effect.Concurrent
import cats.effect.MonadCancel
import cats.effect.Resource
import cats.implicits._
import skunk._
import skunk.codec.numeric.int4
import skunk.implicits._

trait SkunkSyntax {
  implicit def skunkSyntaxCommandOps[A](cmd: Command[A]): CommandOps[A] =
    new CommandOps(cmd)
  implicit def skunkSyntaxQueryVoidOps[B](query: Query[Void, B]): QueryVoidOps[B] =
    new QueryVoidOps(query)
  implicit def skunkSyntaxQueryOps[A, B](query: Query[A, B]): QueryOps[A, B] =
    new QueryOps(query)
  implicit def skunkSyntaxFragmentOps(af: AppliedFragment): FragmentOps =
    new FragmentOps(af)
}

final class QueryOps[A, B](query: Query[A, B]) {
  def queryM[F[_], G[_]](
      action: PreparedQuery[F, A, B] => F[G[B]]
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[G[B]] =
    session.use {
      _.prepare(query).use(action)
    }

  def query[F[_]](
      action: PreparedQuery[F, A, B] => F[B]
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[B] =
    session.use {
      _.prepare(query).use(action)
    }

  def queryUnique[F[_]](
      args: A
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[B] =
    query { prepQuery: PreparedQuery[F, A, B] =>
      prepQuery.unique(args)
    }

  def queryList[F[_]: Concurrent](
      args: A
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[List[B]] =
    queryM { prepQuery: PreparedQuery[F, A, B] =>
      prepQuery.stream(args, 1024).compile.toList
    }

  def queryStream[F[_]](
      args: A
    )(implicit
      sessionRes: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): fs2.Stream[F, B] =
    for {
      session <- fs2.Stream.resource(sessionRes)
      query <- fs2.Stream.resource(session.prepare(query))
      stream <- query.stream(args, 128)
    } yield stream

  def queryOption[F[_]](
      args: A
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[Option[B]] =
    queryM { prepQuery: PreparedQuery[F, A, B] =>
      prepQuery.option(args)
    }
}
final class QueryVoidOps[B](query: Query[Void, B]) {
  def all[F[_]](
      implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[List[B]] =
    session.use {
      _.execute(query)
    }

  def queryStream[F[_]](
      query: Query[Void, B]
    )(implicit
      sessionRes: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): fs2.Stream[F, B] =
    for {
      session <- fs2.Stream.resource(sessionRes)
      query <- fs2.Stream.resource(session.prepare(query))
      stream <- query.stream(Void, 128)
    } yield stream
}

final class CommandOps[A](cmd: Command[A]) {
  def action[F[_], B](
      action: PreparedCommand[F, A] => F[B]
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[B] =
    session.use {
      _.prepare(cmd).use(action)
    }

  def execute[F[_]](
      args: A
    )(implicit
      session: Resource[F, Session[F]],
      ev: MonadCancel[F, Throwable],
    ): F[Unit] =
    action[F, Unit] {
      _.execute(args).void
    }
}

final class FragmentOps(af: AppliedFragment) {
  def paginate(lim: Int, index: Int): AppliedFragment = {
    val offset = (index - 1) * lim
    val filter: Fragment[Int ~ Int] = sql" LIMIT $int4 OFFSET $int4 "
    af |+| filter(lim ~ offset)
  }

  /** Returns `WHERE (f1) AND (f2) AND ... (fn)` for defined `f`, if any, otherwise the empty fragment. */

  def whereAndOpt(fs: Option[AppliedFragment]*): AppliedFragment = {
    val filters =
      if (fs.flatten.isEmpty)
        AppliedFragment.empty
      else
        fs.flatten.foldSmash(void" WHERE ", void" AND ", AppliedFragment.empty)
    af |+| filters
  }

  def andOpt(fs: Option[AppliedFragment]*): AppliedFragment = {
    val filters =
      if (fs.flatten.isEmpty)
        AppliedFragment.empty
      else
        fs.flatten.foldSmash(void" AND ", void" AND ", AppliedFragment.empty)
    af |+| filters
  }
}
