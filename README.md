### Table of content
 - Introduction
 - Requirements
 - Running
 - Configuration
 - Additional parameters
 
### Introduction

This program helps to change audio and subtitle lines of mkv files.

### Requirements

 - Java 8 or higher
 - mkvtoolnix installation
 
### Running

1. Extract downloaded archive
2. Copy `config-template.yaml` to `config.yaml`
3. Update `config.yaml` to fit your needs
4. Open terminal / cmd in the directory of the jar and the config file 
5. Execute following commands:
   1. (Optional) `java -jar mkvaudiosubtitleschanger.jar -l [path to mkv or dir with mkv] --safe-mode`
   2. To permanently apply changes: `java -jar mkvaudiosubtitleschanger.jar -l [path to mkv or dir with mkv]`

### Configuration

Config file needs to be placed in the same directory as the jar or path to config has to be passed via command line
argument.

The list of language configurations can be expanded. Use `OFF` if you want to turn of the audio or subtitle lane. 
Players probably will display forced subtitles nonetheless.
```yaml
config:
  1:
    audio: ger
    subtitle: OFF
  2:
    audio: eng
    subtitle: ger
```
Subtitle lanes recognized as forced will be set as one. Already existing ones will not be overwritten or changed.


### Additional arameters
These properties overwrite already existing values in the config file.
```properties
 -c,--config <arg>                Path to config file
 -e,--exclude-directories <arg>   Directories to be excluded, combines with config file
 -h,--help                        "for help this is" - Yoda
 -i,--include-pattern <arg>       Include files matching pattern
 -k,--forcedKeywords <arg>        Additional keywords to identify forced tracks, combines with config file
 -l,--library <arg>               Path to library
 -m,--mkvtoolnix <arg>            Path to mkv tool nix installation
 -s,--safe-mode                   Test run (no files will be changes)
 -t,--threads <arg>               thread count (default: 2)
 -v,--version                     Display version
```