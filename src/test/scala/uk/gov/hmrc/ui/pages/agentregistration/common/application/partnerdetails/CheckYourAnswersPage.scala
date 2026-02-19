package uk.gov.hmrc.ui.pages.agentregistration.common.application.partnerdetails

import uk.gov.hmrc.ui.pages.agentregistration.common.application.BaseCheckYourAnswersPage
import uk.gov.hmrc.ui.utils.AppConfig

object CheckYourAnswersPage
extends BaseCheckYourAnswersPage:

  override val path: String = "/agent-registration/apply/list-details/check-your-answers"
  override val baseUrl: String = AppConfig.baseUrlAgentRegistrationFrontend

