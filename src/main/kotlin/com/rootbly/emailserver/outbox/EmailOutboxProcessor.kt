package com.rootbly.emailserver.outbox

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailStatus
import com.rootbly.emailserver.service.EmailClaimService
import jakarta.mail.internet.MimeMessage
import org.owasp.html.HtmlPolicyBuilder
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class EmailOutboxProcessor(
    private val emailClaimService: EmailClaimService,
    private val mailSender: JavaMailSender,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val htmlPolicy = HtmlPolicyBuilder()
        .allowElements("p", "br", "b", "i", "u", "strong", "em", "ul", "ol", "li", "a")
        .allowUrlProtocols("https")
        .allowAttributes("href").onElements("a")
        .toFactory()

    // fixedDelay: 이전 실행이 끝난 후 5초 뒤에 다시 실행 (중복 처리 방지)
    @Scheduled(fixedDelay = 5000)
    fun process() {
        val claimed = emailClaimService.claimPendingEmails()
        claimed.forEach { processSingleEmail(it) }
    }

    private fun processSingleEmail(email: Email) {
        try {
            mailSender.send(createMimeMessage(email))
            emailClaimService.markAsSent(email)
        } catch (e: Exception) {
            log.error("Failed id=${email.id}: ${e.message}")
            emailClaimService.markAsFailed(email)
        }
    }

    private fun createMimeMessage(email: Email): MimeMessage {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, false, "UTF-8")
        helper.setFrom(email.fromAddress)
        helper.setTo(email.toAddress)
        helper.setSubject(email.subject)
        helper.setText(htmlPolicy.sanitize(email.body), true)
        return message
    }
}