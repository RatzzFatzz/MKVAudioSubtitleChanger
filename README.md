## Introduction

This program helps to change audio and subtitle tracks of mkv files without rewriting the file. Only track properties will be updated.
![](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/blob/master/example.gif)

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
```
 -l,--library-path <arg>           Path to library
 -a,--attribute-config <arg>       Attribute config to decide which tracks to choose when
 -p,--config-path <arg>            Path to config file
 -m,--mkvtoolnix <arg>             Path to mkv tool nix installation
 -s,--safe-mode                    Test run (no files will be changes)
 -c,--coherent <arg>               Try to match all files in dir of depth with the same config
 -cf,--force-coherent              Force coherent and don't update anything if config fits not whole config (default: false)
 -n,--only-new-files               Sets filter-date to last successful execution (Overwrites input of filter-date)
 -d,--filter-date <arg>            Only consider files created newer than entered date (format: "dd.MM.yyyy-HH:mm:ss")
 -t,--threads <arg>                Thread count (default: 2)
 -i,--include-pattern <arg>        Include files matching pattern (default: ".*")
 -e,--excluded-directories <arg>   Directories to be excluded, combines with config file
 -fk,--forced-keywords <arg>       Additional keywords to identify forced tracks
 -ck,--commentary-keywords <arg>   Additional keywords to identify commentary tracks
 -ps,--preferred-subtitles <arg>   Additional keywords to prefer specific subtitle tracks
 -v,--version                      Display version
 -h,--help                         "For help this is" - Yoda
```
If you need more information about how each parameter works, check out [this wiki page](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/wiki/Parameters).

All parameters can also be defined in a [config file](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/wiki/How-to-config-file).

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
