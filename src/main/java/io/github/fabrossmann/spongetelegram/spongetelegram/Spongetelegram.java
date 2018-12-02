package io.github.fabrossmann.spongetelegram.spongetelegram;

import com.google.inject.Inject;
import io.github.fabrossmann.spongetelegram.spongetelegram.config.ConfigHandler;
import io.github.fabrossmann.spongetelegram.spongetelegram.config.GlobalConfig;
import jdk.nashorn.internal.objects.Global;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(id = "spongetelegram", name = "Spongetelegram", description = "Telegram Integration for SprongeAPI")
public class Spongetelegram {
    private static Spongetelegram instance;

    GlobalConfig config;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path defaultConfig;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        instance = this;
        reload();
        logger.info("[SpongeTelegram] started!");
    }

    @Listener
    public void onStopServer(GameStoppingServerEvent e) {
        logger.info("[SpongeTelegram] disabled!");
    }

    @Listener
    public void reload(GameStartedServerEvent event) {
        reload();
    }

    private void reload() {
        Sponge.getEventManager().unregisterPluginListeners(Sponge.getPluginManager().fromInstance(instance).get());
        try {
            this.config= ConfigHandler.loadConfig();
        } catch (ObjectMappingException | IOException e) {
            logger.error("[SpongeTelegram]Error loading config!!");
        }
    }

    public static Spongetelegram getInstance() {
        return  instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Path getConfigDir() {
        return defaultConfig;
    }

    public GlobalConfig getConfig(){
        return config;
    }
}
