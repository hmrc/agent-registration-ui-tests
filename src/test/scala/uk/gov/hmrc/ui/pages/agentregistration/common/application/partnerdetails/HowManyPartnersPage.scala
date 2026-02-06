package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object HowManyPartnersPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/how-many-key-individuals"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val fiveOrFewerRadio = By.id("numberOfKeyIndividuals")
  private val sixOrMoreRadio = By.id("numberOfKeyIndividuals-2")
  private val exactNumberField = By.id("exactNumberOfOfficials")
  private val numResponsibleForTaxField = By.id(("numberOfOfficialsWhoDealWithTax"))

  def selectFiveOrLess(): Unit = click(fiveOrFewerRadio)
  def selectSixOrMore(): Unit = click(sixOrMoreRadio)
  def enterExactNumber(n: String): Unit = sendKeys(exactNumberField, n)
  def enterNumResponsible(n: String): Unit = sendKeys(numResponsibleForTaxField, n)
