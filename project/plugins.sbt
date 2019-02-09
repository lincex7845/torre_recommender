resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.typesafeRepo("releases")

resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.17")