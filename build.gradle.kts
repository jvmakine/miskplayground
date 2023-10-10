plugins {
    kotlin("jvm") version "1.9.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    api(platform("com.squareup.misk:misk-bom:2023.10.06.160258-7d03e8a"))
    implementation("org.eclipse.jetty:jetty-servlet:10.0.15")
    implementation("com.squareup.misk:misk")
}
