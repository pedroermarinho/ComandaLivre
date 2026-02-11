tasks.register("printLabStatus") {
    group = "engineering-lab"
    description = "Lista todos os micros serviços e seus status."
    
    doLast {
        println("\n --- Status do Laboratório Comanda Livre ---")
        val services = subprojects.filter { it.childProjects.isEmpty() }
        println("Total de Micro serviços: ${services.size}")
        services.forEach { service ->
            println("   - 📦 ${service.name.padEnd(25)}: ${service.description ?: "Sem descrição"} (v${service.version})")
        }
        println("-----------------------------------------------\n")
    }
}

tasks.register("buildImages") {
    group = "engineering-lab"
    description = "Gera as imagens Docker (Buildpacks) para todos os micro serviços."

    dependsOn(subprojects.mapNotNull { it.tasks.findByName("bootBuildImage") })
    
    doLast {
        println("\n🐳 --- Build de Imagens Concluído ---")
        println("As seguintes imagens foram geradas localmente:")
        subprojects.forEach { project ->
            if (project.plugins.hasPlugin("org.springframework.boot")) {
                println("   - comandalivre/${project.name}:latest")
            }
        }
        println("-----------------------------------------\n")
    }
}

tasks.register("createCluster") {
    group = "engineering-lab"
    description = "Cria o cluster Kind utilizando a configuração do projeto."
    
    doLast {
        println("\n🛠️ --- Criando Cluster Kind: comanda-livre ---")
        exec {
            commandLine("kind", "create", "cluster", "--config", "k8s/cluster/kind-config.yaml")
            isIgnoreExitValue = true 
        }
        println("✅ Cluster pronto para receber os serviços.\n")
    }
}

tasks.register("deleteCluster") {
    group = "engineering-lab"
    description = "Remove o cluster Kind 'comanda-livre'."
    
    doLast {
        println("\n🗑️ --- Deletando Cluster Kind ---")
        exec {
            commandLine("kind", "delete", "cluster", "--name", "comanda-livre")
        }
    }
}

tasks.register("loadImagesToKind"){
    group = "engineering-lab"
    description = "Carrega as Imagens Docker para o cluster Kind local."

    dependsOn("buildImages")
    dependsOn("createCluster")


    doLast{
        val cluster = "comanda-livre"
        val services = subprojects.filter { it.childProjects.isEmpty() && it.plugins.hasPlugin("org.springframework.boot") }

        println("\n🚚 --- Carregando Imagens no Kind ($cluster) ---")

        services.forEach { service ->
            val imageName = "comandalivre/${service.name}:latest"
            println("   - Carregando $imageName para o cluster $cluster...")
            exec {
                commandLine("kind", "load", "docker-image", imageName, "--name", cluster)
            }
        }
        println("--------------------------------------------------\n")
    }
}

tasks.register("ApplyDevOverlay"){
    group = "engineering-lab"
    description = "Aplica o overlay de desenvolvimento utilizando o Kustomize."

    dependsOn("createCluster")

    exec {
        commandLine("kubectl", "apply", "-k", "k8s/overlays/dev")
    }
}


tasks.register("deployDev") {
    group = "engineering-lab"
    description = "Ciclo completo: Build das imagens, carga no Kind e aplicação do Kustomize (Dev)."
    
    dependsOn("loadImagesToKind")

    doLast {
        println("\n☸️ --- Iniciando Deploy no Kubernetes (Overlay: Dev) ---")
        
        exec {
            commandLine("kubectl", "apply", "-k", "k8s/overlays/dev")
        }
        
        println("\n✅ Deploy finalizado com sucesso!")
        println("🔗 Acessos:")
        println("   - ComandaLivre: http://localhost:8080")
        println("   - Company:      http://localhost:8081")
        println("   - User:         http://localhost:8082")
        println("   - PrumoDigital: http://localhost:8083")
        println("   - Keycloak:     http://localhost:8090")
        println("   - MailHog:      http://localhost:8025")
        println("   - GRAFANA:      http://localhost:3000")
        println("   - GRAFANA:      http://localhost:3000")
        println("--------------------------------------------------\n")
    }
}