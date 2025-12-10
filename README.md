## Introduction

A streamlined solution for managing MKV files, this program leverages MKVToolNix to modify audio and subtitle track properties without the need for time-consuming file reencoding. Users can easily set their track preferences, and the application intelligently applies the best matching configuration. The tool focuses on metadata modification rather than full file rewriting, ensuring quick operations while maintaining the original file integrity. This makes it an ideal choice for managing multilingual media collections or batch processing multiple MKV files.

![](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/blob/master/example.gif)

## Requirements

 - Java 21 or newer
 - mkvtoolnix installation
 
## Execution
Portable: 
```
java -jar mkvaudiosubtitlechanger-<version>.jar --library "X:/Files" --attribute-config eng:ger eng:OFF
```
Windows & Linux (installed): 
```
mkvaudiosubtitlechanger --library "X:/Files" --attribute-config eng:ger eng:OFF
```

Add `--safe-mode` oder `-s` to not change any files. This is recommended for the first executions.

Attribute-config must be entered in pairs: `audio:subtitle`; Example: `jpn:eng`. More about this topic
[here](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/wiki/Attribute-Config).

### Available parameters
```
* -a, --attribute-config=<attributeConfig>...
                            List of audio:subtitle pairs used to match in order
                              and update files accordingly (e.g. jpn:eng jpn:
                              ger)
* -l, --library=<libraryPath>
                            path to library
  -m, --mkvtoolnix=<mkvToolNix>
                            path to mkvtoolnix installation
  -s, --safemode            test run (no files will be changes)
  -t, --threads=<threads>   thread count (default: 2)
  -c, --coherent=<coherent> try to match all files in dir of depth with the
                              same attribute config
      -cf, --force-coherent changes are only applied if it's a coherent match
  -n, --only-new-file       sets filter-date to last successful execution
                              (overwrites input of filter-date)
  -d, --filter-date=<filterDate>
                            only consider files created newer than entered date
                              (format: "dd.MM.yyyy-HH:mm:ss")
  -i, --include-pattern=<includePattern>
                            include files matching pattern (default: ".*")
  -e, --excluded=<excluded>...
                            Directories and files to be excluded (no wildcard)
  -o, -overwrite-forced     remove all forced flags
      --forced-keywords=<forcedKeywords>[, <forcedKeywords>...]...
                            Keywords to identify forced tracks (Defaults will
                              be overwritten; Default: forced, signs, songs)
      --commentary-keywords=<commentaryKeywords>[, <commentaryKeywords>...]...
                            Keywords to identify commentary tracks (Defaults
                              will be overwritten; Default: comment,
                              commentary, director)
      --hearing-impaired=<hearingImpaired>[, <hearingImpaired>...]...
                            Keywords to identify hearing impaired tracks
                              (Defaults will be overwritten; Default: SDH
      --preferred-subtitles=<preferredSubtitles>[, <preferredSubtitles>...]...
                            Keywords to prefer specific subtitle tracks
                              (Defaults will be overwritten; Default: unstyled)
      --debug               Enable debug logging
  -h, --help                Show this help message and exit.
  -V, --version             Print version information and exit.
```
If you need more information how each parameter works, check out [this wiki page](https://github.com/RatzzFatzz/MKVAudioSubtitleChanger/wiki/Parameters-v4).

All parameters can also be defined in a [config file](https://picocli.info/#_argument_files_for_long_command_lines).

## Build requirements
- JDK 21 or newer
- Maven 3
- Git

## Build from source
```shell
git clone https://github.com/RatzzFatzz/MKVAudioSubtitleChanger.git
cd MKVAudioSubtitleChanger
mvn clean package -Pportable
```
