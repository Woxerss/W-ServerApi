package com.woxerss.wserverapi.listeners;

import com.woxerss.wserverapi.Main;
import com.woxerss.wserverapi.events.CustomEvent;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.FailedLoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import fr.xephi.authme.events.UnregisterByAdminEvent;

import java.util.Queue;

public class AuthmeListener implements Listener {
    @EventHandler
    public void onLogin(LoginEvent event) {
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            eventsQueue.add(new CustomEvent("player_login", event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onFailedLoginEvent(FailedLoginEvent event) {
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            eventsQueue.add(new CustomEvent("player_failed_login", event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onRegisterEvent(RegisterEvent event) {
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            eventsQueue.add(new CustomEvent("player_registered", event.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onUnregisterByAdminEvent(UnregisterByAdminEvent event) {
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            eventsQueue.add(new CustomEvent("player_unregistered", event.getPlayerName()));
        }
    }
}
