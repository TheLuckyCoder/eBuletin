package bugs.decentralized.controller.service

import bugs.decentralized.BlockchainApplication
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.util.*

@Component
class EmailSenderService {

    val mailSender by lazy {
        val mailSender = JavaMailSenderImpl().apply {
            host = "smtp.gmail.com"
            port = 587
            username = BlockchainApplication.DOTENV["SMTP_USERNAME"]
            password = BlockchainApplication.DOTENV["SMTP_APP_PASSWORD"]
        }

        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = BlockchainApplication.DOTENV["SMTP_DEBUG"]
        props["mail.smtp.socketFactory.port"] = "465"
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"

        mailSender
    }

    fun sendMail(to: String, subject: String, content: String) {
        val msg = createSimpleMessage(to, subject, content)
        mailSender.send(msg)
    }

    private fun createSimpleMessage(to: String, subject: String, content: String): MimeMessage {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)

        setupMessage(helper, to, subject, content)

        return message
    }

    private fun setupMessage(helper: MimeMessageHelper, to: String, subject: String, content: String) {
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(content)
    }
}
