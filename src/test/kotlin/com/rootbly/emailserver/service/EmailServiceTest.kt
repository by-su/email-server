package com.rootbly.emailserver.service

import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.domain.EmailStatus
import com.rootbly.emailserver.dto.SendEmailRequest
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EmailServiceTest {

    private val emailRepository: EmailRepository = mockk()
    private val service = EmailService(emailRepository)

    private val request = SendEmailRequest(
        to = "receiver@example.com",
        subject = "테스트 제목",
        body = "<p>테스트 본문</p>",
    )

    @BeforeEach
    fun setUp() {
        every { emailRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `send는 PENDING 상태로 저장한다`() {
        val result = service.send(request)

        assertEquals(EmailStatus.PENDING, result.status)
        verify(exactly = 1) { emailRepository.save(any()) }
    }
}