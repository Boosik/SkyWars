/*
 * Copyright (C) 2013 Dabo Ross <www.daboross.net>
 */
package net.daboross.bukkitdev.skywars.commands;

import net.daboross.bukkitdev.commandexecutorbase.ColorList;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.SkyWarsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author daboross
 */
public class CancelCommand extends SubCommand {

    private final SkyWarsPlugin plugin;

    public CancelCommand(SkyWarsPlugin plugin) {
        super("cancel", true, "skywars.cancel", new String[]{"ID"}, "Cancels a current game with the given id");
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender sender, Command baseCommand, String baseCommandLabel, String subCommandLabel, String[] subCommandArgs) {
        if (subCommandArgs.length == 0) {
            sender.sendMessage(ColorList.ERR + "Not enough arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        } else if (subCommandArgs.length > 1) {
            sender.sendMessage(ColorList.ERR + "Too many arguments!");
            sender.sendMessage(getHelpMessage(baseCommandLabel, subCommandLabel));
            return;
        }
    }
}