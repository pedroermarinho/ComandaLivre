package io.github.pedroermarinho.comandalivreapi.shared.core.infra.util

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.exceptions.DataConversionException

private val log = KotlinLogging.logger {}

fun <T> errorDataConversion(onFunction: () -> T): Result<T> =
    runCatching { onFunction() }
        .fold(
            onSuccess = { Result.success(it) },
            onFailure = { e ->
                log.error(e) { "Não foi possível converter os dados: ${e.message}" }
                when (e) {
                    is KotlinNullPointerException -> {
                        return@fold Result.failure(DataConversionException("Erro ao converter dados: Dados nulos"))
                    }

                    is NullPointerException -> {
                        return@fold Result.failure(DataConversionException("Erro ao converter dados: Dados nulos"))
                    }

                    else -> {
                        return@fold Result.failure(DataConversionException("Erro ao converter dados: Erro desconhecido"))
                    }
                }
            },
        )
