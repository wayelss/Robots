package gui;
import java.text.MessageFormat;
import java.util.HashMap;

public class MessageFormatCache {
    private static HashMap<String, MessageFormat> cache = new HashMap<>();

    public static String checkPattern(String pattern, Object... args){
        MessageFormat messageFormat = cache.get(pattern);

        if(messageFormat == null){
            messageFormat = new MessageFormat(pattern);
            cache.put(pattern, messageFormat);
        }

        return messageFormat.format(args);
    }

}
