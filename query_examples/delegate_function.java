// Command: ccgrep -f assign_statement.java -r TARGET/
// Matches functions that calls a function of an object.
// Two 'f' matches the same function name.

T f()
{
    return a.f();
}
