package uk.gov.hmrc.ui.pages.agentregistration.common.riskoutcomes

import uk.gov.hmrc.ui.pages.BasePage
import uk.gov.hmrc.ui.utils.AppConfig
import org.openqa.selenium.By
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import scala.jdk.CollectionConverters._

object ConditionsNotMetIndividualsPage
extends BasePage:

  override val path: String = "/agent-registration/conditions-not-yet-met/individuals"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

  inline def assertPageIsDisplayed(): Unit = eventually:
    getCurrentUrl shouldBe url

  case class ActionRow(
    name: String,
    actions: Seq[String],
    completed: String
  )

  private val individualFailuresLink = By.cssSelector("a[aria-describedby='individualFailures-1-status']")

  private def normalise(text: String): String = text.replaceAll("\\s+", " ").trim

  private def actionsTableRows: Seq[WebElement] = findElementsBy(By.cssSelector("table.govuk-table tbody tr.govuk-table__row"))

  private def rowForIndividual(name: String): WebElement = actionsTableRows
    .find { row =>
      val actualName = normalise(row.findElement(By.cssSelector("th.govuk-table__header")).getText)

      actualName == name
    }
    .getOrElse(fail(s"No actions row found for individual: $name"))

  def clickIndividualFailuresLink(): Unit = click(individualFailuresLink)

  // Functions to assert the actions table rows for individuals
  def assertActionsRow(expected: ActionRow): Unit = {
    val row = rowForIndividual(expected.name)

    val cells =
      row.findElements(By.cssSelector("td.govuk-table__cell"))
        .asScala
        .toSeq

    val actualActions =
      cells.head
        .findElements(By.cssSelector("li"))
        .asScala
        .map(action => normalise(action.getText))
        .toSeq

    val actualCompleted = normalise(cells(1).getText)

    actualActions shouldBe expected.actions
    actualCompleted shouldBe expected.completed
  }
