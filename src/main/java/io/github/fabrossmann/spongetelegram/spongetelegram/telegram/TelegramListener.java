package io.github.fabrossmann.spongetelegram.spongetelegram.telegram;

import io.github.fabrossmann.spongetelegram.spongetelegram.telegram.components.MinecraftToTelegram;

public interface TelegramListener {
    void onSendToTelegram(MinecraftToTelegram chat);

    void onSendToMinecraft(MinecraftToTelegram chatMsg);
}
