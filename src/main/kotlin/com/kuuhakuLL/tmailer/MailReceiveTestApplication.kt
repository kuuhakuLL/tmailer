package com.kuuhakuLL.tmailer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MailReceiveTestApplication

fun main(args: Array<String>) {
	runApplication<MailReceiveTestApplication>(*args)
}
