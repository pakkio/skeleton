name := "skeleton"

organization := "com.github.fractal"

version := "1.2"

scalaVersion := "2.11.7"

resolvers ++= Seq(
    "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

//resolvers += "jitpack" at "https://jitpack.io" //this to be able to get from github

//Define dependencies. These ones are only required for Test and Integration Test scopes.
libraryDependencies ++= Seq(
    "org.scalatest"   %% "scalatest"    % "2.2.4"   % "test,it",
    "org.scalacheck"  %% "scalacheck"   % "1.12.5"      % "test,it",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
    "joda-time" % "joda-time" % "2.9.3",
    "org.scala-saddle" %% "saddle-core" % "1.3.+", //this to have date comparators
    "commons-net" % "commons-net" % "3.4",
    "com.typesafe.akka" %% "akka-actor" % "2.4.3",
    "org.apache.spark" %% "spark-core" % "1.6.1",
    "org.apache.hadoop" % "hadoop-client" % "2.6.0"


    //"com.github.owainlewis" % "scala-uk.co.pakkio.ftp" % "master-SNAPSHOT" //this to get uk.co.pakkio.ftp



)

// For Settings/Task reference, see http://www.scala-sbt.org/release/sxr/sbt/Keys.scala.html

// Compiler settings. Use scalac -X for other options and their description.
// See Here for more info http://www.scala-lang.org/files/archive/nightly/docs/manual/html/scalac.html 
scalacOptions ++= List("-feature","-deprecation", "-unchecked", "-Xlint")

// ScalaTest settings.
// Ignore tests tagged as @Slow (they should be picked only by integration test)
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l", "org.scalatest.tags.Slow", "-u","target/junit-xml-reports", "-oD", "-eS")

//Style Check section
scalastyleConfig <<= baseDirectory { _ / "src/main/config" / "scalastyle-config.xml" }

// Generate Eclipse project with sources for dependencies
EclipseKeys.withSource := true

coverageEnabled := true