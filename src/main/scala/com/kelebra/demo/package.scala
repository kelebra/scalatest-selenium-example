package com.kelebra

import java.io.File
import javax.activation.FileDataSource

import org.codemonkey.simplejavamail.email.Email
import org.codemonkey.simplejavamail.{Mailer, TransportStrategy}

package object demo {

  case class InfoArguments(caseNumber: String, mail: String)

  case class CaseInfo(caseNumber: String, text: String, screenshot: File) {

    def source = new FileDataSource(screenshot)
  }

  def send(login: String, password: String, arguments: InfoArguments, info: CaseInfo): Unit = {
    new Mailer("smtp.gmail.com", 587, login, password, TransportStrategy.SMTP_TLS)
      .sendMail(
        new Email.Builder()
          .from("Me", login)
          .to("You", arguments.mail)
          .subject(s"Current status for case ${arguments.caseNumber}")
          .addAttachment("Current_status.png", info.source)
          .text(info.text)
          .build()
      )
    info.screenshot.delete()
  }

}
