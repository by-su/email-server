package com.rootbly.emailserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmailServerApplication

fun main(args: Array<String>) {
    runApplication<EmailServerApplication>(*args)
}
