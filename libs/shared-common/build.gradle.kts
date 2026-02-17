import com.google.protobuf.gradle.*
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.protobuf)
}

group = "io.github.pedroermarinho"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Spring Boot Core (Usando 'api' pois é uma java-library)
    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.validation)
    api(libs.spring.data.commons)
    api(libs.spring.web)

    // gRPC & Protobuf Runtime
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.protobuf)
    api(libs.protobuf.kotlin)

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.api)

    // Utilities & DB
    api(libs.uuid.creator)
    implementation(libs.jooq)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protoc}"
    }
    plugins {
        id("grpc") {
            artifact = libs.protoc.gen.grpc.java.get().toString()
        }
        id("grpckt") {
            artifact = "${libs.protoc.gen.grpc.kotlin.get()}:jdk8@jar"
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

// Desabilita a criação do JAR executável (Fat Jar) pois é uma lib
tasks.named<BootJar>("bootJar") {
    enabled = false
}

// Habilita o JAR padrão (Plain Jar) para ser consumido por outros módulos
tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}
