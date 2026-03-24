package com.stviz.backend.common.exception

import org.springframework.http.HttpStatus

open class AppException(
    val status: HttpStatus,
    message: String
) : RuntimeException(message)

class NotFoundException(message: String) : AppException(HttpStatus.NOT_FOUND, message)

class ValidationException(message: String) : AppException(HttpStatus.BAD_REQUEST, message)

class UnauthorizedException(message: String) : AppException(HttpStatus.UNAUTHORIZED, message)
