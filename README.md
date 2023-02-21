## Introduction

This program helps to change audio and subtitle tracks of mkv files without rewriting the file. Only track properties will be updated.

## Requirements

 - Java 11 or higher
 - mkvtoolnix installation
 
## Execution
**Minimal usage:**
`java -jar mkvaudiosubtitlechanger.jar --library "X:/Files" --attribute-config eng:ger eng:OFF`

**Safe usage (best for testing before applying to whole library):**
`java -jar mkvaudiosubtitlechanger.jar --library "X:/Files" --attribute-config eng:ger eng:OFF --safe-mode`

Attribute-config must be entered in pairs: `audio:subtitle`; Example: `jpn:eng`. More about this topic
[here](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/wiki/Attribute-Config).

## Available parameters
```shell
   -l,--library <arg>                Path to library
   -a,--attribute-config <arg>       Attribute config to decide which tracks to choose when
   -p,--config-path <arg>            Path to config file
   -m,--mkvtoolnix <arg>             Path to mkv tool nix installation
   -s,--safe-mode                    Test run (no files will be changes)
   -t,--threads <arg>                Thread count (default: 2)
   -i,--include-pattern <arg>        Include files matching pattern (default: ".*")
   -e,--exclude-directories <arg>    Directories to be excluded, combines with config file
   -fk,--forced-keywords <arg>       Additional keywords to identify forced tracks
   -ck,--commentary-keywords <arg>   Additional keywords to identify commentary tracks
   -v,--version                      Display version
   -h,--help                         "For help this is" - Yoda
```

All parameters can also be defined in a config file. 
Please read [this wiki page](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/wiki/How-to-config-file)  for more information.

## Build requirements
- JDK 11 or higher
- Maven 3
- Git

## Build from source
```shell
git clone https://github.com/RatzzFatzz/MKVAudioSubtitleChanger.git
cd MKVAudioSubtitleChanger
mvn package
```
