package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;

import okhttp3.*;

import java.io.IOException;
import java.net.URL;

@Service
public class TriviaService {
    private final OkHttpClient client = new OkHttpClient();

    public String makeGetRequest(URL apiUrl) throws IOException {
        Request request = new Request.Builder().url(apiUrl).build();
        return getResponse(request);
    }

    public String makePostRequest(URL apiUrl, String requestBody) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);
        Request request = new Request.Builder().url(apiUrl).post(body).build();

        return getResponse(request);
    }

    private String getResponse(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        return body.string();
                    }
                }

            }
            throw new IOException("Unexpected response: " + response.code());
        }
    }
}
