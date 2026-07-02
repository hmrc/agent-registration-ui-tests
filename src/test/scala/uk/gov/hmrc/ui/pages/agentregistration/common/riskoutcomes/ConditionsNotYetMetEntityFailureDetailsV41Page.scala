package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes

import org.openqa.selenium.By
import uk.gov.hmrc.ui.pages.{BasePage, PageObject}
import uk.gov.hmrc.ui.utils.AppConfig

object ConditionsNotYetMetEntityFailureDetailsV41Page
  extends BasePage:

  override val path: String = "/agent-registration/conditions-not-yet-met/failure-details/EntityFix.4.1"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  private val pageHeading = By.cssSelector("h1.govuk-heading-l")

  private val yesRadio = By.id("isFixed")
  private val noRadio = By.id("isFixed-2")

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl should include(url)

  def assertPageHeadingContains(entityPageHeading: String): Unit = eventually:
    getText(pageHeading) should include(entityPageHeading)

  def selectYes(): Unit = click(yesRadio)

  def selectNo(): Unit = click(noRadio)
