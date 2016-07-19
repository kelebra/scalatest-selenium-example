package com.kelebra.demo

import java.io.File
import java.util.{Timer, TimerTask}

import com.machinepublishers.jbrowserdriver.JBrowserDriver
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.Eventually._
import org.scalatest.selenium.{Driver, WebBrowser}

import scala.collection.JavaConversions._

object InformationExtractor extends App with Driver with WebBrowser {

  implicit val webDriver = new JBrowserDriver()

  val config = ConfigFactory.parseURL(this.getClass.getResource("/application.conf"))

  val host = args(0)

  val login = args(1)
  val password = args(2)

  val task = new TimerTask {
    override def run(): Unit = {

      val casesConfig = ConfigFactory.parseFile(new File(config.getString("config.location"))).getConfig("cases")

      casesConfig
        .entrySet()
        .map(_.getKey)
        .map(email => InfoArguments(casesConfig.getString(email), email))
        .map(arguments => (arguments, caseData(arguments.caseNumber)))
        .foreach {
          case (arguments: InfoArguments, caseData: CaseInfo) =>
            send(login, password, arguments, caseData)
        }
    }
  }

  def captureScreenshot(screenshotFileName: String): File = {
    val screenshot = new File(System.getProperty("java.io.tmpdir"), s"$screenshotFileName.png")
    captureTo(screenshotFileName)
    screenshot
  }

  def caseData(caseNumber: String): CaseInfo = {
    go to host
    val homePageTitle = pageTitle

    textArea(name("caseNumbers")).value = caseNumber

    click on id("Searchcases")

    eventually {
      pageTitle != homePageTitle
    }

    val text = findAll(tagName("td")).toList.map(_.text).grouped(6).map(_.mkString(" ")).mkString("\n")
    val screenshot = captureScreenshot(System.currentTimeMillis().toString)

    CaseInfo(caseNumber, text, screenshot)
  }

  val timer = new Timer()
  timer.scheduleAtFixedRate(task, 0, 86400000)
}
