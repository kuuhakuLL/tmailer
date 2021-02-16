package com.kuuhakuLL.tmailer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.InputFile
import javax.mail.internet.MimeBodyPart
import javax.mail.*
import java.util.ArrayList

@Service
class MassageReceiver {
    @Autowired
    private val reportBot: ReportBot? = null

    @Value("\${chats.report}")
    private var reportChatId: MutableList<Int?>? = mutableListOf()

    fun receive(message: Message) {
        try {
            when (message.subject.toString().toLowerCase()) {
                "report" -> {
                    val multiPart = message.content as Multipart
                    for (partIndex in 0 until multiPart.count) {
                        val part = multiPart.getBodyPart(partIndex) as MimeBodyPart
                        if (!part.contentType.contains("xlsx"))
                            continue
                        val report = InputFile(part.inputStream, part.fileName)
                        reportChatId?.forEach {reportBot?.sendReport(chatId = it, report = report)}
                    }
                }
                "error" -> {}
            }
            message.setFlag(Flags.Flag.SEEN, true)
        }catch (e: Exception){}
    }
}