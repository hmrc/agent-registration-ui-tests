package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes

import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig
import org.openqa.selenium.By

object ApplicationStatusPage
extends BasePage:
  
  override val path: String = "/agent-registration/application-status"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val viewActionsToTakeButton =
    By.cssSelector("a.govuk-button--start[href='/agent-registration/conditions-not-yet-met/task-list']")

  def clickViewActionsToTakeButton(): Unit = click(viewActionsToTakeButton)