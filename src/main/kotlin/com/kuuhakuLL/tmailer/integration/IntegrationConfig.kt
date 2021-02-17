package com.kuuhakuLL.tmailer.integration

import com.kuuhakuLL.tmailer.service.MessageReceiver
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.*
import org.springframework.integration.mail.dsl.Mail
import org.springframework.integration.support.PropertiesBuilder
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.messaging.MessageHeaders
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
                Mail.imapInboundAdapter(
                "imaps://${URLEncoder.encode(email, Charset.defaultCharset())}:"
                    +"${URLEncoder.encode(password, Charset.defaultCharset())}@imap.yandex.ru:$port/inbox"
                )
                .searchTermStrategy(SearchUnreadMessagesStrategy())
                .shouldMarkMessagesAsRead(false)
                .shouldDeleteMessages(false)
                .autoCloseFolder(false)
                .javaMailProperties { p: PropertiesBuilder ->
                    p.put("mail.debug", "false")
                    p.put("mail.imaps.ssl.trust", "*")
                }
            )
            { e: SourcePollingChannelAdapterSpec ->
                e.autoStartup(true).poller { p -> p.fixedDelay(1000) }
            }
            .handle { payload: Message<MimeMessage>, _: MessageHeaders? -> payload}
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
}