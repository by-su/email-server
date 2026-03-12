package com.rootbly.emailserver.service

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.dto.SendEmailRequest
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailRepository: EmailRepository,
) {
    fun send(request: SendEmailRequest): Email =
        emailRepository.save(
            Email(
                fromAddress = request.from,
                toAddress = request.to,
                subject = request.subject,
                body = request.body,
            )
        )
}