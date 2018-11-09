#!/bin/bash
java -cp "target/classes;lib/antlr-4.7.1-complete.jar;lib/jargp.jar" ccgrep/CCGrep $*
