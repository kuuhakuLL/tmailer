package com.kuuhakuLL.tmailer.service

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Slf4j
class ReportBot: TelegramLongPollingBot() {
    val logger: Logger = LoggerFactory.getLogger(ReportBot::class.java)

    @Value("\${telegram.name}")
    private val botName: String? = null

    @Value("\${telegram.token}")
    private val token: String? = null

    fun sendReport(chatId: Int?, report: InputFile){
        logger.info("Send message telegram chat:$chatId ${report.attachName}")
        val document = SendDocument(chatId.toString(), report)
        execute(document)
    }

    override fun onUpdateReceived(update: Update) {}

    override fun getBotUsername() = botName

    override fun getBotToken() = token

}