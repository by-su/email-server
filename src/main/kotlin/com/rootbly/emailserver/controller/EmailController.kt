package com.rootbly.emailserver.controller

import com.rootbly.emailserver.dto.EmailResponse
import com.rootbly.emailserver.dto.SendEmailRequest
import com.rootbly.emailserver.service.EmailService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/emails")
class EmailController(
    private val emailService: EmailService,
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun sendEmail(@RequestBody @Valid request: SendEmailRequest): EmailResponse =
        EmailResponse.from(emailService.send(request))

}