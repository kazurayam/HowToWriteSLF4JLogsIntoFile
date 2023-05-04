import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * Test Cases/TC1
 */
Logger logger = LoggerFactory.getLogger(this.getClass())

WebUI.comment("TC1 started")

for (int i = 1; i <= 999; i++) {
	logger.debug("Greeting from TC1(" + i + ")")
}
WebUI.comment("TC1 finished")