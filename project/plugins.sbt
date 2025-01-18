libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.33",
)

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "4.3.2")
