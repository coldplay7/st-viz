package com.stviz.app.portfolio

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreatePortfolioRequest(
    val name: String,
    val description: String? = null
)
