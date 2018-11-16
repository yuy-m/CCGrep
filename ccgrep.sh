#!/bin/bash
java -cp "target/classes;lib/antlr-runtime-4.7.1.jar;lib/jargp.jar" ccgrep.CCGrep $*
