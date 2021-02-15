/*
 * Custom bot for the Hiro Akiba fan server on discord
 * Copyright (C) 2021 Duncan "duncte123" Sterken
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    implementation(group = "net.dv8tion", name = "JDA", version = "4.2.0_227")
    implementation(group = "com.jagrosh", name = "jda-utilities-command", version = "3.0.4")
    
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.1")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
    implementation(group = "net.sf.trove4j", name = "trove4j", version = "3.0.3")

    implementation(group = "com.zaxxer", name = "HikariCP", version = "3.4.1")
    implementation(group = "org.xerial", name = "sqlite-jdbc", version = "3.30.1")

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
    gradleVersion = "6.8"
}
