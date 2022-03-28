package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.AttributeConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.MkvToolNix;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;

@Log4j2
public class ConfigProcessorOld {
    private int audioDefault = - 1;
    private int subtitleDefault = - 1;
    private final AttributeConfig config;

    public ConfigProcessorOld(AttributeConfig config) {
        this.config = config;
    }

    /**
     * Processes the config lists and apply the changes if the combination matches
     *
     * @param file       is the file, which will be updated
     * @param attributes has the metadata for the transferred file
     * @return If the current configuration matched and changes applied or not
     */
    public boolean processConfig(File file, List<FileAttribute> attributes) {
        // check if size is bigger or equal 2 to make sure that there is at least one audio and subtitle line
        // TODO: implement empty audio or subtitle line
        List<FileAttribute> attributesCopy = new ArrayList<>(attributes);
        if(attributesCopy.size() >= 2){
            TransferObject transfer = filterAttributes(attributesCopy);
            if(! attributesCopy.isEmpty()){
                return updateFile(file, attributesCopy, transfer);
            }
        }
        return false;
    }

    /*
     * Filters the attributes that only those are remaining which are needed in the current configuration.
     * Also analyzes which tracks were default before.
     */
    private TransferObject filterAttributes(List<FileAttribute> attributes) {
        TransferObject transfer = new TransferObject();
        Iterator<FileAttribute> iterator = attributes.iterator();
        while(iterator.hasNext()){
            FileAttribute elem = iterator.next();
            if("audio".equals(elem.getType())){
                if(elem.isDefaultTrack()){
                    audioDefault = elem.getId();
                }
                if(config.getAudioLanguage().contains("OFF")){
                    transfer.setAudioOn(false);
                    transfer.setAudioIndex(- 2);
                }
                if(! config.getAudioLanguage().contains(elem.getLanguage())){
                    iterator.remove();
                }
            }else if("subtitles".equals(elem.getType())){
                if(elem.isDefaultTrack()){
                    subtitleDefault = elem.getId();
                }
                if(config.getSubtitleLanguage().contains("OFF")){
                    transfer.setSubtitleOn(false);
                    transfer.setSubtitleIndex(- 2);
                }
                if(! config.getSubtitleLanguage().contains(elem.getLanguage())){
                    iterator.remove();
                }
            }
        }
        return transfer;
    }

    /**
     * Creates the command which will be executed if the attributes of the current file fit the current {@link AttributeConfig}
     *
     * @param file       is the file, which will be updated
     * @param attributes has the metadata for the transferred file
     * @return if the current file was updated or not. Returns true if the file already has the correct metadata set
     */
    private boolean updateFile(File file, List<FileAttribute> attributes, TransferObject transfer) {
        StringBuilder stringBuffer = new StringBuilder();
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            stringBuffer.append(format("\"%s\" \"%s\" ",
                    Config.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT),
                    file.getAbsolutePath()));
        }else{
            stringBuffer.append(format("%s %s ",
                    Config.getInstance().getPathFor(MkvToolNix.MKV_PROP_EDIT),
                    file.getAbsolutePath()));
        }
        if(audioDefault != - 1){
            stringBuffer.append(format("--edit track:=%s --set flag-default=0 ", audioDefault));
        }
        if(subtitleDefault != - 1){
            stringBuffer.append(format("--edit track:=%s --set flag-default=0 ", subtitleDefault));
        }
        collectLines(attributes, transfer);
        if(transfer.isValid){
            if(transfer.isAudioOn){
                stringBuffer.append(format("--edit track:=%s --set flag-default=1 ", transfer.getAudioIndex()));
            }
            if(transfer.isSubtitleOn){
                stringBuffer.append(format("--edit track:=%s --set flag-default=1 ", transfer.getSubtitleIndex()));
            }
            if(subtitleDefault == transfer.getSubtitleIndex() && audioDefault == transfer.getAudioIndex()){
                /*
                 * In this case the file would be change to the exact same audio and subtitle lines and we want to
                 * avoid unnecessary changes to the file
                 */
                log.info("File already fits config: {}", file.getName());
                return true;
            }
            try{
                if(!Config.getInstance().isSafeMode()) {
                    Runtime.getRuntime().exec(stringBuffer.toString());
                }
            }catch(IOException e){
                log.error("Couldn't make changes to file");
            }
            /*
             * We return true even if there was an error. If there was an error, the chances that this file is still
             * busy later.
             */
            log.info("Updated {}", file.getName());
            return true;
        }else{
            return false;
        }
    }

    /**
     * Analyzes the left over attributes and decides which is the most wanted for audio and subtitle
     *
     * @param attributes contains all the leftover attributes
     * @return is an object, which contains information about which audio and subtitle line is the best suitable for
     * the entered config. Also transfers a boolean which contains information about if the other two values
     * were set
     */
    private TransferObject collectLines(List<FileAttribute> attributes, TransferObject transfer) {
        int subtitleListIndex = - 1;
        int audioListIndex = - 1;
        for(FileAttribute elem : attributes){
//            if("audio".equals(elem.getType())){
//                for(int i = 0; i < config.getAudio().size(); i++){
//                    audioListIndex = findIndex("audio", elem, audioListIndex, config.getAudio(), transfer);
//                }
//            }else if("subtitles".equals(elem.getType())){
//                for(int i = 0; i < config.getSubtitle().size(); i++){
//                    subtitleListIndex = findIndex("subtitles", elem, subtitleListIndex, config.getSubtitle(), transfer);
//                }
//            }
        }

        transfer.analyzeIfValid();
        return transfer;
    }

    private int findIndex(String type, FileAttribute elem, int index, List<String> config, TransferObject transfer) {
        for(int i = 0; i < config.size(); i++){
            if(config.get(i).equals(elem.getLanguage()) && (index == - 1 || i < index)){
                switch(type){
                    case "audio":
                        transfer.setAudioIndex(elem.getId());
                        break;
                    case "subtitles":
                        transfer.setSubtitleIndex(elem.getId());
                        break;
                }
                return i;
            }
        }
        return index;
    }

    @Getter
    @Setter
    private static class TransferObject {
        private boolean isValid;
        private int audioIndex = - 1;
        private int subtitleIndex = - 1;
        private boolean isSubtitleOn = true;
        private boolean isAudioOn = true;

        TransferObject() {
        }

        private void analyzeIfValid() {
            isValid = audioIndex != - 1 && subtitleIndex != - 1;
        }
    }
}