name := """IDP_Benchmarking_3"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs
)

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.6",
  "org.apache.poi" % "poi" % "3.10-FINAL",
  "org.apache.poi" % "poi-ooxml" % "3.9",
  "org.apache.commons" % "commons-math3" % "3.6"
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
