package net.plazmix.core.connection.http;

import lombok.NonNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBuilder {

    private final String urlString;

    private final Map<String, String> parameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    private String requestMethod = "GET";

    private boolean hasParams;

    public RequestBuilder(@NonNull String urlString) {
        this.urlString = urlString;
        this.hasParams = urlString.contains("?");
    }

    public RequestBuilder parameter(@NonNull String key, @NonNull String value) {
        parameters.put(key, value);
        return this;
    }

    public RequestBuilder header(@NonNull String key, @NonNull String value) {
        headers.put(key, value);
        return this;
    }

    public RequestBuilder method(@NonNull String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    private URL buildUrl() throws MalformedURLException {
        if (parameters.isEmpty()) {
            return new URL(urlString);
        }

        return new URL(urlString
                .concat(hasParams ? "" : "?")
                .concat(parameters.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + (entry.getValue() != null ? escape(entry.getValue()) : ""))
                        .collect(Collectors.joining("&"))));
    }

    public String makeRequest() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) buildUrl().openConnection();

            //добавляем хеадеры
            connection.setRequestProperty("User-Agent", "tynixcore");
            connection.setRequestProperty("Content-Type", "application/json");
            headers.forEach(connection::setRequestProperty);

            connection.setRequestMethod(requestMethod);
            connection.setReadTimeout(2_000);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                return content.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String escape(String data) {
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
