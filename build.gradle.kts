import name.remal.gradle_plugins.dsl.extensions.convention
import name.remal.gradle_plugins.dsl.extensions.get
import name.remal.gradle_plugins.dsl.extensions.implementation
import name.remal.gradle_plugins.dsl.extensions.testImplementation
import name.remal.gradle_plugins.plugins.publish.ossrh.RepositoryHandlerOssrhExtension


plugins {
    `maven-publish`
    `java-library`
    signing
    jacoco
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    id("name.remal.maven-publish-ossrh") version "1.0.192"
}

group = "com.github.mckernant1"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.isEnabled = true
        csv.isEnabled = true
        html.destination = file("${buildDir}/jacocoHtml")
    }
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            include("**/*Client*")
        }
    )
}

jacoco {
    toolVersion = "0.8.5"
    reportsDir = file("$buildDir/customJacocoReportDir")
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(tasks.dokka)
}



publishing.repositories.convention[RepositoryHandlerOssrhExtension::class.java].ossrh {
    credentials.username = System.getenv("MAVEN_USERNAME")
    credentials.password = System.getenv("MAVEN_PASSWORD")
}



publishing {
    publications {
        create<MavenPublication>("default") {
            artifactId = "fs-scheduler"
            from(components["java"])
            val sourcesJar by tasks.creating(Jar::class) {
                val sourceSets: SourceSetContainer by project
                from(sourceSets["main"].allSource)
                classifier = "sources"
            }
            artifact(dokkaJar)
            artifact(sourcesJar)
            pom {
                name.set("fs-scheduler")
                description.set("A light file system storage system")
                url.set("https://github.com/mckernant1/fs-scheduler")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mckernant1")
                        name.set("Tom McKernan")
                        email.set("tmeaglei@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/mckernant1/fs-scheduler.git")
                    developerConnection.set("scm:git:ssh://github.com/mckernant1/fs-scheduler.git")
                    url.set("https://github.com/mckernant1/fs-scheduler")
                }
            }
        }
    }
}