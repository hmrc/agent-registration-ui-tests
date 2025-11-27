/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ui.utils

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.Outcome
import uk.gov.hmrc.selenium.webdriver.Driver

import scala.util.Try

object TestFailureOverlay
extends LazyLogging:

  /** Creates an interactive HTML overlay on the browser window to display test failure information.
    *
    * The overlay provides a detailed view of the test failure with:
    *   - Test name and error message
    *   - Precise failure location (class name, file and line number)
    *   - Full stack trace in a collapsible section
    *
    * Features:
    *   - Draggable window that can be positioned anywhere on screen
    *   - Resizable panel for better readability
    *   - Minimizable view to reduce screen space
    *   - Copyable text content for sharing/debugging
    *
    * @param testName
    *   The name of the test that failed
    * @param outcomeOrException
    *   The test failure details, either as a ScalaTest Outcome or Throwable
    */
  def renderTestFailureOverlay(
    testName: String,
    outcomeOrException: Outcome | Throwable
  ): Unit = Try:

    def extractFailureLocation(ex: Throwable): String =
      ex match
        case tfe: org.scalatest.exceptions.StackDepthException =>
          tfe.failedCodeFileNameAndLineNumberString match
            case Some(location) =>
              val stackElement = tfe.getStackTrace()(tfe.failedCodeStackDepth)
              s"${stackElement.getClassName} at ($location)"
            case None => ""
        case _ =>
          ex.getStackTrace.headOption.map { ste =>
            s"${ste.getClassName} at (${ste.getFileName}:${ste.getLineNumber})"
          }.getOrElse("")

    val (errorMessage, failureLocation, stackTrace) =
      outcomeOrException match
        case outcome: Outcome =>
          outcome match
            case org.scalatest.Failed(ex) => (ex.getMessage, extractFailureLocation(ex), ex.getStackTrace.map(_.toString).mkString("\n"))
            case org.scalatest.Canceled(ex) => (ex.getMessage, extractFailureLocation(ex), ex.getStackTrace.map(_.toString).mkString("\n"))
            case _ => ("Unknown failure", "", "")
        case ex: Throwable => (ex.getMessage, extractFailureLocation(ex), ex.getStackTrace.map(_.toString).mkString("\n"))

    val js = Driver.instance.asInstanceOf[org.openqa.selenium.JavascriptExecutor]
    js.executeScript(
      """
        |var container = document.createElement('div');
        |container.style.position = 'fixed';
        |container.style.top = '20px';
        |container.style.left = '20px';
        |container.style.backgroundColor = '#fff';
        |container.style.border = '1px solid #e0e0e0';
        |container.style.borderLeft = '4px solid #f44336';
        |container.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)';
        |container.style.borderRadius = '4px';
        |container.style.fontSize = '14px';
        |container.style.fontFamily = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif';
        |container.style.zIndex = '999999';
        |container.style.width = '600px';
        |container.style.minWidth = '400px';
        |container.style.resize = 'both';
        |container.style.overflow = 'auto';
        |
        |var handle = document.createElement('div');
        |handle.style.backgroundColor = '#fafafa';
        |handle.style.padding = '8px 16px';
        |handle.style.cursor = 'move';
        |handle.style.borderBottom = '1px solid #e0e0e0';
        |handle.style.borderRadius = '4px 4px 0 0';
        |handle.style.userSelect = 'none';
        |handle.style.fontSize = '12px';
        |handle.style.fontWeight = '500';
        |handle.style.color = '#666';
        |handle.style.textTransform = 'uppercase';
        |handle.style.letterSpacing = '0.5px';
        |handle.style.display = 'flex';
        |handle.style.justifyContent = 'space-between';
        |handle.style.alignItems = 'center';
        |
        |var handleText = document.createElement('span');
        |handleText.innerHTML = 'Test Failed: ' + arguments[0];
        |handleText.style.overflow = 'hidden';
        |handleText.style.textOverflow = 'ellipsis';
        |handleText.style.whiteSpace = 'nowrap';
        |
        |var minimizeBtn = document.createElement('button');
        |minimizeBtn.innerHTML = '−';
        |minimizeBtn.style.backgroundColor = 'transparent';
        |minimizeBtn.style.border = 'none';
        |minimizeBtn.style.color = '#666';
        |minimizeBtn.style.fontSize = '18px';
        |minimizeBtn.style.cursor = 'pointer';
        |minimizeBtn.style.padding = '0 4px';
        |minimizeBtn.style.lineHeight = '1';
        |minimizeBtn.style.fontWeight = 'bold';
        |minimizeBtn.style.transition = 'color 0.2s';
        |minimizeBtn.style.flexShrink = '0';
        |minimizeBtn.onmouseover = function() {
        |  this.style.color = '#333';
        |};
        |minimizeBtn.onmouseout = function() {
        |  this.style.color = '#666';
        |};
        |
        |handle.appendChild(handleText);
        |handle.appendChild(minimizeBtn);
        |
        |var content = document.createElement('div');
        |content.style.padding = '16px';
        |
        |var textDiv = document.createElement('div');
        |textDiv.style.userSelect = 'text';
        |textDiv.style.cursor = 'text';
        |textDiv.style.marginBottom = '12px';
        |textDiv.style.fontSize = '13px';
        |textDiv.style.fontWeight = '500';
        |textDiv.style.color = '#333';
        |textDiv.innerHTML = arguments[0];
        |
        |var errorDiv = document.createElement('pre');
        |errorDiv.style.userSelect = 'text';
        |errorDiv.style.cursor = 'text';
        |errorDiv.style.fontSize = '13px';
        |errorDiv.style.lineHeight = '1.5';
        |errorDiv.style.margin = '0 0 8px 0';
        |errorDiv.style.fontWeight = '400';
        |errorDiv.style.color = '#d32f2f';
        |errorDiv.style.backgroundColor = '#ffebee';
        |errorDiv.style.padding = '12px';
        |errorDiv.style.borderRadius = '3px';
        |errorDiv.style.border = '1px solid #ffcdd2';
        |errorDiv.style.fontFamily = '"JetBrains Mono", "Fira Code", "SF Mono", Monaco, Consolas, monospace';
        |errorDiv.style.whiteSpace = 'pre-wrap';
        |errorDiv.style.wordWrap = 'break-word';
        |errorDiv.style.overflowWrap = 'break-word';
        |errorDiv.textContent = arguments[1];
        |
        |var failureLocationDiv = document.createElement('pre');
        |failureLocationDiv.style.userSelect = 'text';
        |failureLocationDiv.style.cursor = 'text';
        |failureLocationDiv.style.fontSize = '11px';
        |failureLocationDiv.style.lineHeight = '1.4';
        |failureLocationDiv.style.margin = '0 0 12px 0';
        |failureLocationDiv.style.fontWeight = '400';
        |failureLocationDiv.style.color = '#616161';
        |failureLocationDiv.style.backgroundColor = '#f5f5f5';
        |failureLocationDiv.style.padding = '8px 12px';
        |failureLocationDiv.style.borderRadius = '3px';
        |failureLocationDiv.style.border = '1px solid #e0e0e0';
        |failureLocationDiv.style.fontFamily = '"JetBrains Mono", "Fira Code", "SF Mono", Monaco, Consolas, monospace';
        |failureLocationDiv.style.whiteSpace = 'pre-wrap';
        |failureLocationDiv.style.wordWrap = 'break-word';
        |failureLocationDiv.style.overflowWrap = 'break-word';
        |failureLocationDiv.textContent = arguments[2];
        |
        |var toggleButton = document.createElement('button');
        |toggleButton.style.marginTop = '8px';
        |toggleButton.style.padding = '6px 12px';
        |toggleButton.style.cursor = 'pointer';
        |toggleButton.style.backgroundColor = '#fff';
        |toggleButton.style.border = '1px solid #e0e0e0';
        |toggleButton.style.borderRadius = '3px';
        |toggleButton.style.color = '#666';
        |toggleButton.style.fontSize = '12px';
        |toggleButton.style.fontWeight = '400';
        |toggleButton.style.fontFamily = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
        |toggleButton.style.transition = 'all 0.2s';
        |toggleButton.innerHTML = '▼ Stack Trace';
        |toggleButton.onmouseover = function() {
        |  this.style.backgroundColor = '#fafafa';
        |  this.style.borderColor = '#bdbdbd';
        |};
        |toggleButton.onmouseout = function() {
        |  this.style.backgroundColor = '#fff';
        |  this.style.borderColor = '#e0e0e0';
        |};
        |
        |var stackTraceDiv = document.createElement('pre');
        |stackTraceDiv.style.userSelect = 'text';
        |stackTraceDiv.style.cursor = 'text';
        |stackTraceDiv.style.fontSize = '11px';
        |stackTraceDiv.style.lineHeight = '1.4';
        |stackTraceDiv.style.marginTop = '8px';
        |stackTraceDiv.style.marginBottom = '0';
        |stackTraceDiv.style.fontWeight = '400';
        |stackTraceDiv.style.backgroundColor = '#263238';
        |stackTraceDiv.style.color = '#aed581';
        |stackTraceDiv.style.padding = '12px';
        |stackTraceDiv.style.overflow = 'auto';
        |stackTraceDiv.style.maxHeight = '300px';
        |stackTraceDiv.style.borderRadius = '3px';
        |stackTraceDiv.style.border = '1px solid #37474f';
        |stackTraceDiv.style.fontFamily = '"JetBrains Mono", "Fira Code", "SF Mono", Monaco, Consolas, monospace';
        |stackTraceDiv.style.display = 'none';
        |stackTraceDiv.style.whiteSpace = 'pre-wrap';
        |stackTraceDiv.style.wordWrap = 'break-word';
        |stackTraceDiv.style.overflowWrap = 'break-word';
        |stackTraceDiv.textContent = arguments[3];
        |
        |toggleButton.addEventListener('click', function() {
        |  if (stackTraceDiv.style.display === 'none') {
        |    stackTraceDiv.style.display = 'block';
        |    toggleButton.innerHTML = '▲ Stack Trace';
        |  } else {
        |    stackTraceDiv.style.display = 'none';
        |    toggleButton.innerHTML = '▼ Stack Trace';
        |  }
        |});
        |
        |content.appendChild(textDiv);
        |content.appendChild(errorDiv);
        |content.appendChild(failureLocationDiv);
        |content.appendChild(toggleButton);
        |content.appendChild(stackTraceDiv);
        |
        |container.appendChild(handle);
        |container.appendChild(content);
        |document.body.appendChild(container);
        |
        |var isMinimized = false;
        |var savedWidth, savedHeight;
        |
        |function toggleMinimize() {
        |  isMinimized = !isMinimized;
        |  if (isMinimized) {
        |    savedWidth = container.style.width;
        |    savedHeight = container.style.height;
        |    content.style.display = 'none';
        |    container.style.resize = 'none';
        |    container.style.width = 'auto';
        |    container.style.height = 'auto';
        |    container.style.minHeight = 'auto';
        |    minimizeBtn.innerHTML = '□';
        |  } else {
        |    content.style.display = 'block';
        |    container.style.resize = 'both';
        |    container.style.width = savedWidth || '600px';
        |    container.style.height = savedHeight || 'auto';
        |    container.style.minHeight = '200px';
        |    minimizeBtn.innerHTML = '−';
        |  }
        |}
        |
        |minimizeBtn.addEventListener('click', function(e) {
        |  e.stopPropagation();
        |  toggleMinimize();
        |});
        |
        |handle.addEventListener('dblclick', function(e) {
        |  toggleMinimize();
        |});
        |
        |var isDragging = false;
        |var offsetX, offsetY;
        |
        |handle.addEventListener('mousedown', function(e) {
        |  if (e.target !== minimizeBtn) {
        |    isDragging = true;
        |    offsetX = e.clientX - container.offsetLeft;
        |    offsetY = e.clientY - container.offsetTop;
        |  }
        |});
        |
        |document.addEventListener('mousemove', function(e) {
        |  if (isDragging) {
        |    container.style.left = (e.clientX - offsetX) + 'px';
        |    container.style.top = (e.clientY - offsetY) + 'px';
        |  }
        |});
        |
        |document.addEventListener('mouseup', function() {
        |  isDragging = false;
        |});
        |""".stripMargin,
      testName,
      errorMessage,
      failureLocation,
      stackTrace
    )
  .recover:
    case ex => logger.warn(s"Could not display test failure info on browser: ${ex.getMessage}")
