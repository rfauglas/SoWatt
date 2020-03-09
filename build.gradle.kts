import org.jetbrains.kotlin.gradle.tasks.KotlinCompile



plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"//2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
}

apply(plugin = "io.spring.dependency-management")
buildscript {
	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-noarg:1.3.61")
	}
}

apply(plugin="idea")

apply(plugin = "kotlin-jpa")
group = "org.sowatt"
version = "2.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8
val cucumber_version = "5.0.0-RC4"

repositories {
    mavenCentral()
    jcenter()
    google()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.data:spring-data-rest-hal-browser")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    implementation("com.github.calimero:calimero-core:2.4")
    implementation("com.google.api-client:google-api-client:1.30.3")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.30.3")
    implementation("com.google.apis:google-api-services-sheets:v4-rev609-1.25.0")
    implementation("uk.co.4ng:enocean4j:1.1.0")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("io.cucumber:cucumber-java8:${cucumber_version}")
    testImplementation("io.cucumber:cucumber-junit:${cucumber_version}")
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks =  Runtime.getRuntime().availableProcessors()
    setForkEvery(1)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
