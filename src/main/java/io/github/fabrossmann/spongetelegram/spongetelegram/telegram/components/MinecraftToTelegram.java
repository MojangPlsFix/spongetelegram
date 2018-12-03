package io.github.fabrossmann.spongetelegram.spongetelegram.telegram.components;

public class MinecraftToTelegram {
    public String text;
    public int chat_id;
    public String parse_mode;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getParse_mode() {
        return parse_mode;
    }

    public void setParse_mode(String parse_mode) {
        this.parse_mode = parse_mode;
    }
}
