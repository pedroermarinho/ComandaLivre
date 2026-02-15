import com.google.protobuf.gradle.*
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	kotlin("jvm") version "2.3.10"
	id("com.google.protobuf") version "0.9.6"
    `java-library`
	kotlin("plugin.spring") version "2.3.10"
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.github.pedroermarinho"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

val versions =
    mapOf(
        "slf4j" to "2.0.17",
        "logback" to "1.5.27",
        "kotlinLogging" to "7.0.14",
        "uuidCreator" to "6.1.1",
        "jooq" to "3.19.15",
    )

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.data:spring-data-commons")
    api("org.springframework:spring-web")

    api("io.grpc:grpc-kotlin-stub:1.4.1")
    api("io.grpc:grpc-protobuf:1.61.1")
    api("com.google.protobuf:protobuf-kotlin:3.25.3")

    // Logging
    implementation("ch.qos.logback:logback-classic:${versions["logback"]}")
    implementation("io.github.oshai:kotlin-logging-jvm:${versions["kotlinLogging"]}")
    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")

    api("com.github.f4b6a3:uuid-creator:${versions["uuidCreator"]}")
    implementation("org.jooq:jooq:${versions["jooq"]}")

}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.33.5"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.79.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.5.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}