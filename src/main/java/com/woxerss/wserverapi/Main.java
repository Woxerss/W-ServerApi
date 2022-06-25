package com.woxerss.wserverapi;

import com.woxerss.wserverapi.events.CustomEvent;
import com.woxerss.wserverapi.hooks.AuthmeHook;
import com.woxerss.wserverapi.httpserver.*;
import com.woxerss.wserverapi.listeners.AuthmeListener;
import com.woxerss.wserverapi.listeners.JoinQuitEventListener;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    // Objects
    static AuthmeHook authmeHook = new AuthmeHook();
    FileConfiguration config = getConfig();
    HttpServer server;

    // Config
    public static String token = "";

    // Events Queue
    static Queue<CustomEvent> eventsQueue = new LinkedList<CustomEvent>();

    @Override
    public void onEnable() {
        // Регистрируем listners
        getServer().getPluginManager().registerEvents(new JoinQuitEventListener(), this);

        // Интеграция с Authme
        if (getServer().getPluginManager().isPluginEnabled("AuthMe")) {
            getServer().getPluginManager().registerEvents(new AuthmeListener(), this);
            authmeHook.initializeHook();
            getLogger().info("Authme integration enabled!");
        } else {
            getLogger().info("Authme not not installed!");
        }

        // Инициализация конфига
        config.options().copyDefaults(true);
        saveConfig();
        token = config.getString("token");

        // Запускаем Http сервер
        try {
            server = new HttpServer();
            server.startServer(config.getInt("port"));
            getLogger().info("HTTP Server has been successfully started!");
        } catch (Exception e) {
            getLogger().info("Cannot start HTTP server, disabling");
            getServer().getPluginManager().disablePlugin(this);
            getLogger().info(e.toString());
            return;
        }
    }

    @Override
    public void onDisable() {
        // Останавливаем сервер
        try {
            server.stop();
        } catch (Exception e) {
            getLogger().info("Cannot stop HTTP server");
            getLogger().info(e.toString());
            return;
        }
        
        getLogger().info("HTTP Server has been successfully stopped!");
    }

    // GET
    public FileConfiguration getConfigFile() {
        return config;
    }

    public static Queue<CustomEvent> getEventsQueue() {
        return eventsQueue;
    }

    public static AuthmeHook getAuthmeHook() {
        return authmeHook;
    }
}
