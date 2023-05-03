package com.kazurayam.myapp

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import com.kazurayam.ks.LoggerContextConfigurator

/**
 * https://yujiorama.github.io/unofficial-translations/logback-manual/03-configuration.html
 */
public class MyApp3 {

	final static Logger logger = LoggerFactory.getLogger(MyApp3.class)

	public void execute() {
		/*
		 // SLF4Jがlogbackを使うように設定されていると想定
		 LoggerContext context = (LoggerContext)LoggerFactory.getILoggerFactory()
		 try {
		 JoranConfigurator configurator = new JoranConfigurator();
		 configurator.setContext(context)
		 // デフォルトの設定を取り消すために context.reset()を呼び出す
		 // context.reset()を呼ばなければ設定を上書きすることになる
		 //context.reset()
		 //configurator.doConfigure("src/main/resources/logback-console-file.xml")
		 configurator.doConfigure("src/main/resources/logback-file.xml")
		 } catch (JoranException je) {
		 // StatusPrinter will handle this
		 }
		 StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		 */

		logger.info("Entering MyApp3 application.");
		WebUI.comment("calling Foo")

		Foo foo = new Foo()
		foo.doIt()

		WebUI.comment("called Foo")
		logger.info("Exiting MyApp3 application");
	}
}
