package com.smartjobai.ai.similarity;

import com.smartjobai.commons.util.TextUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TFIDFMatcher {

    public double calcularSimilaridade(String textoVaga, String textoCurriculo) {
        List<String> tokensVaga = tokenizar(textoVaga);
        List<String> tokensCurriculo = tokenizar(textoCurriculo);

        Set<String> vocabulario = new HashSet<>(tokensVaga);
        vocabulario.addAll(tokensCurriculo);

        Map<String, Double> tfidfVaga = calcularTFIDF(tokensVaga, vocabulario);
        Map<String, Double> tfidfCurriculo = calcularTFIDF(tokensCurriculo, vocabulario);

        return cossenoSimilaridade(tfidfVaga, tfidfCurriculo);
    }

    private List<String> tokenizar(String texto) {
        String limpo = TextUtils.cleanText(TextUtils.removeAccents(texto.toLowerCase()));
        
        // Split por espaços e caracteres de pontuação, mantendo apenas palavras
        return Arrays.stream(limpo.split("[\\s\\p{P}]+"))
                .filter(t -> !t.isEmpty() && t.length() > 1)  // Ignora palavras muito curtas
                .collect(Collectors.toList());
    }

    private Map<String, Double> calcularTFIDF(List<String> tokens, Set<String> vocabulario) {
        Map<String, Integer> freq = new HashMap<>();
        for (String t : tokens) {
            freq.put(t, freq.getOrDefault(t, 0) + 1);
        }
        Map<String, Double> tfidf = new HashMap<>();
        double totalTokens = Math.max(tokens.size(), 1);
        for (String termo : vocabulario) {
            double tf = freq.getOrDefault(termo, 0) / totalTokens;
            tfidf.put(termo, tf);
        }
        return tfidf;
    }

    private double cossenoSimilaridade(Map<String, Double> v1, Map<String, Double> v2) {
        Set<String> allKeys = new HashSet<>(v1.keySet());
        allKeys.addAll(v2.keySet());

        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (String key : allKeys) {
            double a = v1.getOrDefault(key, 0.0);
            double b = v2.getOrDefault(key, 0.0);
            dot += a * b;
            norm1 += a * a;
            norm2 += b * b;
        }
        if (norm1 == 0 || norm2 == 0) return 0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
