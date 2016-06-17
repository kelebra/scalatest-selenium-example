package com.kelebra.demo

import java.io.File
import java.util.{Timer, TimerTask}
import javax.activation.FileDataSource

import com.machinepublishers.jbrowserdriver.JBrowserDriver
import org.codemonkey.simplejavamail.email.Email
import org.codemonkey.simplejavamail.{Mailer, TransportStrategy}
import org.scalatest.concurrent.Eventually._
import org.scalatest.selenium.{Driver, WebBrowser}

object InformationExtractor extends App with Driver with WebBrowser {

  implicit val webDriver = new JBrowserDriver()

  val host = args(0)
  val caseNumber = args(1)

  val login = args(2)
  val password = args(3)

  val task = new TimerTask {
    override def run(): Unit = {
      go to args(0)
      val homePageTitle = pageTitle

      textArea(name("caseNumbers")).value = caseNumber

      click on id("Searchcases")

      eventually {
        pageTitle != homePageTitle
      }

      send(
        captureScreenshot(System.currentTimeMillis().toString),
        findAll(tagName("td")).toList.map(_.text).grouped(6).map(_.mkString(" ")).mkString("\n"),
        login,
        password
      )
    }
  }

  val timer = new Timer()
  timer.scheduleAtFixedRate(task, 0, 86400000)


  def captureScreenshot(screenshotFileName: String): File = {
    val screenshot = new File(System.getProperty("java.io.tmpdir"), s"$screenshotFileName.png")
    captureTo(screenshotFileName)
    screenshot
  }

  def send(screenShot: File, text: String, login: String, password: String): Unit = {
    new Mailer("smtp.gmail.com", 587, login, password, TransportStrategy.SMTP_TLS)
      .sendMail(
        new Email.Builder()
          .from("Me", login)
          .to("Me", login)
          .subject("Current status")
          .addAttachment("Current_status.png", new FileDataSource(screenShot))
          .text(text)
          .build()
      )
  }
}
