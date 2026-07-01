package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes

import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig
import org.openqa.selenium.By

object ConditionsNotMetTaskListPage
extends BasePage:

  override val path: String = "/agent-registration/conditions-not-yet-met/task-list"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  private val individualFailuresLink = By.cssSelector("a[aria-describedby='individualFailures-1-status']")

  def clickIndividualFailuresLink(): Unit = click(individualFailuresLink)
