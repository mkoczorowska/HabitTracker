package com.example.habittracker.utils;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuoteApiService {

    public interface QuoteCallback {
        void onSuccess(String quote, String author);
        void onError();
    }

    private static final OkHttpClient client = new OkHttpClient();

    public static void fetchMotivationalQuote(QuoteCallback callback) {
        // Using quotable.io - free public API, no key needed
        Request request = new Request.Builder()
                .url("https://api.quotable.io/random?tags=motivational,wisdom&maxLength=100")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(callback::onError);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    new Handler(Looper.getMainLooper()).post(callback::onError);
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONObject json = new JSONObject(body);
                    String content = json.getString("content");
                    String author = json.getString("author");
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(content, author));
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(callback::onError);
                }
            }
        });
    }
}