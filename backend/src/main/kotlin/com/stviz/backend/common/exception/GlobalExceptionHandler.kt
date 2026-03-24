package com.stviz.backend.common.exception

import com.stviz.backend.common.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("Application error: {}", ex.message)
        return ResponseEntity
            .status(ex.status)
            .body(ApiResponse(error = ex.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val errorMessage = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        
        log.warn("Validation error: {}", errorMessage)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(error = errorMessage))
    }

    @ExceptionHandler(Exception::class)
    fun handleAllOtherExceptions(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Unhandled exception", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(error = "An unexpected error occurred"))
    }
}
