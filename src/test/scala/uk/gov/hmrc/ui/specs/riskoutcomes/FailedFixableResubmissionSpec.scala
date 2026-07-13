package uk.gov.hmrc.ui.specs.riskoutcomes

import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow.listProgress.complete
import uk.gov.hmrc.ui.domain.BusinessType.GeneralPartnership
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks
import uk.gov.hmrc.ui.flows.common.application.riskingOutcome.RiskingOutcomeFlow
import uk.gov.hmrc.ui.flows.common.application.FastForwardLinks.ApplicationProgress.AgentStandards
import uk.gov.hmrc.ui.flows.common.application.declaration.DeclarationFlow
import uk.gov.hmrc.ui.flows.common.application.partnerInformation.PartnerTaxAdvisorInformationFlow
import uk.gov.hmrc.ui.flows.common.application.providedetails.ProvideIndividualDetailsFlow
import uk.gov.hmrc.ui.pages.agentregistration.common.application.ApplicationSubmittedPage
import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.EntityFix
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualFix
import uk.gov.hmrc.ui.utils.MongoHelper.IndividualRiskingOutcome


class FailedFixableResubmissionSpec

  extends BaseSpec:

  Feature("Individual FailedFixable Tasklist"):
    Scenario("Applicant signs declaration and confirms resubmission", TagFixableFailures):

      val stubbedSignInData = FastForwardLinks
        .FastForward
        .runFlow(AgentStandards, GeneralPartnership)

      PartnerTaxAdvisorInformationFlow
        .singlePartner
        .runFlow()

      val username = ProvideIndividualDetailsFlow
        .ProvideIndividualDetails
        .runFlowWithUsername(
          stubbedSignInData,
          complete,
          GeneralPartnership
        )

      DeclarationFlow
        .AcceptDeclaration
        .runFlow(GeneralPartnership)

      ApplicationSubmittedPage.assertPageIsDisplayed()

      ApplicationSubmittedPage.assertConfirmationTitle(
        "You’ve applied for an agent services account"
      )

      val applicationReference = ApplicationSubmittedPage.getApplicationReference
      val linkId: String = MongoHelper.getLinkIdByApplicationReference(applicationReference)

      MongoHelper
        .findByApplicationReference(applicationReference)
        .getOrElse(throw new AssertionError(s"No Mongo record found for reference: $applicationReference"))

      MongoHelper.insertRiskingOutcomeToAgentApplication(
        applicationReference = applicationReference,
        actualDecisionDate = "2026-06-18",
        outcome = "FailedFixable",
        correctiveActionExpiryDate = "2026-08-17",
        fixes = Seq(EntityFix("EntityFix._4._2", isConfirmed = true))
      )
      
      // Insert one individual. They have confirmed their fixes and signed the declaration
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Bobby Boucher" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      RiskingOutcomeFlow
        .viewListOfIndividualActions
        .runFlow(
          stubbedSignInData
        )
