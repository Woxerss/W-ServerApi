package com.woxerss.wserverapi.listeners;

import com.woxerss.wserverapi.Main;
import com.woxerss.wserverapi.events.CustomEvent;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Queue;

public class JoinQuitEventListener implements Listener {
    @EventHandler
    public  void onPlayerJoin(PlayerJoinEvent event) {
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            eventsQueue.add(new CustomEvent("player_joined", event.getPlayer().getName()));
        }
    }

    @EventHandler
    public  void onPlayerQuit(PlayerQuitEvent event) {
        Queue<CustomEvent> eventsQueue = Main.getEventsQueue();
        synchronized(eventsQueue) {
            eventsQueue.add(new CustomEvent("player_quit", event.getPlayer().getName()));
        }
    }
}
