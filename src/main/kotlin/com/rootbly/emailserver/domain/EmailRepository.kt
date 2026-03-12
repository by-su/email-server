package com.rootbly.emailserver.domain

import org.springframework.data.jpa.repository.JpaRepository

interface EmailRepository : JpaRepository<Email, Long> {
    fun findAllByStatus(status: EmailStatus): List<Email>
}