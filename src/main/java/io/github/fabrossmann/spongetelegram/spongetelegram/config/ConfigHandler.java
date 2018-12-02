package io.github.fabrossmann.spongetelegram.spongetelegram.config;

import com.google.common.reflect.TypeToken;
import io.github.fabrossmann.spongetelegram.spongetelegram.Spongetelegram;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigHandler {

    public static GlobalConfig loadConfig() throws ObjectMappingException, IOException {
        Spongetelegram instance = Spongetelegram.getInstance();
        Logger logger = instance.getLogger();
        Path configDir = instance.getConfigDir();

        if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
        }

        Path configFile = Paths.get(configDir + "/spongetelegram.conf");
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(configFile).build();
        ConfigurationNode rootNode = loader.createEmptyNode(ConfigurationOptions.defaults());

        GlobalConfig config = rootNode.getValue(TypeToken.of(GlobalConfig.class), new GlobalConfig());

        if (!Files.exists(configFile)) {
            Files.createFile(configFile);

            TelegramConfig telegramConfig= new TelegramConfig();
            telegramConfig.initializeDefaults();

            config.telegramConfig.add(telegramConfig);
            logger.info("[SpongeTelegram] Config created and initialized!");
        }

        rootNode.setValue(TypeToken.of(GlobalConfig.class), config);

        loader.save(rootNode);
        logger.info("[SpongeTelegram] Config loaded!");

        return config;
    }

}
