@echo off

call SetClasspath

REM To get console-mode results from unit test execution, use the following instead:

REM java junit.textui.TestRunner comp3350.tests.AllTests

@echo on

java junit.swingui.TestRunner comp3350.tests.IntegrationTests