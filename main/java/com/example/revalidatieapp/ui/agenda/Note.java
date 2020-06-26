package com.example.revalidatieapp.ui.agenda;

import com.google.firebase.Timestamp;

public class Note {

    private String title;
    private String body;
    private Timestamp time;

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
