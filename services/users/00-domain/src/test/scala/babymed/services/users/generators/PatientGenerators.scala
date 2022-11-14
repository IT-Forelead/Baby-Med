package babymed.services.users.generators

import org.scalacheck.Gen

import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.Town
import babymed.services.users.domain.types.RegionId
import babymed.services.users.domain.types.TownId

trait PatientGenerators extends TypeGen {
  lazy val patientGen: Gen[Patient] =
    for {
      id <- patientIdGen
      createdAt <- localDateTimeGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      regionId <- regionIdGen
      townId <- townIdGen
      address <- addressGen
      birthday <- dateGen
      phone <- phoneGen
    } yield Patient(
      id,
      createdAt,
      firstname,
      lastname,
      regionId,
      townId,
      address,
      birthday,
      phone,
    )

  def createPatientGen(
      maybeRegionId: Option[RegionId] = None,
      maybeTownId: Option[TownId] = None,
    ): Gen[CreatePatient] =
    for {
      firstname <- firstNameGen
      lastname <- lastNameGen
      regionId <- regionIdGen
      townId <- townIdGen
      address <- addressGen
      birthday <- dateGen
      phone <- phoneGen
    } yield CreatePatient(
      firstname,
      lastname,
      maybeRegionId.getOrElse(regionId),
      maybeTownId.getOrElse(townId),
      address,
      birthday,
      phone,
    )

  lazy val regionGen: Gen[Region] =
    for {
      id <- regionIdGen
      name <- regionNameGen
    } yield Region(id, name)

  lazy val townGen: Gen[Town] =
    for {
      id <- townIdGen
      regionId <- regionIdGen
      name <- townNameGen
    } yield Town(id, regionId, name)

  lazy val patientWithAddressGen: Gen[PatientWithAddress] =
    for {
      patient <- patientGen
      region <- regionGen
      town <- townGen
    } yield PatientWithAddress(patient, region, town)
}
