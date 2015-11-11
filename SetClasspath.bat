@echo off

REM This file sets the common classpath shared between the compile and run
REM batch files.

@echo on

set CLASSPATH=.;bin\classes;libs\junit.jar;libs\android-support-v4.jar;libs\hsqldb.jar;libs\markdown4j-2.2.jar;
