package com.kuuhakuLL.tmailer.integration

import com.kuuhakuLL.tmailer.service.MassageReceiver
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
class IntegrationConfig {
    @Autowired
    private val massageReceive: MassageReceiver? = null

    @Value("\${mail.port}")
    private val port: Int? = null

    @Value("\${mail.email}")
    private val email: String? = null

    @Value("\${mail.password}")
    private val password: String? = null

    @Bean
    fun mailListener(): IntegrationFlow? {
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
                payload.subject?.toString() == "Report" || payload.subject?.toString() == "Error"
            }
            .handle(massageReceive, "receive")
            .handle { payload: Message<MimeMessage> ->
                StaticMessageHeaderAccessor.getCloseableResource(payload)?.close()
            }
            .get()
    }
}