package io.github.fabrossmann.spongetelegram.spongetelegram.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class GlobalConfig {
    public GlobalConfig(){
        telegramConfig = new ArrayList<>();
    }

    @Setting
    public List<TelegramConfig> telegramConfig;
}
