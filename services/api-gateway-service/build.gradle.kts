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

val versions =
	mapOf(
		"slf4j" to "2.0.17",
		"logback" to "1.5.27",
		"kotlinLogging" to "7.0.14",
	)

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
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

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.1")
	}
}

dependencies {
	implementation(project(":libs:shared-common"))


	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("io.grpc:grpc-netty:1.79.0")


	// observabilidade
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-otlp")
	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")


	// Logging
	implementation("ch.qos.logback:logback-classic:${versions["logback"]}")
	implementation("io.github.oshai:kotlin-logging-jvm:${versions["kotlinLogging"]}")
	implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")


	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
