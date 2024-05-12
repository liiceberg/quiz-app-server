package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;

import okhttp3.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URL;

@Service
public class TriviaService {
    private final OkHttpClient client = new OkHttpClient();

    public String getCategories() throws IOException {
        URL url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("opentdb.com")
                .path("api_category.php")
                .build()
                .toUri()
                .toURL();
        return makeGetRequest(url);
    }

    public String getTrivia(Integer amount, Integer category, String difficulty, String type)
                                                                                    throws IOException {
        URL url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("opentdb.com")
                .path("api.php")
                .queryParam("amount", amount)
                .queryParam("category", category)
                .queryParam("difficulty", difficulty)
                .queryParam("type", type)
                .build()
                .toUri()
                .toURL();
        return makeGetRequest(url);
    }

    private String makeGetRequest(URL apiUrl) throws IOException {
        Request request = new Request.Builder().url(apiUrl).build();
        return getResponse(request);
    }

    private String makePostRequest(URL apiUrl, String requestBody) throws IOException {
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
