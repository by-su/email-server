package com.rootbly.emailserver.domain

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface EmailRepository : JpaRepository<Email, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Email e WHERE e.status = :status")
    fun findAllByStatusForUpdate(status: EmailStatus): List<Email>
}