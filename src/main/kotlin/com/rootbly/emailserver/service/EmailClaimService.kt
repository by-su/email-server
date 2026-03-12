package com.rootbly.emailserver.service

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.domain.EmailStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class EmailClaimService(private val emailRepository: EmailRepository) {

    @Transactional
    fun claimPendingEmails(): List<Email> {
        val pending = emailRepository.findAllByStatus(EmailStatus.PENDING) // SELECT FOR UPDATE
        if (pending.isEmpty()) return emptyList()
        pending.forEach { it.status = EmailStatus.PROCESSING }
        return emailRepository.saveAll(pending)
    }

    @Transactional
    fun updateStatus(email: Email, status: EmailStatus, sentAt: LocalDateTime?) {
        email.status = status
        email.sentAt = sentAt
        emailRepository.save(email)
    }
}