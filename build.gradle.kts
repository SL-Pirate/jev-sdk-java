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
