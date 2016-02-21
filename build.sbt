name := """IDP_Benchmarking_3"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.6"
//"org.webjars" % "bootstrap" % "4.0.0-alpha.2"
)

unmanagedBase :=
  {
    if (sys.props("os.name").toLowerCase().contains("mac"))
      baseDirectory.value / "lib/OSX_Linux"
    else if (sys.props("os.name").toLowerCase().contains("win"))
      baseDirectory.value / "lib/WIN"
    else
      baseDirectory.value / "lib"
  }


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
