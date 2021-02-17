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
    private var reportChatId: MutableList<Int?>? = mutableListOf()

    fun receive(message: Message) {
        try {
            logger.info("The message received:${message.subject} time: ${message.receivedDate}")
            when (message.subject.toString().toLowerCase()) {
                "report" -> {
                    val multiPart = message.content as Multipart
                    for (partIndex in 0 until multiPart.count) {
                        val part = multiPart.getBodyPart(partIndex) as MimeBodyPart
                        if (!part.contentType.contains("xlsx"))
                            continue
                        val report = InputFile(part.inputStream, part.fileName)
                        reportChatId?.forEach {
                            reportBot?.sendReport(chatId = it, report = report)
                        }
                    }
                }
                "error" -> {}
            }
            message.setFlag(Flags.Flag.SEEN, true)
        }catch (e: Exception){
            logger.error(e.toString())
        }
    }
}