import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.buildpack.platform.build.PullPolicy
import nu.studer.gradle.jooq.JooqEdition

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jooq)
    alias(libs.plugins.liquibase)
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

buildscript {
    dependencies {
        classpath(libs.liquibase.core)
    }
}

dependencies {
    implementation(project(":libs:shared-common"))

    implementation(platform(libs.spring.dotenv.bom))

    // --- Core Spring ---
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.data.commons)

    // --- Spring Extras ---
    implementation(libs.spring.boot.starter.actuator)
    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.spring.dotenv)
    implementation(libs.spring.boot.starter.aop)

    // --- Kotlin + Coroutines ---
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.nimbus.jose.jwt)

    // --- Database: PostgreSQL, JOOQ, Liquibase ---
    implementation(libs.uuid.creator)
    implementation(libs.postgresql)
    implementation(libs.jooq)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.liquibase.core)

    // Dependências de Geração do jOOQ (Runtime do plugin)
    jooqGenerator(libs.jooq.meta)
    jooqGenerator(libs.jooq.codegen)
    jooqGenerator(libs.jooq.meta.extensions)
    jooqGenerator(libs.postgresql)

    // Dependências de Runtime do Liquibase
    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.snakeyaml)
    liquibaseRuntime(libs.postgresql)
    liquibaseRuntime(libs.picocli)

    // --- Cache ---
    implementation(libs.caffeine)
    implementation(libs.spring.boot.starter.data.redis)

    // --- AWS ---
    implementation(libs.aws.s3)

    // --- OpenAPI ---
    implementation(libs.springdoc.openapi.ui)
    implementation(libs.jackson.module.kotlin)

    // --- Logging ---
    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)
    implementation(libs.slf4j.api)

    // --- Test ---
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertj.core)

    testImplementation(libs.javafaker) {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    // Snakeyaml explícito para testes, garantindo alinhamento de versão
    testImplementation(libs.snakeyaml)

    testImplementation(libs.rest.assured)
    testImplementation(libs.testcontainers.minio)
    testImplementation(libs.testcontainers.redis)
    testImplementation(libs.testcontainers.toxiproxy)
    testImplementation(libs.mockk)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}


liquibase {
    activities.register("compose") {
        this.arguments =
            mapOf(
                "searchPath" to "${project.projectDir}/lib/db/src/main",
                "changelogFile" to "resources/db/changelog/db-changelog.yaml",
            )
    }
    activities.register("main") {
        this.arguments =
            mapOf(
                "searchPath" to "${project.projectDir}/src/main/resources",
                "changelogFile" to "db/changelog/db-changelog.yaml",
                "url" to project.findProperty("postgres.url") as String?,
                "username" to project.findProperty("postgres.username") as String?,
                "password" to project.findProperty("postgres.password") as String?,
                "driver" to "org.postgresql.Driver",
            )
    }
    runList = "main"
}


jooq {
    version.set(libs.versions.jooq.get())
    edition.set(JooqEdition.OSS)
    configurations {
        create("public") {
            generateSchemaSourceOnCompilation.set(false)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.DEBUG
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isJavaTimeTypes = true
                        isPojos = false
                        isDaos = false
                        isInterfaces = false
                        isRelations = false
                        isComments = true
                        isValidationAnnotations = false
                        isKotlinNotNullRecordAttributes = true
                    }
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        excludes = "databasechangelog|databasechangeloglock"
                    }
                    target.apply {
                        packageName = "user"
                        directory = "src/main/kotlin/io/github/pedroermarinho/user/data/repositories/generated"
                    }
                }
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = project.findProperty("postgres.url") as String?
                    user = project.findProperty("postgres.username") as String?
                    password = project.findProperty("postgres.password") as String?
                }
            }
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
