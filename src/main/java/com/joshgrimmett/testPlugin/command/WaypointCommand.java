/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.joshgrimmett.testPlugin.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author jooshhg
 */
public class WaypointCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("give")) {
            return false;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return false;
        }

        Player player = (Player) sender;

        // Create the custom map
        ItemStack waypointMap = new ItemStack(Material.FILLED_MAP);
        ItemMeta meta = waypointMap.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Waypoint");
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.setUnbreakable(true);
            waypointMap.setItemMeta(meta);
        }

        // Give the player the map
        player.getInventory().addItem(waypointMap);
        player.sendMessage(ChatColor.GREEN + "You have been given a Waypoint map!");
        return true;
    }
    
}
