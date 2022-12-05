package babymed.services.users.generators

import org.scalacheck.Gen

import babymed.services.users.domain.City
import babymed.services.users.domain.CreatePatient
import babymed.services.users.domain.Patient
import babymed.services.users.domain.PatientWithAddress
import babymed.services.users.domain.Region
import babymed.services.users.domain.types.CityId
import babymed.services.users.domain.types.RegionId

trait PatientGenerators extends TypeGen {
  lazy val patientGen: Gen[Patient] =
    for {
      id <- patientIdGen
      createdAt <- localDateTimeGen
      firstname <- firstNameGen
      lastname <- lastNameGen
      regionId <- regionIdGen
      cityId <- cityIdGen
      address <- addressGen.opt
      birthday <- dateGen
      phone <- phoneGen
    } yield Patient(
      id,
      createdAt,
      firstname,
      lastname,
      regionId,
      cityId,
      address,
      birthday,
      phone,
    )

  def createPatientGen(
      maybeRegionId: Option[RegionId] = None,
      maybeCityId: Option[CityId] = None,
    ): Gen[CreatePatient] =
    for {
      firstname <- firstNameGen
      lastname <- lastNameGen
      regionId <- regionIdGen
      cityId <- cityIdGen
      address <- addressGen.opt
      birthday <- dateGen
      phone <- phoneGen
    } yield CreatePatient(
      firstname,
      lastname,
      maybeRegionId.getOrElse(regionId),
      maybeCityId.getOrElse(cityId),
      address,
      birthday,
      phone,
    )

  lazy val regionGen: Gen[Region] =
    for {
      id <- regionIdGen
      name <- regionNameGen
    } yield Region(id, name)

  lazy val cityGen: Gen[City] =
    for {
      id <- cityIdGen
      regionId <- regionIdGen
      name <- cityNameGen
    } yield City(id, regionId, name)

  lazy val patientWithAddressGen: Gen[PatientWithAddress] =
    for {
      patient <- patientGen
      region <- regionGen
      city <- cityGen
    } yield PatientWithAddress(patient, region, city)
}
