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
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Wrapper> {
    distributionType = DistributionType.ALL
    gradleVersion = "5.6.3"
}