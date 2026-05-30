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

    private static final String[] FALLBACK_QUOTES = {
            "Małe kroki każdego dnia prowadzą do wielkich celów.|Anonimowy",
            "Dyscyplina to most między celami a osiągnięciami.|Jim Rohn",
            "Nie musisz być świetny, żeby zacząć. Ale musisz zacząć, żeby być świetnym.|Zig Ziglar",
            "Sukces to suma małych wysiłków powtarzanych dzień po dniu.|Robert Collier",
            "Twoje nawyki kształtują twoją tożsamość.|James Clear",
            "Każdy dzień to nowa szansa na lepszą wersję siebie.|Anonimowy",
            "Konsekwencja jest ważniejsza niż perfekcja.|Anonimowy",
            "Zacznij tam, gdzie jesteś. Użyj tego co masz. Rób co możesz.|Arthur Ashe"
    };

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(4, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    public static void fetchMotivationalQuote(QuoteCallback callback) {

        Request request = new Request.Builder()
                .url("https://zenquotes.io/api/random")
                .header("User-Agent", "HabitTrackerApp/1.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                deliverFallback(callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    deliverFallback(callback);
                    return;
                }
                try {
                    String body = response.body().string();
                    JSONArray arr = new JSONArray(body);
                    JSONObject obj = arr.getJSONObject(0);
                    String quote  = obj.getString("q");
                    String author = obj.getString("a");
                    // Odrzuć "Unknown" lub zbyt długie cytaty
                    if (quote.length() > 120 || author.equals("Unknown")) {
                        deliverFallback(callback);
                        return;
                    }
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(quote, author));
                } catch (Exception e) {
                    deliverFallback(callback);
                }
            }
        });
    }

    private static void deliverFallback(QuoteCallback callback) {
        int idx = (int) (Math.random() * FALLBACK_QUOTES.length);
        String[] parts = FALLBACK_QUOTES[idx].split("\\|");
        String quote  = parts[0];
        String author = parts.length > 1 ? parts[1] : "Anonimowy";
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(quote, author));
    }
}