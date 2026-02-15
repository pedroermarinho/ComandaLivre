import nu.studer.gradle.jooq.JooqEdition
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.buildpack.platform.build.PullPolicy

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.spring") version "2.3.10"
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "9.0"
    id("org.liquibase.gradle") version "2.2.2"
    id("com.diffplug.spotless") version "7.0.4"
}

group = "io.github.pedroermarinho"
version = "0.0.1-SNAPSHOT"

val versions =
    mapOf(
        "kotlinCoroutines" to "1.10.2",
        "springdoc" to "2.8.5",
        "caffeine" to "3.2.0",
        "firebase" to "9.4.3",
        "jooq" to "3.19.15",
        "picocli" to "4.7.6",
        "snakeyaml" to "2.4",
        "slf4j" to "2.0.17",
        "logback" to "1.5.27",
        "kotlinLogging" to "7.0.14",
        "awsSdk" to "2.31.0",
        "springDotenv" to "4.0.0",
        "uuidCreator" to "6.1.1",
        "springModulith" to "1.4.0",
        "assertjCore" to "3.25.3",
        "javafaker" to "1.0.2",
        "mockitoKotlin" to "5.3.1",
        "adminServer" to "3.5.0",
        "restAssured" to "5.5.6",
        "mockk" to "1.14.6",
    )

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":libs:shared-common"))

    // Core Spring
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.data:spring-data-commons")

    // Spring Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core:${versions["springModulith"]}")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test:${versions["springModulith"]}")
    implementation("org.springframework.modulith:spring-modulith-actuator:${versions["springModulith"]}")
    implementation("org.springframework.modulith:spring-modulith-docs:${versions["springModulith"]}")
    implementation("org.springframework.modulith:spring-modulith-events-api:${versions["springModulith"]}")
    implementation("org.springframework.modulith:spring-modulith-starter-jdbc:${versions["springModulith"]}")

    // Spring Extras
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("me.paulschwarz:spring-dotenv:${versions["springDotenv"]}")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Kotlin + Coroutines
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions["kotlinCoroutines"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${versions["kotlinCoroutines"]}")
    implementation("com.nimbusds:nimbus-jose-jwt:10.5")

    // Database: PostgreSQL, JOOQ, Liquibase
    implementation("com.github.f4b6a3:uuid-creator:${versions["uuidCreator"]}")
    implementation("org.postgresql:postgresql")
    implementation("org.jooq:jooq:${versions["jooq"]}")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.liquibase:liquibase-core")

    jooqGenerator("org.jooq:jooq-meta:${versions["jooq"]}")
    jooqGenerator("org.jooq:jooq-codegen:${versions["jooq"]}")
    jooqGenerator("org.jooq:jooq-meta-extensions:${versions["jooq"]}")
    jooqGenerator("org.postgresql:postgresql")

    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.yaml:snakeyaml:${versions["snakeyaml"]}")
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("info.picocli:picocli:${versions["picocli"]}")

    // Cache
    implementation("com.github.ben-manes.caffeine:caffeine:${versions["caffeine"]}")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Firebase
    implementation("com.google.firebase:firebase-admin:${versions["firebase"]}")

    // AWS
    implementation("software.amazon.awssdk:s3:${versions["awsSdk"]}")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${versions["springdoc"]}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Logging
    implementation("ch.qos.logback:logback-classic:${versions["logback"]}")
    implementation("io.github.oshai:kotlin-logging-jvm:${versions["kotlinLogging"]}")
    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.mockito.kotlin:mockito-kotlin:${versions["mockitoKotlin"]}")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.assertj:assertj-core:${versions["assertjCore"]}")
    testImplementation("com.github.javafaker:javafaker:${versions["javafaker"]}") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    testImplementation("org.yaml:snakeyaml:${versions["snakeyaml"]}")

    testImplementation("io.rest-assured:rest-assured:${versions["restAssured"]}")
    testImplementation("org.testcontainers:minio:1.19.5")
    testImplementation("com.redis.testcontainers:testcontainers-redis:1.6.4")
    testImplementation("org.testcontainers:toxiproxy:1.21.3")
    testImplementation("io.mockk:mockk:${versions["mockk"]}")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Configuração do Liquibase
liquibase {
    activities.register("compose") {
        this.arguments =
            mapOf(
                "classpath" to "${rootProject.rootDir}/lib/db/src/main",
                "changelogFile" to "resources/db/changelog/db-changelog.yaml",
            )
    }
    activities.register("main") {
        this.arguments =
            mapOf(
                "classpath" to "${rootProject.rootDir}/src/main/resources",
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
    version.set(versions["jooq"])
    edition.set(JooqEdition.OSS)
    configurations {
        create("shared") {
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
                        packageName = "shared"
                        directory = "src/main/kotlin/io/github/pedroermarinho/comandalivreapi/shared/core/data/repositories/generated"
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

        create("company") {
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
                        inputSchema = "company"
                    }
                    target.apply {
                        packageName = "company"
                        directory = "src/main/kotlin/io/github/pedroermarinho/comandalivreapi/company/core/data/repositories/generated"
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

        create("comandaLivre") {
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
                        inputSchema = "comandalivre"
                    }
                    target.apply {
                        packageName = "comandalivre"
                        directory = "src/main/kotlin/io/github/pedroermarinho/comandalivreapi/comandalivre/core/data/repositories/generated"
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

        create("prumoDigital") {
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
                        inputSchema = "prumodigital"
                    }
                    target.apply {
                        packageName = "prumodigital"
                        directory = "src/main/kotlin/io/github/pedroermarinho/comandalivreapi/prumodigital/core/data/repositories/generated"
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

spotless {
    kotlin {
        ktlint()
        target("src/**/*.kt")
    }
    kotlinGradle {
        ktlint()
        target("*.gradle.kts")
    }
}

tasks.register("generateAllJooq") {
    group = "jOOQ"
    description = "Gera as classes jOOQ para todos os schemas configurados."
    dependsOn(
        "update",
        "generateSharedJooq",
        "generateCompanyJooq",
        "generateComandaLivreJooq",
        "generatePrumoDigitalJooq",
    )
}

sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/kotlin")
        resources.srcDir("src/integrationTest/resources")
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += output + compileClasspath
    }
    create("developmentTest") {
        java.srcDir("src/developmentTest/kotlin")
        resources.srcDir("src/developmentTest/resources")
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += output + compileClasspath
    }
}

configurations {
    val integrationTestImplementation by getting {
        extendsFrom(configurations.testImplementation.get())
    }
    val integrationTestRuntimeOnly by getting {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
    val developmentTestImplementation by getting {
        extendsFrom(configurations.getByName("integrationTestImplementation"))
    }
    val developmentTestRuntimeOnly by getting {
        extendsFrom(configurations.getByName("integrationTestRuntimeOnly"))
    }
}

tasks.named<Test>("test") {
    description = "Executa os testes de unidade (do sourceSet 'test')."
    group = "test"
    useJUnitPlatform {
        excludeTags("integration", "development")
    }
}

tasks.register<Test>("integrationTest") {
    description = "Executa os testes de integração (do sourceSet 'integrationTest')."
    group = "test"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform {
        includeTags("integration")
    }
    shouldRunAfter(tasks.test)
}

tasks.register<Test>("developmentTest") {
    description = "Executa os testes de desenvolvimento (do sourceSet 'developmentTest')."
    group = "test"
    testClassesDirs = sourceSets["developmentTest"].output.classesDirs
    classpath = sourceSets["developmentTest"].runtimeClasspath
    useJUnitPlatform {
        includeTags("development")
    }
    shouldRunAfter(tasks.test)
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("comandalivre/app-legacy-service:latest")
    pullPolicy.set(PullPolicy.IF_NOT_PRESENT)
    environment.set(mapOf("BP_JVM_VERSION" to "21"))
    publish.set(false)
}
