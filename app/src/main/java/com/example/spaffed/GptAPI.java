package com.example.spaffed;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GptAPI {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("/chat/completions")
    Call<GptResponse> createCompletion(@Body GptRequest request);
}
