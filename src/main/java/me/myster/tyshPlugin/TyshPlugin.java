package me.myster.tyshPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
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
        LOGGER.log(Level.INFO, "TyshPlugin startup.");
        TYSH_PLUGIN = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new TyshItem(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getCommand("tysh").setExecutor(new TyshCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LOGGER.log(Level.INFO, "TyshPlugin shutdown.");
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getCause() != EntityDamageEvent.DamageCause.VOID)
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExhausted(EntityExhaustionEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractive(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        if (Objects.nonNull(itemStack) && itemStack.getType() == Material.MAP)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String discordLink = "https://discord.gg/9MBYxXhmgA";
        TextComponentBuilder discordBuilder = new TextComponentBuilder().setClickEvent(ClickEvent.Action.OPEN_URL, discordLink);
        event.getPlayer().spigot().sendMessage(
                TextComponentBuilder.create("===================================", ChatColor.YELLOW), TextComponents.LINE_FEED,
                TextComponentBuilder.create("歡迎加入 ", ChatColor.GREEN),
                TextComponentBuilder.create("TYSH Minecraft", ChatColor.AQUA), TextComponents.LINE_FEED,
                TextComponentBuilder.create("本伺服器旨在將學長製作的桃高 Minecraft 地圖完成", ChatColor.GREEN), TextComponents.LINE_FEED,
                TextComponentBuilder.create("本計劃與桃園市立桃園高級中等學校無任何關係", ChatColor.YELLOW), TextComponents.LINE_FEED,
                TextComponentBuilder.create("更多詳細資訊可加入 Discord 群組查看", ChatColor.GREEN), TextComponents.LINE_FEED,
                discordBuilder.duplicate().setText("點我加入").setColor(ChatColor.GREEN).build(),
                discordBuilder.duplicate().setText(" Discord 群組").setColor(ChatColor.AQUA).build(), TextComponents.LINE_FEED,
                discordBuilder.duplicate().setText(discordLink).setColor(ChatColor.AQUA).build(), TextComponents.LINE_FEED,
                TextComponentBuilder.create("===================================", ChatColor.YELLOW)
        );
    }
}
