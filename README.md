CCGrep
====
*Now writing.*

CCGrep is a easy-to-use code clone detector like *grep* command.

## Description
Many code clone detectors already exist, but their install, configuration and execution are difficult to use.
CCGrep is a simple clone detector based on grep command interface and you can use it instantly.

CCGrep can detect Type 1, 2(p-match or not) clones.

#### Language
 - C
 - C++
 - Java
 - Python3 (*experimental*)

## Demo
## VS.
## Requirement
 - Java8
 - Apache Ant *(for build)*

## Install
Optional.
Without install, `ccgrep` and `CCGrep.jar` must be in same directory.

#### Unix
`$ ./install`

#### Windows
*Not implemented yet*

## Usage
 - `$ ccgrep [OPTIONS]... QUERY_CODE [TARGETS]...`
 - `$ ccgrep [OPTIONS]... -f QUERY_FILE [TARGETS]...`

#### Options
 - `-b,--blind <LEVEL>`     set blind level. none(Type 1) /
                        consistent(p-match)(by default) / full(Type 2).
 - `-f,--file <FILES>`      obtain needle from file. CANNOT give needle as
                        code string at once.
 - `-h,--help`              show help.
 - `   --json`              print clones with JSON format (*experimental*).
 - `-l,--language <LANG>`   set target language. c / c++ / java(by default) /
                        python3. With `-f` option, the language can be
                        inferred from the file extension.
 - `-p,--print <OPTION>`    set printing option c/l/n/f/e like `-p fn`. When
                        `c` set, print ONLY the count of clones. When `l`
                        set, print ONLY file name per matched files. When
                        `h` set, NOT print file names. When `n` set, print
                        line numbers. When `f` set, print whole code of
                        clones. When `e` set, comment out the file name
                        and line numbers.
 - `-r,--recursive`         traverse directories recursively.

#### Example
 - `$ ccgrep -r -p n -f query.java target/`
 - `$ ccgrep -r -p fn 'int a = 1;' target/`

Note: to specify a query code, you should use SINGLE quotes `'` instead of DOUBLE quotes `"` because the variable expansion leads to unexpected results.

#### ClonesToDetect
Clone type to detect can be set by command line option `-b MODE`.
 - `none`       : Each token matches exact same token (Type 1 clones).
 - `full`       : Each *identifier* token matches any *identifier* token. also *literal* token (Type 2).
 - `consistent` : Same as `full`, except that same tokens in the query match same ones in targets (p-match clones).(**by default**)
clones).

#### FixedIdentifier `$id`
In a query, identifiers can starts with `$` (e.g. `$value`).
These identifiers match exact same identifiers regardless of the [blind level](#ClonesToDetect).

Note: No spaces are allowed between `$` and identifier.  
Note: assume that the languages uses no `$` in their grammar.

#### AnyTokenSequence `$$`
Special token `$$` in a query matches any token sequences (0 or more length) with balanced parentheses/braces/brackets
 (e.g. `{ set(get()).value(); }`).

Example: `if($$){$$ return v;}` detects if-statements returning value at the end.

Note: `$$` cannot use as first/final token in a query (e.g. `$$ { aaa }`, `{ aaa } $$`).

## Build

`$ ant`

## Contribution
## Licence
## Author
[yuy-mymt](http://sel.ist.osaka-u.ac.jp/)
