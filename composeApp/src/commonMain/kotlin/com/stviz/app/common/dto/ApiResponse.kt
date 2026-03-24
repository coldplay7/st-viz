package com.stviz.app.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val data: T? = null,
    val error: String? = null,
    val timestamp: String? = null
)
