package com.rootbly.emailserver.service

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.domain.EmailStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmailClaimService(private val emailRepository: EmailRepository) {

    @Transactional
    fun claimPendingEmails(): List<Email> {
        val pending = emailRepository.findAllByStatusForUpdate(EmailStatus.PENDING)
        if (pending.isEmpty()) return emptyList()
        pending.forEach { it.status = EmailStatus.PROCESSING }
        return emailRepository.saveAll(pending)
    }

    @Transactional
    fun markAsSent(email: Email) {
        email.status = EmailStatus.SENT
        email.sentAt = LocalDateTime.now()
        emailRepository.save(email)
    }

    @Transactional
    fun markAsFailed(email: Email) {
        email.status = EmailStatus.FAILED
        emailRepository.save(email)
    }
}