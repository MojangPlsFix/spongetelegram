package io.github.fabrossmann.spongetelegram.spongetelegram.telegram.components;

import java.util.UUID;

public class TelegramToMinecraft {
    UUID uuid;
    String content;
    int chatID;

    public TelegramToMinecraft(UUID uuid, String content, int chatID) {
        this.uuid = uuid;
        this.content = content;
        this.chatID = chatID;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }
}
