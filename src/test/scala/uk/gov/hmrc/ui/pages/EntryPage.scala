package uk.gov.hmrc.ui.pages

trait EntryPage extends BasePage {
  def open(): Unit = get(url)
}
