package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.PartnerFullNamePage.{eventually, getCurrentUrl, sendKeys}
import uk.gov.hmrc.ui.utils.AppConfig

object OtherRelevantIndividualPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/enter-other-relevant-individual"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val unofficialPartnerFullNameField = By.id("individualName")

  def enterPartnerFullName(name: String): Unit = sendKeys(unofficialPartnerFullNameField, name)