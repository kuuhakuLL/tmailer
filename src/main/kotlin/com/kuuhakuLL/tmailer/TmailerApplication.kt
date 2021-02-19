package com.kuuhakuLL.tmailer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TmailerApplication{
	companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			runApplication<TmailerApplication>(*args)
		}
	}
}

