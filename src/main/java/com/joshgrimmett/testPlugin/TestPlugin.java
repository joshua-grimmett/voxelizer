package com.joshgrimmett.testPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.joshgrimmett.testPlugin.command.CommandVoxelize;
import com.joshgrimmett.testPlugin.command.WaypointCommand;

public class TestPlugin extends JavaPlugin implements Listener {

    public static TestPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(this, this);

        this.getCommand("waypoint").setExecutor(new WaypointCommand());
        this.getCommand("voxelize").setExecutor(new CommandVoxelize());
        this.getCommand("voxelize").setTabCompleter(new CommandVoxelize());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.FILLED_MAP) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.RED + "Waypoint")) {
                event.setCancelled(true);

                // Open a double chest-sized inventory
                Inventory waypointMenu = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Waypoint Menu");
                player.openInventory(waypointMenu);
            }
        }
    }

}
