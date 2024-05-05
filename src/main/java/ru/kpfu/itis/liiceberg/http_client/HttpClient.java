package ru.kpfu.itis.liiceberg.http_client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClient {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";


    public String get(String url, Map<String, String> params) {
        try {
            StringBuilder str = new StringBuilder(url);
            str.append("?");

            for (String key : params.keySet()) {
                str.append(key);
                str.append("=");
                str.append(params.get(key));
                str.append("&");
            }
            str.deleteCharAt(str.length() - 1);
            HttpURLConnection connection = (HttpURLConnection) new URL(str.toString()).openConnection();

            connection.setRequestMethod(GET);
            connection.setRequestProperty("Content-Type", "application/json");

            String info = read(connection);
            connection.disconnect();
            return info;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String post(String url, Map<String, String> params) {
        return makeRequest(url, params, POST);
    }

    public String put(String url, Map<String, String> params) {
        return makeRequest(url, params, PUT);
    }

    public String delete(String url, Map<String, String> params) {
        return makeRequest(url, params, DELETE);
    }

    private String makeRequest(String url, Map<String, String> params, String method) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization",
                    "Bearer c3e8c3246ac69365d429b0a6a69ad1f04bc6f83b6596039cafb5a48a84d3baa6");
            connection.setDoOutput(true);

            String jsonInput = new JSONObject(params).toString();

            try (OutputStream out = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                out.write(input, 0, input.length);
            }
            String info = read(connection);
            connection.disconnect();
            return info;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String read(HttpURLConnection connection) throws IOException {
        if (connection != null) {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }
            }
            return content.toString();
        }
        return null;
    }
}
