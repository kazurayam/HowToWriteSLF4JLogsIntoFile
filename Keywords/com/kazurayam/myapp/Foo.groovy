package com.kazurayam.myapp

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that emits a lot of logs which are very long
 * so that the amount of characters emitted by this class
 * will overflow the buffer of Console window of Katalon Stduio
 */
public class Foo {

	final static Logger logger = LoggerFactory.getLogger(Foo.class)

	void doIt() {
		for (int i = 0; i < 1000; i++) {
			StringBuilder sb = new StringBuilder()
			for (int j = 0; j < 300; j++) {
				sb.append("Hello(" + i + "," + j + ") ")
			}
			logger.debug(sb.toString())
		}
	}
}
