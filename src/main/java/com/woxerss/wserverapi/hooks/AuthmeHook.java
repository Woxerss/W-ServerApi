package com.woxerss.wserverapi.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.xephi.authme.api.v3.AuthMeApi;

public class AuthmeHook {
    private AuthMeApi authMeApi;

    public void initializeHook() {
        authMeApi = AuthMeApi.getInstance();
    }
  
    public boolean registerPlayer(String name, String password) 
            throws Exception {
        if (authMeApi == null) {
            throw new Exception("Exception while register new player, AuthMe API is not avaible!");
        }

        if (authMeApi.isRegistered(name)) {
            throw new Exception("Exception while register new player, nickname is already in use! " + name);
        }

        return authMeApi.registerPlayer(name, password);
    }

    public void unregisterPlayer(String name) 
            throws Exception {
        if (authMeApi == null) {
            throw new Exception("Exception while unregister player, AuthMe API is not avaible!");
        }

        authMeApi.forceUnregister(name);
    }

    public void restorePassword(String name, String password)
            throws Exception {
        if (authMeApi == null) {
            throw new Exception("Exception while restorePassword, AuthMe API is not avaible!");
        }

        authMeApi.changePassword(name, password);
    }

    public void forceLogin(String name) 
            throws Exception {
        if (authMeApi == null) {
            throw new Exception("Exception while forceLogin player, AuthMe API is not avaible!");
        }

        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            throw new Exception("Exception while forceLogin player, there is no player with such name! " + name);
        }

        authMeApi.forceLogin(player);
    }
}