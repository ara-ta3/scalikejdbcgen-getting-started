organization := "com.example"

name := "scalikejdbcgen-getting-startd"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.16"

enablePlugins(ScalikejdbcPlugin)

scalikejdbcSettings ++= Seq(
  scalikejdbcGeneratorConfig in Compile := GeneratorConfig(
    packageName = "com.example.infrastructures",
    template = "queryDsl"
  )
)


