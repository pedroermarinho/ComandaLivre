rootProject.name = "comanda-livre-monorepo"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":libs:shared-common")

// Mapeamento dos microsserviços
include(":services:comandalivre-service")
include(":services:company-service")
include(":services:user-service")
include(":services:prumodigital-service")
include(":services:api-gateway-service")
include(":services:app-legacy-service")
