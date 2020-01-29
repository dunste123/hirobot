import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version("5.0.0")
}

group = "me.duncte123"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "$group.hirobot.Hiro"
}

repositories {
    jcenter()
}

dependencies {
    implementation(group = "net.dv8tion", name = "JDA", version = "4.1.1_101")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    implementation(group = "org.jscience", name = "jscience", version = "4.3.1")


}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.compilerArgs = listOf("-Xlint:deprecation", "-Xlint:unchecked")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Wrapper> {
    distributionType = DistributionType.ALL
    gradleVersion = "5.6.3"
}