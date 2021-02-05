package com.kuuhakuLL.tmailer.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ReportBot: TelegramLongPollingBot() {

    @Value("\${telegram.name}")
    private val botName: String? = null

    @Value("\${telegram.token}")
    private val token: String? = null

    fun sendReport(chatId: Int?, report: InputFile){
        val document = SendDocument(chatId.toString(), report)
        execute(document)
    }

    override fun onUpdateReceived(update: Update) {}

    override fun getBotUsername() = botName

    override fun getBotToken() = token

}