package com.penny.pennytracker;

import org.springframework.stereotype.Component;

@Component("emoji")
public class EmojiUtil {

    public String get(String category) {

        if (category == null) return "🔖";

        return switch (category) {
            case "Food" -> "🍔";
            case "Travel" -> "🚕";
            case "Shopping" -> "🛍️";
            case "Bills" -> "💡";
            case "Entertainment" -> "🎬";
            default -> "🔖";
        };
    }
}
