package com.rootbly.emailserver.outbox

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailStatus
import com.rootbly.emailserver.service.EmailClaimService
import io.mockk.*
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender

class EmailOutboxProcessorTest {

    private val emailClaimService: EmailClaimService = mockk()
    private val mailSender: JavaMailSender = mockk()
    private val processor = EmailOutboxProcessor(emailClaimService, mailSender)

    private val mimeMessage = mockk<MimeMessage>(relaxed = true)

    @BeforeEach
    fun setUp() {
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { emailClaimService.markAsSent(any()) } just Runs
        every { emailClaimService.markAsFailed(any()) } just Runs
    }

    private fun email(id: Long = 1L) = Email(
        id = id,
        fromAddress = "sender@gmail.com",
        toAddress = "receiver@example.com",
        subject = "제목",
        body = "<p>본문</p>",
    )

    @Test
    fun `PENDING이 없으면 발송하지 않는다`() {
        every { emailClaimService.claimPendingEmails() } returns emptyList()

        processor.process()

        verify(exactly = 0) { mailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `발송 성공 시 markAsSent를 호출한다`() {
        val email = email()
        every { emailClaimService.claimPendingEmails() } returns listOf(email)
        every { mailSender.send(any<MimeMessage>()) } just Runs

        processor.process()

        verify { emailClaimService.markAsSent(email) }
        verify(exactly = 0) { emailClaimService.markAsFailed(any()) }
    }

    @Test
    fun `발송 실패 시 예외가 전파되지 않고 markAsFailed를 호출한다`() {
        val email = email()
        every { emailClaimService.claimPendingEmails() } returns listOf(email)
        every { mailSender.send(any<MimeMessage>()) } throws RuntimeException("발송 실패")

        processor.process()

        verify { emailClaimService.markAsFailed(email) }
        verify(exactly = 0) { emailClaimService.markAsSent(any()) }
    }

    @Test
    fun `첫 번째 이메일 발송 실패 시 두 번째 이메일은 계속 처리된다`() {
        val email1 = email(id = 1L)
        val email2 = email(id = 2L)
        every { emailClaimService.claimPendingEmails() } returns listOf(email1, email2)
        every { mailSender.send(any<MimeMessage>()) } throws RuntimeException("실패") andThenJust Runs

        processor.process()

        verify { emailClaimService.markAsFailed(email1) }
        verify { emailClaimService.markAsSent(email2) }
    }
}