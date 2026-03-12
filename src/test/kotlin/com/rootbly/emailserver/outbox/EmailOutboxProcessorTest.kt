package com.rootbly.emailserver.outbox

import com.rootbly.emailserver.domain.Email
import com.rootbly.emailserver.domain.EmailRepository
import com.rootbly.emailserver.domain.EmailStatus
import io.mockk.*
import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender

class EmailOutboxProcessorTest {

    private val emailRepository: EmailRepository = mockk()
    private val mailSender: JavaMailSender = mockk()
    private val processor = EmailOutboxProcessor(emailRepository, mailSender)

    private val mimeMessage = mockk<MimeMessage>(relaxed = true)

    @BeforeEach
    fun setUp() {
        every { mailSender.createMimeMessage() } returns mimeMessage
        every { emailRepository.save(any()) } answers { firstArg() }
    }

    private fun email(id: Long = 1L) = Email(
        id = id,
        fromAddress = "sender@gmail.com",
        toAddress = "receiver@example.com",
        subject = "제목",
        body = "<p>본문</p>",
    )

    @Test
    fun `PENDING이 없으면 save를 호출하지 않는다`() {
        every { emailRepository.findAllByStatus(EmailStatus.PENDING) } returns emptyList()

        processor.process()

        verify(exactly = 0) { emailRepository.save(any()) }
    }

    @Test
    fun `발송 성공 시 SENT 상태로 저장된다`() {
        every { emailRepository.findAllByStatus(EmailStatus.PENDING) } returns listOf(email())
        every { mailSender.send(any<MimeMessage>()) } just Runs

        processor.process()

        verify { emailRepository.save(match { it.status == EmailStatus.SENT }) }
        verify { emailRepository.save(match { it.sentAt != null }) }
    }

    @Test
    fun `발송 실패 시 예외가 전파되지 않고 FAILED로 저장된다`() {
        every { emailRepository.findAllByStatus(EmailStatus.PENDING) } returns listOf(email())
        every { mailSender.send(any<MimeMessage>()) } throws RuntimeException("발송 실패")

        processor.process()

        verify { emailRepository.save(match { it.status == EmailStatus.FAILED }) }
    }

    @Test
    fun `첫 번째 이메일 발송 실패 시 두 번째 이메일은 계속 처리된다`() {
        every { emailRepository.findAllByStatus(EmailStatus.PENDING) } returns listOf(email(id=1L), email(id=2L))
        every { mailSender.send(any<MimeMessage>()) } throws RuntimeException("실패") andThenJust Runs

        processor.process()

        verify { emailRepository.save(match { it.id == 1L && it.status == EmailStatus.FAILED }) }
        verify { emailRepository.save(match { it.id == 2L && it.status == EmailStatus.SENT }) }
    }

}