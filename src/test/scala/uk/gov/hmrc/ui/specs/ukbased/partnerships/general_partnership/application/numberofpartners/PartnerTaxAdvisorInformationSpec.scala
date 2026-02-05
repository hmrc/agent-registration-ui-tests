package uk.gov.hmrc.ui.specs.ukbased.partnerships.general_partnership.application.numberofpartners

import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.flows.common.application.agentdetails.AgentDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.amlsdetails.AmlsDetailsFlow
import uk.gov.hmrc.ui.flows.common.application.contactdetails.ContactDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.general_partnership.businessdetails.application.BusinessDetailsFlow
import uk.gov.hmrc.ui.flows.ukbased.partnerships.limited_liability_partnership.application.businessdetails.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.specs.BaseSpec

class PartnerTaxAdvisorInformationSpec
extends BaseSpec:

  Feature("Complete Partner and Tax Advisor information section"):
    Scenario("Partnership has 5 or less partners", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .FiveOrLessPartners
        .runFlow()

    Scenario("Partnership has 6 or more partners", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .SixOrMorePartners
        .runFlow()

    Scenario("Partnership has 6 more partners but less than 6 with tax authority", HappyPath):

      val stubbedSignInData = BusinessDetailsFlow
        .HasNoOnlineAccount
        .runFlow()

      ContactDetailsFlow
        .runFlow(stubbedSignInData)

      AgentDetailsFlow
        .WhenUsingProvidedOptions
        .runFlow(stubbedSignInData, GeneralPartnership)

      AmlsDetailsFlow
        .WhenHmrcAreSupervisoryBody
        .runFlow()

      PartnerTaxAdvisorInformationFlow
        .SixOrMorePartnersAlt
        .runFlow()
