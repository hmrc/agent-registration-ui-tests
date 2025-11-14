package uk.gov.hmrc.ui.pages.agentregistration.ukbased.partnerships.limited_liability_partnership.application.contactdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object ApplicantNamePage
  extends BasePage {
  
  override val path: String = "/agent-registration/apply/applicant/applicant-name"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val fullNameField = By.id("authorisedName")

  def enterFullName(fullName: String): Unit = sendKeys(fullNameField, fullName)
}
