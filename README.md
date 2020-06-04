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

`java -jar mkvaudiosubtitleschanger.jar [path to mkv or dir with mkv]`

You have to replace the brackets and the content of it with the path to your mkv file or the directory with mkv files.

### config.yml example:

```
mkvtoolnixPath: /usr/bin
config:
  1:
    audio:
      - jpn
    subtitle:
      - ger
      - eng
  2:
    audio:
      - ger
      - eng
    subtitle:
      - OFF
```
Config file needs to be placed in the same directory as the jar.

