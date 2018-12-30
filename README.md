CCGrep
====
*updated on 2018/12/29, written on 2018/12/06*

CCGrep is a easy-to-use code clone detector like *grep* command.

## Description
Many code clone detectors already exist, but their installation, configuration and execution are difficult to use.
CCGrep is a simple clone detector based on grep command interface and you can use it instantly.

CCGrep can detect Type 1, 2(p-match or not), 3 clones.

#### Language
 - C
 - C++
 - Java
 - Python3 (*experimental*)

## Demo
Output samples with target [**Apache HTTP Server**](http://httpd.apache.org/).

 - `ccgrep -r -l c -p n 'T a = b();' httpd-2.4.33/ `

```
httpd-2.4.33/modules/arch/unix/mod_unixd.c:141:    int rv = set_group_privs();
httpd-2.4.33/modules/arch/win32/mod_isapi.c:311:        apr_status_t rv = apr_get_os_error();
httpd-2.4.33/modules/cache/cache_util.c:286:    apr_time_t now = apr_time_now();
httpd-2.4.33/modules/cache/mod_socache_shmcb.c:583:    apr_time_t now = apr_time_now();
httpd-2.4.33/modules/cache/mod_socache_shmcb.c:687:    apr_time_t now = apr_time_now();
httpd-2.4.33/modules/cache/mod_socache_shmcb.c:865:    apr_time_t now = apr_time_now();
httpd-2.4.33/modules/http2/h2_session.c:1738:                apr_time_t now = apr_time_now();
```

 -  `ccgrep -r -l c -p nf 'if($$==$$){$$}' httpd-2.4.33/ `
```
httpd-2.4.33/modules/aaa/mod_access_compat.c
230:        if (wl == dl) {
231:            return 1;                /* matched whole thing */
232:        }
httpd-2.4.33/modules/aaa/mod_access_compat.c
322:    if (a->order[method] == ALLOW_THEN_DENY) {
323:        ret = HTTP_FORBIDDEN;
324:        if (find_allowdeny(r, a->allows, method)) {
325:            ret = OK;
326:        }
327:        if (find_allowdeny(r, a->denys, method)) {
328:            ret = HTTP_FORBIDDEN;
329:        }
330:    }
```

## VS.
## Requirement
 - Java8
 - Apache Ant *(required only to build, not to use)*

## Install
Optional.
Without install, `ccgrep` and `CCGrep.jar` must be in same directory.

#### Unix
`$ ./install`

#### Windows
*Not implemented yet. use ccgrep directly*

## Usage
 - `$ ccgrep [OPTIONS]... QUERY_CODE [TARGETS]...`
 - `$ ccgrep [OPTIONS]... -f QUERY_FILE [TARGETS]...`

#### Options
 - `-b,--blind <LEVEL>`     set blind level.
   - none(Type 1) / consistent(p-match)(by default) / full(Type 2).
 - `--exclude <FILE_PATTERN>`   skip files matching FILE_PATTERN.
 - `-f,--file <FILES>`      obtain query from file.
   - CANNOT give query as code string at once.
 - `--fix <ID>`         specify identifier to match exactly the same one.
 - `-h,--help`              show help.
 - `--include <FILE_PATTERN>`   search ONLY files that match FILE_PATTERN.
 - `   --json`              print clones and execution information with JSON format.
 - `-l,--language <LANG>`   set target language.
   - c / c++ / java(by default) / python3.
   - With `-f` option, the language can be inferred from the file extension.
 - `-m,--max-count <NUM>`   stop after NUM clones.
 - `-p,--print <OPTION>`    set printing option c/l/n/f/e like `-p fn`.
   - When `c` set, print ONLY the count of clones.
   - When `l` set, print ONLY file name per matched files.
   - When `h` set, NOT print file names.
   - When `n` set, print line numbers.
   - When `f` set, print whole code of clones.
   - When `e` set, comment out the file name and line numbers.
 - `-r,--recursive`         traverse directories recursively.
 - `-s,--stdin-query`       obtain query from standard input. CANNOT give
                        query as code string at once.
 - `--xml`               print clones with XML format.

#### Example
 - search recursively `target/` for `query.java`, and print head lines of clones with file name and line number.
   - `$ ccgrep -r -p n -f query.java target/`
 - search recursively `target/` for `'int a = 1;'`, and print whole lines of clones with file name and line number.
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
Note: assume that the language uses no `$` in their grammar.

You can also use `--fix` option to fix identifiers.

#### AnyTokenSequence `$$`
Special token `$$` in a query matches any token sequences (0 or more length) with balanced parentheses/braces/brackets
 (e.g. `{ set(get()).value(); }`).

Example: `if($$){$$ return v;}` detects if-statements returning value at the end.

Note: `$$` cannot use as first/final token in a query (e.g. `$$ { aaa }`, `{ aaa } $$`).

#### RegularExpression
Special tokens can be used for detection by regular expression.
They are regular expression characters with `$` at the head.
 - Grouping `$(`, `$)`
 - Longest Selection `$|`
   - matches longest. `$(a$|aa$)` matches whole `aa` of `aa`;
 - First Selection `$/`
   - matches first. `$(a$/aa$)` matches only first `a` of `aa`;
 - 0-or-more `$*`
   - matches longest.
 - 1-or-more `$+`
   - matches longest.
 - 0-or-1 `$?`
 - Any token `$.`

Note: Identifiers whose first appearance is in selections or repetitions are NOT used for p-match.
e.g. `a$*a` matches both `abc` and `aaa`. And `aa$*` matches `aaa` but not `abc`.


## Build

`$ ant`

## Contribution
## Licence
## Author
[yuy-mymt](http://sel.ist.osaka-u.ac.jp/)
