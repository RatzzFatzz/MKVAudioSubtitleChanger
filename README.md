### Table of content
 - Introduction
 - Requirements
 - Running
 - config.yml example
 
### Introduction

This program helps changing audio and subtitle lines of mkv files.

### Requirements

 - Java 8 or higher
 - mkvtoolnix installation
 
### Running

Opening terminal / cmd in the directory of the jar and the config file and execute following command:

`java -jar mkvaudiosubtitleschanger.jar -l [path to mkv or dir with mkv]`

### Additional arameters
```properties
 -c,--config                      path to config
 -e,--exclude-directories <arg>   Directories to exclude
 -h,--help                        "for help this is" - Yoda
 -k,--forcedKeywords <arg>        Additional keywords to identify forcedtracks"
 -l,--library <arg>               path to library
 -s,--safe-mode                   Test run (no files will be changes)
 -t,--threads <arg>               thread count
```

### config.yml example
Config file needs to be placed in the same directory as the jar or path to config has to be passed via command line 
argument.

```yaml
mkvtoolnixPath: C:\Program Files\MKVToolNix
# Recommendations for data stored on HDDs, increase when using SSDs
threads: 2
#forcedKeywords: ["forced", "signs"]
config:
  1:
    audio: ger
    subtitle: OFF
  2:
    audio: eng
    subtitle: ger
```
Subtitle lanes recognized as forced will be set as one. Already existing ones will not be overwritten or changed.
