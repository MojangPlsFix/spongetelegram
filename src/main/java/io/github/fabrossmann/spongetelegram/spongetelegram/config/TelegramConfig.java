package io.github.fabrossmann.spongetelegram.spongetelegram.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class TelegramConfig {
    @Setting
    public String botName;
    @Setting
    public String key;
    @Setting
    public List<Integer> chatIds;
    @Setting
    public boolean firstUse;

    public void initializeDefaults() {
        botName = "---_bot";
        key = "enter api key";
        chatIds = new ArrayList<>();
        chatIds.add(0);
        firstUse = true;
    }


}
