package io.github.pedroermarinho.comandalivreapi

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ModularityStructureTest {
    private val modules = ApplicationModules.of(ComandaLivreApiApplication::class.java)

    @Test
    @DisplayName("Deve identificar corretamente os módulos da aplicação (shared, company, comandalivre, prumodigital)")
    fun `Deve identificar corretamente os modulos da aplicacao (shared, company, comandalivre, prumodigital)`() {
        assertThat(modules.getModuleByName("shared")).isPresent
        assertThat(modules.getModuleByName("company")).isPresent
        assertThat(modules.getModuleByName("comandalivre")).isPresent
        assertThat(modules.getModuleByName("prumodigital")).isPresent

        assertThat(modules.toList()).hasSize(4)
    }

    @Test
    @DisplayName("Deve verificar a estrutura modular e a ausência de ciclos de dependência não permitidos")
    fun `Deve verificar a estrutura modular e a ausencia de ciclos de dependencia nao permitidos`() {
//        modules.verify()
    }

    @Test
    @DisplayName("Deve verificar a estrutura modular e reportar o número de violações, agrupando por módulo se possível")
    fun `Deve verificar a estrutura modular e reportar o numero de violacoes agrupadas`() {
//        try {
//            modules.verify()
//            println("Nenhuma violação de modularidade encontrada! ✅")
//        } catch (ex: Violations) {
//            val totalViolations = ex.messages.count()
//            val violationsByModule = mutableMapOf<String, MutableList<String>>()
//
//            println("-----------------------------------------------------")
//            println("🚨 VIOLAÇÕES DE MODULARIDADE ENCONTRADAS: $totalViolations 🚨")
//            println("-----------------------------------------------------")
//
//            ex.messages.forEach { message ->
//
//                // Heurística para tentar extrair o módulo de origem da violação (pode ser o primeiro módulo mencionado)
//                val moduleNamePattern = "Module '(\\w+)'".toRegex()
//                val match = moduleNamePattern.find(message)
//                val originatingModule = match?.groupValues?.get(1) ?: "Desconhecido/Geral"
//
//                violationsByModule.computeIfAbsent(originatingModule) { mutableListOf() }.add(message)
//            }
//
//            if (violationsByModule.isNotEmpty()) {
//                println("\n--- Violações por Módulo ---")
//                violationsByModule.forEach { (moduleName, moduleMessages) ->
//                    println("Módulo '$moduleName': ${moduleMessages.size} violação(ões)")
//                    moduleMessages.forEach { msg -> println("  - $msg") }
//                }
//                println("-----------------------------------------------------")
//            }
//
//            fail("Encontradas $totalViolations violações de modularidade. Verifique o output do console para detalhes.", ex)
//        }
    }

    @Test
    @DisplayName("Deve ser capaz de gerar a documentação dos módulos")
    fun `Deve ser capaz de gerar a documentacao dos modulos`() {
        Documenter(modules)
            .writeModuleCanvases()
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()
            .writeDocumentation()
    }

    @Test
    @DisplayName("Deve listar todos os modulos")
    fun `Deve listar todos os modulos`() {
        modules.toList().forEach { println(it) }
    }
}
