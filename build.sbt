import play.sbt.routes.RoutesKeys
import sbt.Def

lazy val appName: String = "register-estate-hub-frontend"

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / majorVersion := 0

lazy val microservice = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(CodeCoverageSettings())
  .settings(
    inConfig(Test)(testSettings),
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 8822,
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true
  )

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  )
)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
