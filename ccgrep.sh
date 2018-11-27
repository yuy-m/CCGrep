#!/bin/bash
DNAME=`dirname ${0}`
java -cp "$DNAME/target/classes;$DNAME/lib/antlr-runtime-4.7.1.jar;$DNAME/lib/jargp.jar" jp.ac.osaka_u.ist.sel.ccgrep.CCGrep "$@"
