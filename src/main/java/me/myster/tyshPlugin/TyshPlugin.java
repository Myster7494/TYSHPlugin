package me.myster.tyshPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class TyshPlugin extends JavaPlugin implements Listener {
    public final Logger LOGGER = getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        LOGGER.log(Level.INFO,"TyshPlugin start up.");
        getServer().getPluginManager().registerEvents(new TyshCompass(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LOGGER.log(Level.INFO,"TyshPlugin shutdown.");
    }
}
