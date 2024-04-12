package com.example.spaffed;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class GptRequest {
    @SerializedName("model")
    private String model;
    @SerializedName("prompt")
    private String prompt;
    @SerializedName("temperature")
    private double temperature;

    public GptRequest(String prompt, String model, double temperature, int maxTokens) {
        this.model = model;
        this.prompt = prompt;
        this.temperature = temperature;
    }
}