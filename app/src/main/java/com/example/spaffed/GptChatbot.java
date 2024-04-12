package com.example.spaffed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;

public class GptChatbot {
    public void sendMessage(String input, final OnMessageReceivedListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GptAPI gptAPI = retrofit.create(GptAPI.class);
        GptRequest request = new GptRequest(input, "gpt-3.5-turbo", 0.7, 150); // You can adjust the temperature and maxTokens as needed
        Call<GptResponse> call = gptAPI.createCompletion(request);
        call.enqueue(new Callback<GptResponse>() {
            @Override
            public void onResponse(Call<GptResponse> call, Response<GptResponse> response) {
                if (response.isSuccessful()) {
                    List<GptChoice> choices = response.body().getChoices();
                    if (choices != null && choices.size() > 0) {
                        String responseText = choices.get(0).getText();
                        listener.onMessageReceived(responseText);
                    } else {
                        listener.onMessageReceived("No response received.");
                    }
                } else {
                    listener.onMessageReceived("Error: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<GptResponse> call, Throwable t) {
                listener.onMessageReceived("Request failed: " + t.getMessage());
            }
        });
    }
    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }
}