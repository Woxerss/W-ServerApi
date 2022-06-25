package com.woxerss.wserverapi.httpserver;

import com.woxerss.wserverapi.Main;
import com.woxerss.wserverapi.events.CustomEvent;
import com.woxerss.wserverapi.hooks.AuthmeHook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

// JLHTTP-2.6
import net.freeutils.httpserver.HTTPServer;

// gson-2.9.0
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HttpHandler {
    public boolean verify_token(HTTPServer.Request req)
            throws IOException {
        if (req.getParams().get("token") == null) {
            return false;
        }

        if (req.getParams().get("token").equals(Main.token)) {
            return true;
        }

        return false;
    }

    @HTTPServer.Context("/online")
    public int onlineMethodHandler(HTTPServer.Request req, HTTPServer.Response resp)
            throws IOException {
        // Проверка токена
        if (!verify_token(req)) {
            sendAccessDenied(resp);
            return 0;
        }

        // Создаем и заполняем вектор с имена игроков
        List<String> playersArr = new ArrayList<String>();
        for (Player ps : Bukkit.getOnlinePlayers()) {
            playersArr.add(ps.getName());
        }

        // Создаем элементы JSON
        JsonObject online = new JsonObject();
        JsonArray playersArray = new JsonArray();

        // Заполняем JSON массив игроков
        for (String player : playersArr) {
            JsonObject playerItem = new JsonObject();
            playerItem.addProperty("nickname", player);
            playersArray.add(playerItem);
        }

        // Добавляем в итоговый JSON
        online.addProperty("total", playersArr.size());
        online.add("players_list", playersArray);

        // Отправляем ответ
        resp.getHeaders().add("Content-Type", "application/json");
        resp.send(200, online.toString());

        return 0;
    }

    @HTTPServer.Context("/events")
    public int eventsMethodHandler(HTTPServer.Request req, HTTPServer.Response resp) 
            throws IOException {
        // Проверка токена
        if (!verify_token(req)) {
            sendAccessDenied(resp);
            return 0;
        }
        
        // Соездаем элементы JSON
        JsonObject eventsJson = new JsonObject();
        JsonArray evenstArray = new JsonArray();

        // Заполняем JSON ивентами
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            while (!eventsQueue.isEmpty()) {
                evenstArray.add(eventsQueue.poll().toJson());
            }
            eventsJson.add("events", evenstArray);
        }

        // Отправляем ответ
        resp.getHeaders().add("Content-Type", "application/json");
        resp.send(200, eventsJson.toString());
        return 0;
    }

    @HTTPServer.Context("/register")
    public int registerMethodHandler(HTTPServer.Request req, HTTPServer.Response resp) 
            throws IOException {
        // Проверка токена
        if (!verify_token(req)) {
            sendAccessDenied(resp);
            return 0;
        }

        AuthmeHook authmeHook = Main.getAuthmeHook();

        if (req.getParams().get("nickname") == null & req.getParams().get("password") == null) {
            sendError(resp, "Invalid nickname or password");
            return -1;
        }

        String nickname = req.getParams().get("nickname");
        String password = req.getParams().get("password");

        try {
            if (!authmeHook.registerPlayer(nickname, password)) {
                sendError(resp, "Invalid nickname or password");
                return -1;
            }
        } catch (Exception e) {
            sendError(resp, e.toString());
            return -1;
        }

        sendOK(resp);

        Bukkit.getLogger().info("Player " + nickname + " was registered by HTTP server!");

        return 0;
    }

    @HTTPServer.Context("/unregister")
    public int unregisterMethodHandler(HTTPServer.Request req, HTTPServer.Response resp) 
            throws IOException {
        // Проверка токена
        if (!verify_token(req)) {
            sendAccessDenied(resp);
            return 0;
        }

        AuthmeHook authmeHook = Main.getAuthmeHook();

        if (req.getParams().get("nickname") == null) {
            sendError(resp, "Invalid nickname or password");
            return -1;
        }

        String nickname = req.getParams().get("nickname");

        try {
            authmeHook.unregisterPlayer(nickname);
        } catch (Exception e) {
            sendError(resp, e.toString());
            return -1;
        }

        sendOK(resp);

        Bukkit.getLogger().info("Player " + nickname + " was unregistered by HTTP server!");
        
        return 0;
    }

    @HTTPServer.Context("/restorepassword")
    public int restorepasswordMethodHandler(HTTPServer.Request req, HTTPServer.Response resp) 
            throws IOException {
        // Проверка токена
        if (!verify_token(req)) {
            sendAccessDenied(resp);
            return 0;
        }

        AuthmeHook authmeHook = Main.getAuthmeHook();

        if (req.getParams().get("nickname") == null & req.getParams().get("password") == null) {
            sendError(resp, "Invalid nickname or password");
            return -1;
        }

        String nickname = req.getParams().get("nickname");
        String password = req.getParams().get("password");

        try {
            authmeHook.restorePassword(nickname, password);
        } catch (Exception e) {
            sendError(resp, e.toString());
            return -1;
        }

        sendOK(resp);

        Bukkit.getLogger().info("Player " + nickname + " restored password by HTTP server!");
        
        return 0;
    }

    @HTTPServer.Context("/forcelogin")
    public int forceloginMethodHandler(HTTPServer.Request req, HTTPServer.Response resp) 
            throws IOException {
        // Проверка токена
        if (!verify_token(req)) {
            sendAccessDenied(resp);
            return 0;
        }

        AuthmeHook authmeHook = Main.getAuthmeHook();

        if (req.getParams().get("nickname") == null) {
            sendError(resp, "Invalid nickname");
            return -1;
        }

        String nickname = req.getParams().get("nickname");

        try {
            authmeHook.forceLogin(nickname);
        } catch (Exception e) {
            sendError(resp, e.toString());
            return -1;
        }

        sendOK(resp);

        Bukkit.getLogger().info("Player " + nickname + " was forcelogined by HTTP server!");
        
        return 0;
    }

    public void sendAccessDenied(HTTPServer.Response resp) 
            throws IOException {
        JsonObject replyJson = new JsonObject();
        replyJson.addProperty("error", "Access denied");

        resp.getHeaders().add("Content-Type", "application/json");
        resp.send(401, replyJson.toString());
    }

    public void sendOK(HTTPServer.Response resp) 
            throws IOException { 
        JsonObject replyJson = new JsonObject();
        replyJson.addProperty("message", "done");
        
        resp.getHeaders().add("Content-Type", "application/json");
        resp.send(202, replyJson.toString());
    }

    public void sendError(HTTPServer.Response resp, String error) 
            throws IOException {
        JsonObject replyJson = new JsonObject();
        replyJson.addProperty("error", error);
        
        resp.getHeaders().add("Content-Type", "application/json");
        resp.send(501, replyJson.toString());
    }
}