#!/bin/bash

main=SimpleTester
#logConfig=cfg/log4j2.xml
#defaultProperties=./cfg/Default.properties

java -cp bin:lib/*  it.csttech.$main  $*
