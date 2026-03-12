package com.rootbly.emailserver.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
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

    @Column
    val platform: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EmailStatus = EmailStatus.PENDING,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.MIN,

    var sentAt: LocalDateTime? = null,
)

enum class EmailStatus { PENDING, PROCESSING, SENT, FAILED }
