import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.buildpack.platform.build.PullPolicy

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
}


group = "io.github.pedroermarinho"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.jvm.get())
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":libs:shared-common"))

    implementation(platform(libs.spring.cloud.dependencies))
    implementation(platform(libs.spring.dotenv.bom))

    // --- Webflux & GraphQL ---
    implementation(libs.spring.boot.starter.graphql)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.spring.cloud.starter.gateway.server.webflux)

    // --- Spring Extras ---
    implementation(libs.spring.dotenv)
    annotationProcessor(libs.spring.boot.configuration.processor)


    // --- Security ---
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.spring.boot.starter.security)

    // --- Kotlin Utilities ---
    implementation(libs.jackson.module.kotlin)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.reactor)

    // --- gRPC ---
    implementation(libs.grpc.netty)

    // --- Observabilidade ---
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.micrometer.registry.otlp)
    implementation(libs.micrometer.tracing.bridge.otel)
    implementation(libs.opentelemetry.exporter.otlp)

    // --- Logging ---
    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.api)

    // --- Processamento de Anotações ---
    annotationProcessor(libs.spring.boot.configuration.processor)

    // --- Testes ---
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.reactor.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.spring.graphql.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.google.protobuf" && requested.name == "protobuf-java") {
            useVersion("4.33.5")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("comandalivre/${project.name}:latest")
    pullPolicy.set(PullPolicy.IF_NOT_PRESENT)
    environment.set(mapOf("BP_JVM_VERSION" to "21"))
    publish.set(false)
}
