package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig

object UnofficialPartnersPage
extends BasePage:

  override val path: String = "/agent-registration/apply/list-details/how-many-other-individuals"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val yesRadio = By.id("hasOtherRelevantIndividuals")
  private val noRadio = By.id("hasOtherRelevantIndividuals-2")

  def selectYes(): Unit = click(yesRadio)
  def selectNo(): Unit = click(noRadio)

