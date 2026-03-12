package com.rootbly.emailserver.domain

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface EmailRepository : JpaRepository<Email, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findAllByStatus(status: EmailStatus): List<Email>
}