package com.rootbly.emailserver.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "emails")
class Email(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val fromAddress: String,

    @Column(nullable = false)
    val toAddress: String,

    @Column(nullable = false)
    val subject: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val body: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EmailStatus = EmailStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var sentAt: LocalDateTime? = null,
)

enum class EmailStatus { PENDING, SENT, FAILED }
