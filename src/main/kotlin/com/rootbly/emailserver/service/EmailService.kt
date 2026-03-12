package com.rootbly.emailserver.service

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.dto.SendEmailRequest
import com.rootbly.emailserver.dto.SubtitleReadyEmailRequest
import com.rootbly.emailserver.template.SubtitleReadyEmailTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val emailRepository: EmailRepository,
    @Value("\${spring.mail.username}") private val mailUsername: String,
) {
    fun send(request: SendEmailRequest): Email =
        emailRepository.save(
            Email(
                fromAddress = mailUsername,
                toAddress = request.to,
                subject = request.subject,
                body = request.body,
                platform = request.platform,
            )
        )

    fun sendSubtitleReady(request: SubtitleReadyEmailRequest): Email =
        emailRepository.save(
            Email(
                fromAddress = mailUsername,
                toAddress = request.to,
                subject = "[RootblySub] 자막이 준비되었습니다",
                body = SubtitleReadyEmailTemplate.render(request.youtubeUrl, request.message),
                platform = request.platform,
            )
        )
}
