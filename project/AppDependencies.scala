import sbt.*

object AppDependencies {

  val boostrapVersion = "9.19.0"
  val mongoVersion = "2.10.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                     % mongoVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"             % "12.18.0",
    "uk.gov.hmrc"             %% "domain-play-30"                         % "11.0.0",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping-play-30"  % "3.3.0",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"             % boostrapVersion,
    "uk.gov.hmrc"             %% "tax-year"                               % "6.0.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-30"   % boostrapVersion,
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-30"  % mongoVersion,
    "org.scalatestplus"           %% "scalacheck-1-18"          % "3.2.19.0",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
