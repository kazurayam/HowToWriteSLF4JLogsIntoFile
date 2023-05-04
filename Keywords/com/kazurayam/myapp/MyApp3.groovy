package com.kazurayam.myapp

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.kazurayam.ks.LoggerContextConfigurator

public class MyApp3 {

	final static Logger logger = LoggerFactory.getLogger(MyApp3.class)

	public void execute() {
		logger.info("Entering MyApp3 application.");
		WebUI.comment("calling Foo")

		Foo foo = new Foo()
		foo.doIt()

		WebUI.comment("called Foo")
		logger.info("Exiting MyApp3 application");
	}
}
