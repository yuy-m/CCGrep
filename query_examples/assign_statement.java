// Command: ccgrep -f assign_statement.java -r TARGET/
// Matches statements that assigns one variable or one value literal.
// 'b' matches any variable.
// '0' matches any one-token literal: integral, floating, string, char, etc.

a = $( b $| 0 $) ;
