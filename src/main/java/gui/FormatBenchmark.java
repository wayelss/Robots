package gui;

import java.text.MessageFormat;

public class FormatBenchmark {
    public static void main(String[] args) {
        int iterations = 1_000_000;
        int x = 150;
        int y = 200;
        long lengthOfText = 0;

        String formatterPattern = "X: %d, Y: %d";
        String messagePattern = "X: {0}, Y: {1}";



        // 1. Formatter (String.format)
        long startFormatter = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            String result = String.format(formatterPattern, x, y);
            lengthOfText += result.length();
        }
        long endFormatter = System.currentTimeMillis();
        long timeFormatter = endFormatter - startFormatter;
        System.out.println("1. Formatter (String.format) время:      " + timeFormatter + " мс");


        // 2. MessageFormat
        long startNoCache = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            String result = MessageFormat.format(messagePattern, x, y);
            lengthOfText += result.length();
        }
        long endNoCache = System.currentTimeMillis();
        long timeNoCache = endNoCache - startNoCache;
        System.out.println("2. MessageFormat (БЕЗ кэша) время:       " + timeNoCache + " мс");


        // 3. MessageFormatCache
        MessageFormatCache.checkPattern(messagePattern, x, y);

        long startWithCache = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            String result = MessageFormatCache.checkPattern(messagePattern, x, y);
            lengthOfText += result.length();
        }
        long endWithCache = System.currentTimeMillis();
        long timeWithCache = endWithCache - startWithCache;
        System.out.println("3. MessageFormatCache (С кэшем) время:   " + timeWithCache + " мс");

    }
}