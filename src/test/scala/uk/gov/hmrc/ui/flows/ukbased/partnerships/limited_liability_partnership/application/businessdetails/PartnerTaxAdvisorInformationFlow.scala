package uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails

import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.PartnerTaxAdvisorInformationFlow.NumberOfPartners.{FiveOrLess, SixOrMore}
import uk.gov.hmrc.ui.pages.agentregistration.common.application.TaskListPage
import uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails.{HowManyPartnersPage, PartnerFullNamePage}

object PartnerTaxAdvisorInformationFlow:

  enum NumberOfPartners:
    case FiveOrLess, SixOrMore
    def selectOnPage(n: String): Unit = this match
      case FiveOrLess => 
        HowManyPartnersPage.selectFiveOrLess()
        HowManyPartnersPage.enterExactNumber(n)
      case SixOrMore => 
        HowManyPartnersPage.selectSixOrMore()
        HowManyPartnersPage.enterNumResponsible(n)


  object FiveOrLessPartners:

    def runFlow(): Unit =
      startJourney()
      enterNumberOfPartners("3", FiveOrLess)
      enterPartnerName("Bobby Boucher")

  object SixOrMorePartners:

    def runFlow(): Unit =
      startJourney()
      enterNumberOfPartners("7", SixOrMore)
      enterPartnerName("Bobby Boucher")

  object SixOrMorePartnersAlt: //alternate flow where there are 6 or more partners but less than 6 with tax authority

    def runFlow(): Unit =
      startJourney()
      enterNumberOfPartners("3", SixOrMore)
      enterPartnerName("Bobby Boucher")

  def startJourney(): Unit =
    TaskListPage.assertPageIsDisplayed()
    TaskListPage.assertPartnerTaxAdvisorInformationStatus("Incomplete")
    TaskListPage.clickOnPartnerTaxAdvisorInformationLink()

  def enterNumberOfPartners(n: String, totalNum: NumberOfPartners): Unit =
    HowManyPartnersPage.assertPageIsDisplayed()
    totalNum.selectOnPage(n)
    HowManyPartnersPage.clickContinue()

  def enterPartnerName(name: String): Unit =
    PartnerFullNamePage.assertPageIsDisplayed()
    PartnerFullNamePage.enterPartnerFullName(name)
    PartnerFullNamePage.clickContinue()
    
  //TODO add rest of this journey once available