package me.myster.tyshPlugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class TyshPlugin extends JavaPlugin implements Listener {
    public final Logger LOGGER = getLogger();
    public static TyshPlugin TYSH_PLUGIN;

    @Override
    public void onEnable() {
        // Plugin startup logic
        LOGGER.log(Level.INFO, "TyshPlugin start up.");
        TYSH_PLUGIN = this;
        getServer().getPluginManager().registerEvents(new TyshItem(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LOGGER.log(Level.INFO, "TyshPlugin shutdown.");
    }
}
