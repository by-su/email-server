package com.rootbly.emailserver.outbox

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.domain.EmailStatus
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EmailOutboxProcessor(
    private val emailRepository: EmailRepository,
    private val mailSender: JavaMailSender,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    // fixedDelay: 이전 실행이 끝난 후 5초 뒤에 다시 실행 (중복 처리 방지)
    @Scheduled(fixedDelay = 5000)
    fun process() {
        val pending = emailRepository.findAllByStatus(EmailStatus.PENDING)
        if (pending.isEmpty()) return

        log.info("Processing ${pending.size} pending email(s)")
        pending.forEach { processSingleEmail(it) }
    }

    private fun processSingleEmail(email: Email) {

        try {
            val message = createMimeMessage(email)
            mailSender.send(message)
            email.status = EmailStatus.SENT
            email.sentAt = LocalDateTime.now()
            emailRepository.save(email)
        } catch (e: Exception) {
            email.status = EmailStatus.FAILED
            emailRepository.save(email)
            log.error("Failed to send email id=${email.id} to=${email.toAddress}: ${e.message}")
        }
    }

    private fun createMimeMessage(email: Email): MimeMessage {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, false, "UTF-8")
        helper.setFrom(email.fromAddress)
        helper.setTo(email.toAddress)
        helper.setSubject(email.subject)
        helper.setText(email.body, true)
        return message
    }
}