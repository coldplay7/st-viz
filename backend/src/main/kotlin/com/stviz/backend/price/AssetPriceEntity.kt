package com.stviz.backend.price

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
@Table(name = "asset_prices")
data class AssetPriceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "portfolio_id", nullable = false)
    val portfolioId: Long,

    @Column(nullable = false)
    val symbol: String,

    @Column(name = "current_price", nullable = false)
    var currentPrice: Double,

    @Column(name = "last_updated", nullable = false)
    var lastUpdated: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)
