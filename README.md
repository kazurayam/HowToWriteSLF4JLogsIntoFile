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

## Solution described

I have developed a Groovy class [`com.kazurayam.ks.LoggerContextConfigurator`](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Keywords/com/kazurayam/ks/LoggerContextConfigurator.groovy)

    include:Keywords/com/kazurayam/ks/LoggerContextConfigurator.groovy[]

This code is almost identical to the sample code of Logback documentation. It overwrites the LoggerContext object as constructed by Katalon Studio while overwriting properties with the specified XML.

The class applies the following XML config as default.

-   <https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Include/config/logback-file.xml>

<!-- -->

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
      
      <property name="LOG_ROOT" value="./build/logs" />
      <property name="LOG_FILE_NAME" value="myapp" />
      
      <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${LOG_ROOT}/${LOG_FILE_NAME}.log</file>
        <append>true</append>
        <encoder>
          <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %-40.40logger{39} - %msg{}%n</pattern>
        </encoder>
      </appender>

      <!-- com.kms and com.kazurayam, etc -->
      <logger name="com" level="debug">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="STDERR"/>
        
        <appender-ref ref="FILE" />
      </logger>
      
    </configuration>

This XML declares an Appender named `FILE`. And the `FILE` appender is applied to all classes of which fully-qualified-class-names starts with `com` at the log level of `debug`. For example, the following classes will be targeted:

-   `com.kms.katalon.core.keyword.builtin.CommentKeyword`

-   `com.kazurayam.myapp.Foo`

I included a file [`logback-console.xml`](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Include/config/logback-console.xml) which is the default Logback configuration used by Katalon Studio. It contains some handles for your customization.

## How to run a demo

You want to create a Katalon Studio on your PC. Let me call it "test" test.

In the "test" project please create `Test Cases/runMyApp3`. You want to copy the above source and paste it into your Test Case.

Also please create `Keywords/myapp/MyApp3.groovy` and `Keywords/myapp/Foo.groovy`. You want to copy the above source and paste it into your Groovy class.

Now we start interesting portions.

Please visit the [Releases](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/releases/) page of GitHub repository. You will find a link to a jar file named `LoggerContextConfigurator-x.x.x.jar`. Please download the jar file and save it into the `Drivers` folder of your local Katalon Studio project. The `Driver` folder would like this:

![02 Drivers](https://kazurayam.github.io/HowToWriteSLF4JLogsIntoFile/images/02_Drivers.png)

Please create a file `Include/config/logback-file.xml`. The content of the file should be like the one as described above.

Please note the `com.kazurayam.ks.LoggerContextConfigurator` class knows the the path of the xml file to load.

Final step. You want to create a Test Listener.

-   [Test Listener/ConfigLogger.groovy](https://github.com/kazurayam/HowToWriteSLF4JLogsIntoFile/blob/develop/Test%40Listener/ConfigLogger.groovy)

<!-- -->

    import com.kazurayam.ks.LoggerContextConfigurator
    import com.kms.katalon.core.annotation.BeforeTestCase
    import com.kms.katalon.core.annotation.BeforeTestSuite
    import com.kms.katalon.core.context.TestCaseContext
    import com.kms.katalon.core.context.TestSuiteContext

    class ConfigLogger {
        
        /**
         * Executes before every test case starts.
         * @param testCaseContext related information of the executed test case.
         */
        @BeforeTestCase
        def beforeTestCase(TestCaseContext testCaseContext) {
            LoggerContextConfigurator.configure()
        }

        /**
         * Executes before every test suite starts.
         * @param testSuiteContext: related information of the executed test suite.
         */
        @BeforeTestSuite
        def beforeTestSuite(TestSuiteContext testSuiteContext) {
            LoggerContextConfigurator.configure()
        }
    }

This Test Listener just calls the `LoggerContextConfigurator.configure()` when any Test Cases and Test Suites are invoked. Effectively the SLF4J `Logger` is customized so that it writes logs into file.

## Further customization

You can change the name of output log file as well as its location by modifying the xml file.

You can add new xml file and do whatever customization of SLF4J logging you want.

How to? --- Please read the code and find for yourself.
