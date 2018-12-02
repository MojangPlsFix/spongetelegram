package io.github.fabrossmann.spongetelegram.spongetelegram.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class TelegramConfig {

    public void initializeDefaults(){
        botName="";
        key="";
    }

    @Setting
    public String botName;

    @Setting
    public String key;
}
