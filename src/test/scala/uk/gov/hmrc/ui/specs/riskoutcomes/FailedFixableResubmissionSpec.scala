package uk.gov.hmrc.ui.specs.riskoutcomes

import uk.gov.hmrc.ui.specs.BaseSpec
import uk.gov.hmrc.ui.utils.MongoHelper
import uk.gov.hmrc.ui.utils.MongoHelper.{IndividualFix, IndividualRiskingOutcome}

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
        fixes = Seq.empty
      )
      
      // Insert two individuals. Both have confirmed their fixes and signed the declaration
      MongoHelper.insertRiskingOutcomeIndividualsToAgentApplication(
        applicationReference = applicationReference,
        outcomesByIndividualName = Map(
          "Steve Austin" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._3", isConfirmed = true),
              IndividualFix("IndividualFix._8._7", isConfirmed = true)
            ),
            declarationAgreed = true
          ),
          "Beverly Hills" -> IndividualRiskingOutcome(
            outcomeType = "FailedFixable",
            fixes = Seq(
              IndividualFix("IndividualFix._4._1", isConfirmed = true),
              IndividualFix("IndividualFix._5._1", isConfirmed = true)
            ),
            declarationAgreed = true
          )
        )
      )

      RiskingOutcomeFlow
        .viewIndividualTaskListPage
        .runFlow(
          stubbedSignInData,
          linkId,
          username
        )
