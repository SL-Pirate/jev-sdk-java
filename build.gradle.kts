plugins {
    id("java")
}

group = "dev.isira"
version = "1.0-SNAPSHOT"

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
