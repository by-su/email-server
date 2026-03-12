package com.rootbly.emailserver.service

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.domain.EmailStatus
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EmailClaimServiceTest {

    private val emailRepository: EmailRepository = mockk()
    private val service = EmailClaimService(emailRepository)

    @BeforeEach
    fun setUp() {
        every { emailRepository.saveAll(any<List<Email>>()) } answers { firstArg() }
        every { emailRepository.save(any()) } answers { firstArg() }
    }

    private fun email(id: Long = 1L) = Email(
        id = id,
        toAddress = "receiver@example.com",
        subject = "제목",
        body = "<p>본문</p>",
    )

    @Test
    fun `PENDING이 없으면 빈 리스트를 반환하고 saveAll을 호출하지 않는다`() {
        every { emailRepository.findAllByStatusForUpdate(EmailStatus.PENDING) } returns emptyList()

        val result = service.claimPendingEmails()

        assertEquals(emptyList<Email>(), result)
        verify(exactly = 0) { emailRepository.saveAll(any<List<Email>>()) }
    }

    @Test
    fun `PENDING 이메일을 PROCESSING으로 변경하고 반환한다`() {
        val emails = listOf(email(1L), email(2L))
        every { emailRepository.findAllByStatusForUpdate(EmailStatus.PENDING) } returns emails

        val result = service.claimPendingEmails()

        assertEquals(2, result.size)
        result.forEach { assertEquals(EmailStatus.PROCESSING, it.status) }
        verify { emailRepository.saveAll(emails) }
    }

    @Test
    fun `markAsSent는 SENT 상태와 sentAt을 설정한다`() {
        val email = email()

        service.markAsSent(email)

        assertEquals(EmailStatus.SENT, email.status)
        assertNotNull(email.sentAt)
        verify { emailRepository.save(email) }
    }

    @Test
    fun `markAsFailed는 FAILED 상태로 설정하고 sentAt은 변경하지 않는다`() {
        val email = email()

        service.markAsFailed(email)

        assertEquals(EmailStatus.FAILED, email.status)
        assertNull(email.sentAt)
        verify { emailRepository.save(email) }
    }
}