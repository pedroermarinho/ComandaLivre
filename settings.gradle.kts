rootProject.name = "comanda-livre-monorepo"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// Mapeamento dos microsserviços
include(":services:comandalivre-service")
include(":services:company-service")
include(":services:user-service")
include(":services:prumodigital-service")
include(":services:api-gateway-service")
