package com.example.retrofitrxjava.event;

public class UpdateMain {
    public String mMessage;

    public UpdateMain(String message) {
        mMessage = message;
    }

    public UpdateMain() { }

    public String getMessage() {
        return mMessage;
    }
}
