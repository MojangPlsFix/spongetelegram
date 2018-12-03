package io.github.fabrossmann.spongetelegram.spongetelegram.telegram;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.fabrossmann.spongetelegram.spongetelegram.Spongetelegram;
import io.github.fabrossmann.spongetelegram.spongetelegram.config.ConfigHandler;
import io.github.fabrossmann.spongetelegram.spongetelegram.config.GlobalConfig;
import io.github.fabrossmann.spongetelegram.spongetelegram.telegram.components.Chat;
import io.github.fabrossmann.spongetelegram.spongetelegram.telegram.components.MinecraftToTelegram;
import io.github.fabrossmann.spongetelegram.spongetelegram.telegram.components.Update;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Telegram {

    static int lastUpdate = 0;
    private final String API_URL_GETME = "https://api.telegram.org/bot%s/getMe";
    private final String API_URL_GETUPDATES = "https://api.telegram.org/bot%s/getUpdates?offset=%d";
    private final String API_URL_GENERAL = "https://api.telegram.org/bot%s/%s";

    public JsonObject authJson;
    public boolean connected = false;
    public String token;
    GlobalConfig config;

    private List<TelegramListener> listeners = new ArrayList<TelegramListener>();
    private Gson gson = new Gson();


    public Telegram() {
        config = Spongetelegram.getInstance().getConfig();
    }

    public void addListener(TelegramListener actionListener) {
        listeners.add(actionListener);
    }

    public boolean auth(String token) {
        this.token = token;
        return reconnect();
    }

    public boolean reconnect() {
        try {
            JsonObject obj = sendGet(String.format(API_URL_GETME, token));
            authJson = obj;
            System.out.print("[SpongeTelegram] telegram connected!");
            connected = true;
            return true;
        } catch (Exception e) {
            connected = false;
            System.out.print("[SpongeTelegram] Error connecting to telegram services!");
            return false;
        }
    }

    public boolean getUpdate() {
        JsonObject up = null;
        try {
            up = sendGet(String.format(API_URL_GETUPDATES, config.telegramConfig.get(0).key, lastUpdate = lastUpdate + 1));
        } catch (IOException e) {
            return false;
        }
        if (up == null) {
            return false;
        }
        if (up.has("result")) {
            for (JsonElement ob : up.getAsJsonArray("result")) {
                if (ob.isJsonObject()) {
                    Update update = gson.fromJson(ob, Update.class);

                    if(lastUpdate == update.getUpdate_id()) return true;
                    lastUpdate = update.getUpdate_id();

                    if (update.getMessage() != null) {
                        Chat chat = update.getMessage().getChat();

                        if (update.getMessage().getText() != null) {
                            String text = update.getMessage().getText();
                            if (text.length() == 0) {
                                return true;
                            }
                            Spongetelegram.getInstance().getLogger().info(text);
                            if (text.contains("/admin ")) {
                                String mes=text.replace("/admin ","");
                                Spongetelegram.getInstance().getLogger().info("ADMIN:" + text);
                                List<Player> playerList = new ArrayList(Sponge.getServer().getOnlinePlayers());
                                for(Player player :playerList){
                                    player.sendMessage(Text.builder(mes).color(TextColors.YELLOW).build());
                                }
                                return true;
                            }else if(text.contains("/kick")){
                                String mes=text.replace("/kick ","");
                                List<Player> playerList = new ArrayList(Sponge.getServer().getOnlinePlayers());
                                for(Player player :playerList){
                                    if(player.getName().toLowerCase().equals(mes.toLowerCase())){
                                        player.kick(Text.builder("You got kicked over Telegram! Congrats").color(TextColors.YELLOW).build());
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return true;
    }

    public void sendMsg(int id, String msg) {
        MinecraftToTelegram chat = new MinecraftToTelegram();
        chat.setChat_id(id);
        chat.setText(msg);
        sendMsg(chat);
    }

    public void sendMsg(MinecraftToTelegram chat) {
        for (TelegramListener actionListener : listeners) {
            actionListener.onSendToTelegram(chat);
        }
        Gson gson = new Gson();
        post("sendMessage", gson.toJson(chat, MinecraftToTelegram.class));
    }

    public void sendAll(final MinecraftToTelegram chat) {
        new Thread(() -> {
            int i=0;
            for (int id : config.telegramConfig.get(i).chatIds) {
                chat.setChat_id(id);
                sendMsg(chat);
                i++;
            }
        }).start();
    }

    public void post(String method, String json) {
        try {
            String body = json;
            URL url = new URL(String.format(API_URL_GENERAL, config.telegramConfig.get(0).key, method));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json; ; Charset=UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
            writer.write(body);
            writer.close();
            wr.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            writer.close();
            reader.close();
        } catch (Exception e) {
            reconnect();
            System.out.print("[SpongeTelegram] Disconnected from Telegram, reconnect...");
        }

    }

    public JsonObject sendGet(String url) throws IOException {
        String a = url;
        URL url2 = new URL(a);
        URLConnection conn = url2.openConnection();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String all = "";
        String inputLine;

        while ((inputLine = br.readLine()) != null) {
            all += inputLine;
        }

        br.close();
        JsonParser parser = new JsonParser();
        return parser.parse(all).getAsJsonObject();

    }
}
