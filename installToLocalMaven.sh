#!/bin/bash

mvn install:install-file -Dfile=LibBlinkCard.aar -DpomFile=pom.xml -DcreateChecksum=true -Djavadoc=LibBlinkCard-javadoc.jar