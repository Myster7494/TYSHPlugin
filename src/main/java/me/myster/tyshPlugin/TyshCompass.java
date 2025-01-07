package me.myster.tyshPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class TyshCompass implements Listener {
    public static NamespacedKey TYSH_COMPASS_KEY;

    public TyshCompass() {
        TYSH_COMPASS_KEY = new NamespacedKey(TyshPlugin.TYSH_PLUGIN,"tysh_compass");
    }

    public static boolean isTyshCompassItemStack(ItemStack itemStack) {
        return Objects.nonNull(itemStack) &&itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(TYSH_COMPASS_KEY);
    }

    public static int getTyshCompassKeyValue(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(TYSH_COMPASS_KEY,PersistentDataType.INTEGER);
    }

    public static ItemStack getTyshCompassItemStack() {
        return getItemStackWithTyshCompassKey(Material.COMPASS, 0);
    }

    public static ItemStack getItemStackWithTyshCompassKey(Material item, int value) {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(TYSH_COMPASS_KEY, PersistentDataType.INTEGER,value);
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
        ItemStack currentItem = event.getCurrentItem();
        if (!isTyshCompassItemStack(currentItem)) return;
        event.setCancelled(true);
        useTyshCompass((Player) event.getWhoClicked(), currentItem);
    }

    @EventHandler
    public void onPlayerInteractive(PlayerInteractEvent event) {
        ItemStack currentItem = event.getItem();
        if (!isTyshCompassItemStack(currentItem)) return;
        event.setCancelled(true);
        useTyshCompass(event.getPlayer(),currentItem);
    }

    public static void useTyshCompass(Player player, ItemStack currentItem) {
        switch (getTyshCompassKeyValue(currentItem)) {
            case 0 -> openInventory(player);
            case 1 -> bungeeTeleport(player,"lobby");
            case 2 -> bungeeTeleport(player,"tysh");
        }
    }

    public static void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null,InventoryType.HOPPER,"TYSH");
        inventory.setItem(0,getItemStackWithTyshCompassKey(Material.EMERALD,1));
        inventory.setItem(1,getItemStackWithTyshCompassKey(Material.BRICKS,2));
        player.openInventory(inventory);
    }

    public static void bungeeTeleport(Player player, String dest) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(dest);
        player.sendPluginMessage(TyshPlugin.TYSH_PLUGIN, "BungeeCord", out.toByteArray());
    }
}
