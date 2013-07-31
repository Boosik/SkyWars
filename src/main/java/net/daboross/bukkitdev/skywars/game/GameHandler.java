/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.game;

import net.daboross.bukkitdev.skywars.Messages;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author daboross
 */
public class GameHandler {

    private final SkyWarsPlugin plugin;

    public GameHandler(SkyWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startNewGame() {
        String[] queued = plugin.getGameQueue().getQueueCopy();
        if (queued.length != 4) {
            throw new IllegalStateException("Queue size is not 4");
        }
        CurrentGames cg = plugin.getCurrentGames();
        int gameID = plugin.getIdHandler().addNewGame(queued);
        Location[] spawnLocations = plugin.getWorldHandler().createArena(gameID);
        for (int i = 0; i < 4; i++) {
            String name = queued[i];
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                throw new IllegalArgumentException("One or more of the players is not online");
            }
            cg.setGameID(name, gameID);
            player.teleport(spawnLocations[i]);
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    public void endGame(int id) {
        CurrentGames cg = plugin.getCurrentGames();
        GameIdHandler idh = plugin.getIdHandler();
        String[] players = idh.getPlayers(id);
        idh.gameFinished(id);
        Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
        String winner = null;
        for (String playerName : players) {
            if (playerName != null) {
                Player player = Bukkit.getPlayerExact(playerName);
                if (player == null) {
                    throw new IllegalStateException("Player in game that isn't online");
                }
                player.teleport(lobby);
                cg.removePlayer(playerName);
                if (winner == null) {
                    winner = playerName;
                } else {
                    winner += ", " + playerName;
                }
            }
        }
        final String message;
        if (winner == null) {
            message = Messages.NONE_WON;
        } else if (winner.contains(", ")) {
            message = String.format(Messages.MULTI_WON, winner);
        } else {
            message = String.format(Messages.SINGLE_WON, winner);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(message);
            }
        }.runTask(plugin);
    }

    public void removePlayerFromGame(String playerName, boolean teleportAndBroadcast) {
        playerName = playerName.toLowerCase();
        CurrentGames cg = plugin.getCurrentGames();
        GameIdHandler idh = plugin.getIdHandler();
        Integer id = cg.getGameID(playerName);
        if (id == null) {
            throw new IllegalStateException("Player not in game");
        }
        cg.removePlayer(playerName);
        String[] players = idh.getPlayers(id);
        int playersLeft = 0;
        for (int i = 0; i < 4; i++) {
            if (players[i] != null) {
                if (players[i].equalsIgnoreCase(playerName)) {
                    players[i] = null;
                } else {
                    playersLeft++;
                }
            }
        }
        if (teleportAndBroadcast) {
            Location lobby = plugin.getLocationStore().getLobbyPosition().toLocation();
            Player player = Bukkit.getPlayerExact(playerName);
            player.teleport(lobby);
            EntityDamageEvent ede = player.getLastDamageCause();
            Entity damager = null;
            if (ede instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) ede).getDamager();
            }
            if (damager == null) {
                Bukkit.broadcastMessage(String.format(Messages.FORFEITED, player.getName()));
            } else {
                String damagerName = (damager instanceof LivingEntity) ? ((LivingEntity) damager).getCustomName() : damager.getType().getName();
                Bukkit.broadcastMessage(String.format(Messages.FORFEITED_BY, damagerName, player.getName()));
            }
        }
        if (playersLeft < 2) {
            endGame(id);
        }
    }
}
