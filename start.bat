@echo off

	set MAIN_CLASS_FILE="it.csttech.SimpleTester"
	set CLASS_PATH=".;lib\*;bin"
	set LOG_CONFIG="cfg\log4j2.xml"
	set DEFAULT_PROPERTIES="./cfg/Default.properties"


	echo -------------------------------------------------------------------------
	echo ***  Launching %MAIN_CLASS_FILE% %* ***
	echo -------------------------------------------------------------------------
	echo.

java -cp %CLASS_PATH% %MAIN_CLASS_FILE% %*

	echo.
	echo -------------------------------------------------------------------------
