# How to write SLF4J logs into file; i.e. How to customize Logback configuration in Katalon Studio

This is a [Katalon Studio](https://katalon.com/katalon-studio) for demonstration purpose.
You can download the zip from the [Releases](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/releases/) page on the GitHub.

This project was created using [version 8.3.0](https://github.com/katalon-studio/katalon-studio/releases) but it should work on any versions.

I created this project in the hope that it can reply to an old question posted in the Katalon user forum at April 2019:

-   ["Write the slf4j logs into a text file"](https://forum.katalon.com/t/write-the-sl4j-logs-into-a-text-file/23332)

## Problem to solve

In a Katalon Studio project, I created [`Test Cases/runMyApps`](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Scripts/runMyApp3/Script1683151952333.groovy):

    import com.kazurayam.myapp.MyApp3

    MyApp3 instance = new MyApp3()
    instance.execute()

`runMyApps` calls a Groovy class.

-   [`Keywords/com/kazurayam/myapp/MyApp3.groovy`](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Keywords/com/kazurayam/myapp/MyApp3.groovy):

This calls another Groovy class:

-   <https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Keywords/com/kazurayam/myapp/Foo.groovy>

<!-- -->

    package com.kazurayam.myapp

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    /**
     * A class that emits a lot of logs which are very long
     * so that the amount of characters emitted by this class
     * will overflow the buffer of Console of Katalon Stduio
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

If you read the source of the `Foo` class, you would notice that it generates quite a lot of logs (1000 lines) using the `org.slf4j.Logger`. And each line of log will be very long (3.8K characters actually). The size of log will be 1000 \* 3.8K = 3.8Mega characters.

I rand the `Test Cases/runMyApp`. I got the following result:

![01 log lines disappeared](https://kazurayam.github.io/HowToWriteSLF4JLogsIntoFile/images/01_log_lines_disappeared.png)

In the Console tab of Katalon Studio, I expected to see 1000 lines of SLF4J logs from the `Foo` class. But actually I saw only the last part. Obviously, my `Foo` class emitted too much logs so that the logs exceeded the buffer size of the Console display.

I wanted to see the whole SLF4J logs from my application class. How can I manage it? --- **I want to write the SLF4J logs into a text file**.

## Solution

Katalon Studio uses the [SLF4J with Logback](https://www.baeldung.com/slf4j-with-log4j2-logback).

In the offical Logback documentation, I found a sample code how to customize the Logback Logger while specifying XML conf file.

-   [Chapter3 Configuration, Invoking JoranConfigurator directly](https://logback.qos.ch/manual/configuration.html#joranDirectly)

Using this technique, I should be able to customize the `LoggingContext` of Logback so that the logs out of my Test Case to be written into a text file.
