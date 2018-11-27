CCGrep
====
*Now writing*

CCGrep is a lighthearted code clone detector like grep command.


## Description
Many code clone detectors already exist, but their install, configuration and execution are difficult.
CCGrep is a simple clone detector based on grep command interface and you can use it instantly.

CCGrep detects Type 1, 2(p-match or not) clones.

## Demo
## VS. 
## Requirement
 - Java8
 - Apache Ant *(for build)*

## Usage
 - `$ ccgrep.sh [options]... needleCode haystackFiles...`
 - `$ ccgrep.sh [options]... -f needleFile haystackFiles...`

For Windows, use `ccgrep.bat` instead of `ccgrep.sh`.

### Language
 - c [^c-bug]
 - c++ [^c-bug]
 - Java
 - Python3

[^c-bug]:c/c++ detection has a bug in tokenizing preprocessor macros.

### Options
 - `-b` LEVEL : set [blind level](#Blind). none / consistent(by default) / full.
 - `-l` LANG  : set target language. c / c++ / java(by default) / python3.
                If you use `-f` option, the language is set automatically from the file extension.
 - `-p` MODE  : set printing option l/N/f/e/c like `-p lN`.
                When `l` set, print only file names.
                When `N` set, Not print line numbers.
                When `f` set, print whole code of clones.
                When `e` set, comment out the file name and line numbers.
                When `c` set, print only the count of clones.
 - `-f` FILE  : obtain needle from file. CANNOT give needle as code string at once.
 - `-h`       : show help

### Example
 - `$ ./ccgrep.sh -f needle.c src/`
 - `$ ./ccgrep.sh 'int a = 0;' src/`

Note: You should not use double quotation `"` to specify needle code because it leads to variable expansion.

### Blind
CCGrep has token blind system, which enables ccgrep to detect type 2 clones.
For Java example, `value.get()` matches either `object.toString()` or `integer.hashCode()`.

Each blindable tokens belong to Blind Group.
Blind Groups are now **Identifiers** and **Literals** (**Literals** only `none` or `full`.).

Tokens in same group are potential treated as same token with the blind system.
On the other hand, ones in different groups are NEVER.

#### Mode
Blind level can be set by command line option `-b MODE`.
 - `none`       : Matches exact same tokens (Type 1 clones).
 - `consistent` : Matches any token in a same group but same tokens matches same ones(p-match clones).(**by default**)
 - `full`       : Matches any token in a same group (All type 2 clones).

### Fixed Token
Identifiers starting with `$` are fixed identifiers.
These identifiers matches exact same identifiers regardless of the [blind level](#Blind). [^dollar-assumption]

[^dollar-assumption]: Assume that the languages uses no `$` in their grammar.


## Install

`$ ant compile`

## Contribution
## Licence
## Author
[yuy-mymt](http://sel.ist.osaka-u.ac.jp/)
