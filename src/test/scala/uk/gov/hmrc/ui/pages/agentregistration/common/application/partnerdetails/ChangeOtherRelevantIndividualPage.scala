package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object ChangeOtherRelevantIndividualPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/change-other-relevant-individual"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should startWith(url)

  private val partnerFullNameField = By.id("individualName")

  def enterPartnerFullName(name: String): Unit = sendKeys(partnerFullNameField, name)
