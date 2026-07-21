package com.smartjobai.commons.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextUtils {

    private TextUtils() {
    }

    public static String removeAccents(String text) {
        if (text == null) return null;
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public static String cleanText(String text) {
        if (text == null) return null;
        return text.replaceAll("[^a-zA-Z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
