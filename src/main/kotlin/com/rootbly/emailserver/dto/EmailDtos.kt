package com.rootbly.emailserver.dto

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailStatus
import jakarta.validation.constraints.Email as EmailValid
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class SendEmailRequest(
    @field:EmailValid @field:NotBlank
    val from: String,

    @field:EmailValid @field:NotBlank
    val to: String,

    @field:NotBlank
    val subject: String,

    @field:NotBlank
    val body: String,
)

data class EmailResponse(
    val id: Long,
    val to: String,
    val subject: String,
    val status: EmailStatus,
    val createdAt: LocalDateTime,
    val sentAt: LocalDateTime?,
) {
    companion object {
        fun from(email: Email) = EmailResponse(
            id = email.id,
            to = email.toAddress,
            subject = email.subject,
            status = email.status,
            createdAt = email.createdAt,
            sentAt = email.sentAt,
        )
    }
}