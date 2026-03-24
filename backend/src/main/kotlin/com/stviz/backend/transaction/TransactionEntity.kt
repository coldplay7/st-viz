package com.stviz.backend.transaction

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class TransactionType {
    BUY, SELL
}

@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "portfolio_id", nullable = false)
    val portfolioId: Long,

    @Column(nullable = false)
    val symbol: String,

    @Column(nullable = false)
    val sector: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    val type: TransactionType,

    @Column(nullable = false)
    val quantity: Double,

    @Column(nullable = false)
    val price: Double,

    @Column(name = "transaction_date", nullable = false)
    val transactionDate: String,

    @Column(nullable = true)
    val notes: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)
