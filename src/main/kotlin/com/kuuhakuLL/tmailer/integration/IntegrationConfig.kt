package com.kuuhakuLL.tmailer.integration

import com.kuuhakuLL.tmailer.service.MessageReceiver
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.mail.dsl.Mail
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.IntegrationFlow
import java.net.URLEncoder
import java.nio.charset.Charset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.Message
import javax.mail.internet.MimeMessage
import org.springframework.integration.StaticMessageHeaderAccessor

@Configuration
@Slf4j
class IntegrationConfig {
    val logger: Logger = LoggerFactory.getLogger(IntegrationConfig::class.java)

    @Autowired
    private val messageReceive: MessageReceiver? = null

    @Value("\${mail.port}")
    private val port: Int? = null

    @Value("\${mail.email}")
    private val email: String? = null

    @Value("\${mail.password}")
    private val password: String? = null

    @Bean
    fun mailListener(): IntegrationFlow? {
        logger.info("Mail listen")
        return IntegrationFlows
            .from(
                Mail.imapInboundAdapter(imapUrl())
                    .searchTermStrategy(SearchUnreadMessagesStrategy())
                    .shouldMarkMessagesAsRead(false)
                    .shouldDeleteMessages(false)
                    .autoCloseFolder(false)
                    .javaMailProperties { p ->
                        p.put("mail.imaps.timeout", "300000")
                        p.put("mail.debug", "false")
                    }
            )
            { e -> e.autoStartup(true).poller { p -> p.fixedDelay(5000).maxMessagesPerPoll(1) } }
            .filter<MimeMessage>{ payload ->
                val subject = payload.subject?.toString()?.toLowerCase()
                subject == "report" || subject == "error"
            }
            .handle(messageReceive, "receive")
            .handle { payload: Message<MimeMessage> ->
                StaticMessageHeaderAccessor.getCloseableResource(payload)?.close()
            }
            .get()
    }

    private fun imapUrl() : String {
        val url ="imaps://${URLEncoder.encode(email, Charset.defaultCharset())}:" +
                "${URLEncoder.encode(password, Charset.defaultCharset())}@imap.yandex.ru:$port/inbox"
        return url
    }

}