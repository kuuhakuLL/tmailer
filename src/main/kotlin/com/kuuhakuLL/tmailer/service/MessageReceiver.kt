package com.kuuhakuLL.tmailer.service

import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.InputFile
import javax.mail.internet.MimeBodyPart
import javax.mail.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Service
@Slf4j
class MessageReceiver {
    val logger: Logger = LoggerFactory.getLogger(MessageReceiver::class.java)

    @Autowired
    private val reportBot: ReportBot? = null

    @Value("\${chats.report}")
    private lateinit var reportChatsId: MutableList<Int>

    @Value("\${chats.error}")
    private lateinit var errorChatsId: MutableList<Int>

    fun receive(message: Message) {
        try {
            logger.info("The message received:${message.subject} time: ${message.receivedDate}")
            var file = InputFile()
            var sendChatsId: MutableList<Int> = mutableListOf()
            when (message.subject.toString().toLowerCase()) {
                "report" -> {
                    file = message.fetchMediaFile("xlsx")
                    sendChatsId = reportChatsId
                }
                "error" -> {
                    file = message.fetchMediaFile("json")
                    sendChatsId = errorChatsId
                }
            }
            sendChatsId.forEach { reportBot?.sendFile(chatId = it, report = file) }
            message.setFlag(Flags.Flag.SEEN, true)
        }catch (e: Exception){
            logger.error(e.toString())
        }
    }

    private fun Message.fetchMediaFile(type: String): InputFile{
        val multiPart = this.content as Multipart
        var file = InputFile()
        for (partIndex in 0 until multiPart.count) {
            val part = multiPart.getBodyPart(partIndex) as MimeBodyPart
            if (!part.contentType.contains(type))
                continue
            file = InputFile(part.inputStream, part.fileName)
        }
        return file
    }
}