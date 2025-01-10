package me.myster.tyshPlugin;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
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

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (Objects.equals(event.getEntityType(), EntityType.PLAYER))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExhausted(EntityExhaustionEvent event) {
        event.setCancelled(true);
    }
}
