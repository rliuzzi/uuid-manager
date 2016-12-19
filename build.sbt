name := """uuid-manager"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  // If you enable PlayEbean plugin you must remove these
  // JPA dependencies to avoid conflicts.
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.4.0.201606070830-r"
)


// setting a maintainer which is used for all packaging types
maintainer := "Romina Liuzzi"

// exposing the play ports
dockerExposedPorts in Docker := Seq(9000, 9443)

// run this with: docker run -p 9000:9000 uuid-manager:1.0-SNAPSHOT

fork in run := true