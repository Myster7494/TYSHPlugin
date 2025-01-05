package me.myster.tyshPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class TyshCompass implements Listener {
    public static NamespacedKey TYSH_COMPASS_KEY;

    public TyshCompass(Plugin plugin) {
        TYSH_COMPASS_KEY = new NamespacedKey(plugin,"tysh_compass");
    }

    public static boolean isTyshCompassItemStack(ItemStack itemStack) {
        return Objects.nonNull(itemStack) &&itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(TYSH_COMPASS_KEY);
    }

    public static ItemStack getTyshCompassItemStack() {
        ItemStack itemStack = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(TYSH_COMPASS_KEY, PersistentDataType.BOOLEAN,true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ItemStack itemStack = TyshCompass.getTyshCompassItemStack();
        event.getPlayer().getInventory().setItem(0,itemStack);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!isTyshCompassItemStack(event.getCurrentItem())) return;
        event.setCancelled(true);
        event.getWhoClicked().openInventory(Bukkit.createInventory(null,InventoryType.HOPPER));
    }
}
