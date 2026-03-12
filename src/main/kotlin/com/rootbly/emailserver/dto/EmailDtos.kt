package com.rootbly.emailserver.dto

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailStatus
import jakarta.validation.constraints.Email as EmailValid
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class SendEmailRequest(
    @field:EmailValid @field:NotBlank
    val to: String,

    @field:NotBlank
    val subject: String,

    @field:NotBlank
    val body: String,

    val platform: String? = null,
)

data class SubtitleReadyEmailRequest(
    @field:EmailValid @field:NotBlank
    val to: String,

    @field:NotBlank
    val youtubeUrl: String,

    @field:NotBlank
    val message: String,

    val platform: String? = null,
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