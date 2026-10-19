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

// README.md quotes the dependency coordinates with a concrete version. This rewrites those
// snippets from `version` above, so run `./gradlew syncReadmeVersion` after bumping it.
tasks.register("syncReadmeVersion") {
    group = "documentation"
    description = "Updates the version in README.md's dependency snippets to match the project version."

    val readme = layout.projectDirectory.file("README.md")
    val coordinates = "${project.group}:${project.name}"
    val artifactId = project.name
    val newVersion = project.version.toString()

    doLast {
        val file = readme.asFile
        val before = file.readText()
        val after = before
            .replace(Regex("""${Regex.escape(coordinates)}:[^"'`\s<]+"""), "$coordinates:$newVersion")
            .replace(
                Regex("""(<artifactId>${Regex.escape(artifactId)}</artifactId>\s*<version>)[^<]+(</version>)"""),
                "$1$newVersion$2"
            )
        if (after != before) {
            file.writeText(after)
            println("README.md now references $coordinates:$newVersion")
        } else {
            println("README.md already references $coordinates:$newVersion")
        }
    }
}
