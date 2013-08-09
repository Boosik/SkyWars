/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.listeners;

import net.daboross.bukkitdev.skywars.internalevents.PrepairGameStartEvent;
import net.daboross.bukkitdev.skywars.internalevents.PrepairPlayerLeaveGameEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author daboross
 */
public class ResetHealthListener implements Listener {

    @EventHandler
    public void onGameStart(PrepairGameStartEvent evt) {
        for (Player p : evt.getPlayers()) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(p.getMaxHealth());
            p.getInventory().clear();
            p.getInventory().setArmorContents(new ItemStack[4]);
            p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerLeave(PrepairPlayerLeaveGameEvent evt) {
        Player p = evt.getPlayer();
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealth(p.getMaxHealth());
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[4]);
        p.setFoodLevel(20);
    }
}
