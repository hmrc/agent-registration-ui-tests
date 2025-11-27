package uk.gov.hmrc.ui.utils

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.Outcome
import uk.gov.hmrc.selenium.webdriver.Driver

import scala.util.Try

object TestFailureOverlay
extends LazyLogging:

  /** Renders a draggable HTML overlay on the browser showing test failure information.
    */
  def renderTestFailureOverlay(
    testName: String,
    outcomeOrException: Outcome | Throwable
  ): Unit = Try:

    val projectRoot = System.getProperty("user.dir")

    def createIntellijLink(
      fileName: String,
      lineNumber: Int
    ): String =
      val filePath = s"$projectRoot/$fileName"
      s"idea://open?file=${java.net.URLEncoder.encode(filePath, "UTF-8")}&line=$lineNumber"

    def extractFailureLocation(ex: Throwable): (String, String, Int) =
      ex match
        case tfe: org.scalatest.exceptions.StackDepthException =>
          tfe.failedCodeFileNameAndLineNumberString match
            case Some(location) =>
              val stackElement = tfe.getStackTrace()(tfe.failedCodeStackDepth)
              val className = stackElement.getClassName
              val fileName = stackElement.getFileName
              val lineNumber = stackElement.getLineNumber
              (s"$className at ($location)", fileName, lineNumber)
            case None => ("", "", 0)
        case _ =>
          ex.getStackTrace.headOption.map { ste =>
            (s"${ste.getClassName} at (${ste.getFileName}:${ste.getLineNumber})", ste.getFileName, ste.getLineNumber)
          }.getOrElse(("", "", 0))

    def makeStackTraceClickable(stackTrace: String): String =
      // Pattern matches: at package.Class.method(File.scala:123)
      val pattern = """at (.+)\((.+\.scala):(\d+)\)""".r
      stackTrace.split("\n").map { line =>
        pattern.findFirstMatchIn(line) match
          case Some(m) =>
            val method = m.group(1)
            val fileName = m.group(2)
            val lineNumber = m.group(3).toInt
            val link = createIntellijLink(fileName, lineNumber)
            s"""<a href="$link" style="color: #81c784; text-decoration: underline;">at $method($fileName:$lineNumber)</a>"""
          case None => line
      }.mkString("\n")

    val (errorMessage, failureLocationText, failureFileName, failureLineNumber, stackTrace) =
      outcomeOrException match
        case outcome: Outcome =>
          outcome match
            case org.scalatest.Failed(ex) =>
              val (locText, locFile, locLine) = extractFailureLocation(ex)
              val rawStack = ex.getStackTrace.map(_.toString).mkString("\n")
              (ex.getMessage, locText, locFile, locLine, rawStack)
            case org.scalatest.Canceled(ex) =>
              val (locText, locFile, locLine) = extractFailureLocation(ex)
              val rawStack = ex.getStackTrace.map(_.toString).mkString("\n")
              (ex.getMessage, locText, locFile, locLine, rawStack)
            case _ => ("Unknown failure", "", "", 0, "")
        case ex: Throwable =>
          val (locText, locFile, locLine) = extractFailureLocation(ex)
          val rawStack = ex.getStackTrace.map(_.toString).mkString("\n")
          (ex.getMessage, locText, locFile, locLine, rawStack)

    val failureLocationLink =
      if failureFileName.nonEmpty && failureLineNumber > 0 then
        createIntellijLink(failureFileName, failureLineNumber)
      else ""

    val clickableStackTrace = makeStackTraceClickable(stackTrace)

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
        |var failureLocationDiv = document.createElement('div');
        |failureLocationDiv.style.userSelect = 'text';
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
        |
        |if (arguments[3]) {
        |  var failureLink = document.createElement('a');
        |  failureLink.href = arguments[3];
        |  failureLink.textContent = arguments[2];
        |  failureLink.style.color = '#1976d2';
        |  failureLink.style.textDecoration = 'underline';
        |  failureLink.style.cursor = 'pointer';
        |  failureLocationDiv.appendChild(failureLink);
        |} else {
        |  failureLocationDiv.textContent = arguments[2];
        |}
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
        |stackTraceDiv.innerHTML = arguments[4];
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
      failureLocationText,
      failureLocationLink,
      clickableStackTrace
    )
  .recover:
    case ex => logger.warn(s"Could not display test failure info on browser: ${ex.getMessage}")
