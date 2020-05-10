package at.pcgamingfreaks.mkvaudiosubtitlechanger.config;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.MKVToolProperties;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.FileAttribute;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class ConfigProcessor {
    private int audioDefault = - 1;
    private int subtitleDefault = - 1;
    private final AttributeConfig config;

    public ConfigProcessor(AttributeConfig config) {
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
        if(attributes.size() >= 2){
            TransferObject transfer = filterAttributes(attributes);
            if(! attributes.isEmpty()){
                return updateFile(file, attributes, transfer);
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
                if(config.getAudio().contains("OFF")){
                    transfer.setAudioOn(false);
                    transfer.setAudioIndex(- 2);
                }
                if(! config.getAudio().contains(elem.getLanguage())){
                    iterator.remove();
                }
            }else if("subtitles".equals(elem.getType())){
                if(elem.isDefaultTrack()){
                    subtitleDefault = elem.getId();
                }
                if(config.getSubtitle().contains("OFF")){
                    transfer.setSubtitleOn(false);
                    transfer.setSubtitleIndex(- 2);
                }
                if(! config.getSubtitle().contains(elem.getLanguage())){
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
     * @return if the the current file was updated or not. Returns true if the file already has the correct metadata set
     */
    private boolean updateFile(File file, List<FileAttribute> attributes, TransferObject transfer) {
        StringBuilder stringBuffer = new StringBuilder("\"");
        stringBuffer.append(MKVToolProperties.getInstance().getMkvpropeditPath());
        stringBuffer.append("\" \"").append(file.getAbsolutePath()).append("\" ");
        if(audioDefault != - 1){
            stringBuffer.append("--edit track:=").append(audioDefault).append(" --set flag-default=0 ");
        }
        if(subtitleDefault != - 1){
            stringBuffer.append("--edit track:=").append(subtitleDefault).append(" --set flag-default=0 ");
        }
        collectLines(attributes, transfer);
        if(transfer.isValid){
            if(transfer.isAudioOn){
                stringBuffer.append("--edit track:=").append(transfer.getAudioIndex()).append(" --set flag-default=1 ");
            }
            if(transfer.isSubtitleOn){
                stringBuffer.append("--edit track:=").append(transfer.getSubtitleIndex()).append(" --set flag-default=1 ");
            }
            if(subtitleDefault == transfer.getSubtitleIndex() && audioDefault == transfer.getAudioIndex()){
                /*
                 * In this case the file would be change to the exact same audio and subtitle lines and we want to
                 * avoid unnecessary changes to the file
                 */
                return true;
            }
            try{
                Runtime.getRuntime().exec(stringBuffer.toString());
            }catch(IOException e){
                log.error("Couldn't make changes to file");

            }
            /*
             * We return true even if there was an error. If there was an error, the chances that this file is still
             * busy later.
             */
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
            if("audio".equals(elem.getType())){
                for(int i = 0; i < config.getAudio().size(); i++){
                    audioListIndex = findIndex("audio", elem, audioListIndex, config.getAudio(), transfer);
                }
            }else if("subtitles".equals(elem.getType())){
                for(int i = 0; i < config.getSubtitle().size(); i++){
                    subtitleListIndex = findIndex("subtitles", elem, subtitleListIndex, config.getSubtitle(), transfer);
                }
            }
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
