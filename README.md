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

### config.yml example
Config file needs to be placed in the same directory as the jar.

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
This config will first check if there is japanese audio and german or english subtitles available, if yes,
it will set these attributes. If these are not available, it will check the second part. This means, it checks
if german or english audio is available. It does not care for the subtitle, because it's "off", which means, it 
will disable subtitles in this case.
