import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version("5.0.0")
}

group = "me.duncte123"
version = "1.0-SNAPSHOT"

application {
//    mainClassName = "$group.hirobot.Hiro"
    mainClassName = "$group.hirobot.RoutesJSRegexParser"
}

repositories {
    jcenter()
}

dependencies {
    implementation(group = "net.dv8tion", name = "JDA", version = "4.1.1_105")
    implementation(group = "com.jagrosh", name = "jda-utilities", version = "3.0.2")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    implementation(group = "org.jscience", name = "jscience", version = "4.3.1")
    implementation(group = "net.sf.trove4j", name = "trove4j", version = "3.0.3")

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