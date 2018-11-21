CCGrep
====
*Now writing*

CCGrep is a lighthearted code clone detector like grep command.


## Description
Many code clone detectors already exist, but their install, configuration and execution are difficult.
CCGrep is a simple clone detector based on grep command interface and you can use it instantly.

CCGrep detects Type 1, 2 clones.

## Demo
## VS. 
## Requirement
## Usage
`$ ccgrep.sh [options]... [needleCode] haystackFiles...`
### Language
 - c[^1]: c/c++ detection has a bug in tokenizing preprocessor macros.
 - c++[^1]
 - Java
 - Python3

### Options
 - `-b` LEVEL : set [blind level](#Blind). none / consistent(by default) / full.
 - `-l` LANG  : set target language. c / c++ / java(by default) / python3.
                If you use `-f` option, the language is set automatically from the file extension.
 - `-p` MODE  : set printing option l/N/f/e/c like `-p lN`.
                When `l` set, print only file names.
                When `N` set, Not print line numbers.
                When `f` set, print whole code of clones.
                When `e` set with `f`, comment out the file name and line numbers.
                When `c` set, print only the count of clones.
 - `-f` FILE  : obtain needle from file. CANNOT give needle as code string at once.
 - `-h`       : show help

### Example
 - `$ ./ccgrep.sh -f needle.c c src`
 - `$ ./ccgrep.sh java "int a = 0;" src`


### Blind
CCGrep has token blind system.
For Java example `value.get()` matches either `object.toString()` or `integer.hashCode()`.

Each blindable tokens belong to Blind Group.
Blind Groups are now **Identifiers**, **Binary or unary operators**, and **Assign operators**.
Tokens in same group are potential treated as same token with the blind system.
On the other hand, ones in different groups are NEVER.

#### Mode
Blind level can be set by command line option `-b MODE`.
 - `none`       : Matches exact same tokens (No blind).
 - `consistent` : Matches any token in a same group but same tokens matches same ones(p-match).(**by default**)
 - `full`       : Matches any token in a same group.

### Fixed Token
Identifiers starting with `$` are fixed identifiers.
These identifiers matches exact same identifiers regardless of the [blind level](#Blind).
 [^2]: Assume that the languages uses no `$` in their grammar.


## Install

`$ ant compile`

## Contribution
## Licence
## Author
[yuy-mymt](http://sel.ist.osaka-u.ac.jp/)
