package com.rootbly.emailserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class EmailServerApplication

fun main(args: Array<String>) {
    runApplication<EmailServerApplication>(*args)
}