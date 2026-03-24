package com.stviz.backend.portfolio

import jakarta.persistence.*
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

@Entity
@Table(name = "portfolios")
data class PortfolioEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var description: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)
