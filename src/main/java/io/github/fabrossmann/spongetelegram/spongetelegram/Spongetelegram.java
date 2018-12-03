package io.github.fabrossmann.spongetelegram.spongetelegram;

import com.google.inject.Inject;
import io.github.fabrossmann.spongetelegram.spongetelegram.config.ConfigHandler;
import io.github.fabrossmann.spongetelegram.spongetelegram.config.GlobalConfig;
import io.github.fabrossmann.spongetelegram.spongetelegram.telegram.Telegram;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "spongetelegram", name = "Spongetelegram", description = "TelegramConnector Integration for SprongeAPI")
public class Spongetelegram {

    public static Spongetelegram instance;
    public static Telegram telegram;
    GlobalConfig config;
    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfig;

    public static Spongetelegram getInstance() {
        return instance;
    }

    @Listener
    public void preServerStart(GamePreInitializationEvent event) {
        Spongetelegram.instance = this;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        Task.Builder taskBuilder = Task.builder().async().interval(1, TimeUnit.SECONDS).delay(1, TimeUnit.SECONDS);
        reload();

        telegram = new Telegram();
        telegram.auth(config.telegramConfig.get(0).key);

        logger.info("Telegram connecting!");
        taskBuilder.execute(
                () -> {
                    boolean connectionLost = false;
                    if (connectionLost) {
                        boolean success = telegram.reconnect();
                        if (success) {
                            connectionLost = false;
                            logger.info("Telegram connected");
                        } else {
                            logger.error("Telegram Connection error!");
                        }

                    }
                    if (telegram.connected) {
                        connectionLost = !telegram.getUpdate();
                    }
                }
        ).submit(this);

        logger.info("[SpongeTelegram] started!");
    }

    @Listener
    public void onStopServer(GameStoppingServerEvent event) {
        logger.info("[SpongeTelegram] disabled!");
    }

    @Listener
    public void reload(GameStartedServerEvent event) {
        reload();
    }

    private void reload() {
        if (instance != null) {
            Sponge.getEventManager().unregisterPluginListeners(Sponge.getPluginManager().fromInstance(Spongetelegram.getInstance()).get());
            try {
                this.config = ConfigHandler.loadConfig();
            } catch (ObjectMappingException | IOException e) {
                logger.error("[SpongeTelegram]Error loading config!!");
            }
        } else {
            reload();
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Path getConfigDir() {
        return defaultConfig;
    }

    public GlobalConfig getConfig() {
        return config;
    }
}
