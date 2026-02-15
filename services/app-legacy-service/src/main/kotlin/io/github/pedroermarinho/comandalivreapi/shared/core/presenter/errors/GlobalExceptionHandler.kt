package io.github.pedroermarinho.comandalivreapi.shared.core.presenter.errors

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.shared.dtos.error.ErrorDTO
import io.github.pedroermarinho.shared.dtos.error.FieldErrorDto
import io.github.pedroermarinho.shared.exceptions.BusinessLogicException
import io.github.pedroermarinho.shared.exceptions.NotFoundException
import io.github.pedroermarinho.shared.exceptions.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException): ResponseEntity<ErrorDTO> {
        log.info { "NotFoundException: ${exception.message}" }
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.message ?: "Recurso não encontrado")
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(exception: UnauthorizedException): ResponseEntity<ErrorDTO> {
        log.info { "UnauthorizedException: ${exception.message}" }
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.message ?: "Não autorizado")
    }

    @ExceptionHandler(BusinessLogicException::class)
    fun handleBusinessException(exception: BusinessLogicException): ResponseEntity<ErrorDTO> {
        log.info { "BusinessLogicException: ${exception.message}" }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.message ?: "Requisição inválida")
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception): ResponseEntity<ErrorDTO> {
        log.error(exception) { "Erro interno do servidor" }
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor")
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorDTO> {
        val errors =
            exception.bindingResult.allErrors.map { error ->
                val fieldName = (error as FieldError).field
                val errorMessage = error.defaultMessage ?: "Erro desconhecido"
                FieldErrorDto(fieldName, errorMessage)
            }

        val detail = "Um ou mais campos estão inválidos. Por favor, corrija-os e tente novamente."
        val fieldErrorDTO =
            ErrorDTO(
                status = HttpStatus.BAD_REQUEST.value(),
                message = detail,
                details = HttpStatus.BAD_REQUEST.reasonPhrase,
                errors = errors,
            )

        log.info { "MethodArgumentNotValidException: $detail - Errors: $errors" }

        return ResponseEntity(fieldErrorDTO, HttpStatus.BAD_REQUEST)
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ErrorDTO> {
        val errorDTO =
            ErrorDTO(
                status = status.value(),
                message = message,
            )
        return ResponseEntity.status(status).body(errorDTO)
    }
}
