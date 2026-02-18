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
description = "Shared Common: codigo compartilhado entres os projetos"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(platform(libs.spring.grpc.bom))

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Spring Boot Core
    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.validation)
    api(libs.spring.data.commons)
    api(libs.spring.web)

    // gRPC & Protobuf Runtime
    api(libs.spring.grpc.starter)
    api(libs.grpc.services)

    api(libs.grpc.stub)
    api(libs.grpc.protobuf)

    api(libs.grpc.kotlin.stub)
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
        artifact = "com.google.protobuf:protoc:${libs.versions.protoc.get()}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${libs.versions.grpcKotlin.get()}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc") {
                    option("@generated=omit")
                }
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
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
