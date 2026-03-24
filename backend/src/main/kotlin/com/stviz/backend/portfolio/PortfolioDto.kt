package com.stviz.backend.portfolio

import jakarta.validation.constraints.NotBlank

data class CreatePortfolioRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null
)

data class UpdatePortfolioRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val description: String? = null
)

data class PortfolioResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)

fun PortfolioEntity.toResponse() = PortfolioResponse(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    updatedAt = updatedAt
)
