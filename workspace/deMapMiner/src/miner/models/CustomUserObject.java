package miner.models;

import java.io.Serializable;

public class CustomUserObject implements Serializable {

    private String EmailMessageId = null;
    private String authorName = null;

    public CustomUserObject(String id, String title) {

        this.EmailMessageId = id;
        this.authorName = title;

    }

    public CustomUserObject() {

    }

    public String getId() {
        return this.EmailMessageId;
    }

    public String getTitle() {
        return this.authorName;
    }

    public void setId(String id) {
        this.EmailMessageId = id;
    }

    public void setSutTitle(String title) {
        this.authorName = title;
    }

    @Override
    public String toString() {
        return this.authorName;

    }  
}