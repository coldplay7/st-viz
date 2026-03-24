package com.stviz.backend.transaction

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class TransactionRequest(
    @field:NotBlank(message = "Symbol is required")
    val symbol: String,

    @field:NotBlank(message = "Sector is required")
    val sector: String,

    @field:NotNull(message = "Transaction type is required")
    val type: TransactionType,

    @field:DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    val quantity: Double,

    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    val price: Double,

    @field:NotBlank(message = "Transaction date is required")
    val transactionDate: String,

    val notes: String? = null
)

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

fun TransactionEntity.toResponse() = TransactionResponse(
    id = id,
    portfolioId = portfolioId,
    symbol = symbol,
    sector = sector,
    type = type,
    quantity = quantity,
    price = price,
    transactionDate = transactionDate,
    notes = notes,
    createdAt = createdAt
)
