package com.stviz.app.transaction

import kotlinx.serialization.Serializable

enum class TransactionType {
    BUY, SELL
}

@Serializable
data class TransactionResponse(
    val id: Long,
    val portfolioId: Long,
    val symbol: String,
    val sector: String,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val transactionDate: String,
    val notes: String?,
    val createdAt: String
)

@Serializable
data class TransactionRequest(
    val symbol: String,
    val sector: String,
    val type: TransactionType,
    val quantity: Double,
    val price: Double,
    val transactionDate: String,
    val notes: String? = null
)

@Serializable
data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val number: Int
)
