package com.example.spaffed;

import com.google.gson.annotations.SerializedName;

public class GptChoice {
    @SerializedName("text")
    private String text;

    public String getText() {
        return text;
    }
}
