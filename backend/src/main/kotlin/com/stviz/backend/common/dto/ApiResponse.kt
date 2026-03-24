package com.stviz.backend.common.dto

data class ApiResponse<T>(
    val data: T? = null,
    val error: String? = null,
    val success: Boolean = error == null
)
