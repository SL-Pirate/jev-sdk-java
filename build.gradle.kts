plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "dev.isira"
version = "1.0"

repositories {
    mavenCentral()
}

val lombokVersion = "1.18.48"

dependencies {
    implementation("tools.jackson.core:jackson-databind:3.2.3")
    implementation("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation("xyz.elemental:elemental-media-type-api:7.7.0")
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Keep constructor parameter names in the bytecode so Jackson can map JSON properties onto
// constructor arguments without @JsonProperty on every parameter.
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates("dev.isira", "jev-sdk", "$version")
    pom {
        name.set("jev-sdk")
        description.set("A small Java client for the Jev API (`https://api.typesafe.ai/v1/systemone`)")
        url.set("https://github.com/SL-Pirate/jev-sdk-java")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
            }
        }
        developers {
            developer {
                id.set("SL-Pirate")
                name.set("Isira Herath")
                url.set("https://github.com/SL-Pirate")
            }
        }
        scm {
            url.set("https://github.com/SL-Pirate/jev-sdk-java")
            connection.set("scm:git:git://github.com/SL-Pirate/jev-sdk-java.git")
            developerConnection.set("scm:git:ssh://git@github.com/SL-Pirate/jev-sdk-java.git")
        }
    }
}

tasks.test {
    useJUnitPlatform()

    // Expose variables from a local .env (e.g. JEV_KEY) to the test JVM so the live API
    // tests can run. Tests gated on JEV_KEY are skipped when the file or variable is absent.
    val dotenv = layout.projectDirectory.file(".env").asFile
    if (dotenv.isFile) {
        dotenv.readLines()
            .map { it.trim().removePrefix("export ") }
            .filter { it.isNotEmpty() && !it.startsWith("#") && it.contains('=') }
            .forEach { line ->
                val (key, value) = line.split('=', limit = 2)
                environment(key.trim(), value.trim().removeSurrounding("\"").removeSurrounding("'"))
            }
    }

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
