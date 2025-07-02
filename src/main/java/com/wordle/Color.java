package com.wordle;

public enum Color {

    GREEN("\u001B[32m"), YELLOW("\u001B[33m"), GRAY("\u001B[0m");

    private final String code;

    Color(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
