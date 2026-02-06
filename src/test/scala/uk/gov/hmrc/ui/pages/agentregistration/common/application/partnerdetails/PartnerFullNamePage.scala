package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object PartnerFullNamePage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/enter-key-individual"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url
    
  private val partnerFullNameField = By.id("individualName")
  
  def enterPartnerFullName(name: String): Unit = sendKeys(partnerFullNameField, name)
