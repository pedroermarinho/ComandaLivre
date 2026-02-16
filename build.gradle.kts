
subprojects {
    repositories {
        mavenCentral()
    }
}

fun runCommand(vararg args: String, ignoreError: Boolean = false) {
    val process = ProcessBuilder(*args)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()

    val exitCode = process.waitFor()

    if (exitCode != 0 && !ignoreError) {
        throw RuntimeException("❌ O comando falhou: ${args.joinToString(" ")}")
    }
}

// --- 📋 TASKS DO LABORATÓRIO ---

tasks.register("printLabStatus") {
    group = "engineering-lab"
    description = "Lista todos os micros serviços e seus status."

    doLast {
        println("\n --- Status do Laboratório Comanda Livre ---")
        val services = subprojects.filter { it.childProjects.isEmpty() }
        println("Total de Micro serviços: ${services.size}")
        services.forEach { service ->
            println("   - 📦 ${service.name.padEnd(25)} (v${service.version})")
        }
        println("-----------------------------------------------\n")
    }
}

tasks.register("buildImages") {
    group = "engineering-lab"
    description = "Gera as imagens Docker para serviços Spring Boot."

    val bootProjects = subprojects.filter { it.plugins.hasPlugin("org.springframework.boot") && !it.path.startsWith(":libs")}
    dependsOn(bootProjects.map { "${it.path}:bootBuildImage" })

    doLast {
        println("\n🐳 --- Build de Imagens Concluído ---")
    }
}

tasks.register("createCluster") {
    group = "engineering-lab"
    description = "Cria o cluster Kind e instala controladores."

    doLast {
        println("\n🛠️ --- Criando Cluster Kind: comanda-livre ---")

        runCommand("kind", "create", "cluster", "--config", "k8s/cluster/kind-config.yaml", ignoreError = true)

        println("✅ Cluster verificado.\n")
        println("\n📦 --- Instalando CloudNativePG Operator ---")

        runCommand("kubectl", "apply", "--server-side", "-f", "https://raw.githubusercontent.com/cloudnative-pg/cloudnative-pg/release-1.28/releases/cnpg-1.28.1.yaml")

        println("✅ CNPG Operator instalado com sucesso.\n")
    }
}

tasks.register("deleteCluster") {
    group = "engineering-lab"
    description = "Remove o cluster Kind."

    doLast {
        println("\n🗑️ --- Deletando Cluster Kind ---")
        runCommand("kind", "delete", "cluster", "--name", "comanda-livre", ignoreError = true)
    }
}

tasks.register("loadImagesToKind") {
    group = "engineering-lab"
    description = "Carrega as imagens locais para o Kind."

    dependsOn("buildImages")

    doLast {
        val cluster = "comanda-livre"
        val services = subprojects.filter { it.plugins.hasPlugin("org.springframework.boot") && !it.path.startsWith(":libs") }

        println("\n🚚 --- Carregando Imagens no Kind ($cluster) ---")

        services.forEach { service ->
            val imageName = "comandalivre/${service.name}:latest"
            println("   - Carregando $imageName ...")
            runCommand("kind", "load", "docker-image", imageName, "--name", cluster)
        }
        println("--------------------------------------------------\n")
    }
}

tasks.register("ApplyDevOverlay") {
    group = "engineering-lab"
    description = "Aplica os manifestos K8s (Overlay Dev)."

    doLast {
        runCommand("kubectl", "apply", "-k", "k8s/overlays/dev")
    }
}

tasks.register("deployDev") {
    group = "engineering-lab"
    description = "Ciclo completo: Load Images -> Apply K8s."

    dependsOn("loadImagesToKind")

    doLast {
        println("\n☸️ --- Iniciando Deploy no Kubernetes ---")

        runCommand("kubectl", "apply", "-k", "k8s/overlays/dev")

        println("--------------------------------------------------\n")
    }
}
