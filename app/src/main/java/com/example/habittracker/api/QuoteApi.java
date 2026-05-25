package com.example.habittracker.api;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface QuoteApi {

    @GET("quotes")
    Call<List<Object>> getQuotes();
}