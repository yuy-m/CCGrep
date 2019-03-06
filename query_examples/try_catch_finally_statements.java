// Command: ccgrep -f try_catch_finally_statements.java -r TARGET/
// Matches try-catch-finally statement.

try { $$ }
$(  catch($$) { $$ }  $) $*
$(  finally { $$ }  $) $?
