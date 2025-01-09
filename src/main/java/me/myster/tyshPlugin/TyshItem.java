package me.myster.tyshPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

public class TyshItem implements Listener {
    public static NamespacedKey TYSH_KEY;

    public static class TyshItemStack extends ItemStack {
        private static final TreeMap<String, TyshItemStack> TYSH_ITEM_STACKS = new TreeMap<>();

        public static void initTyshItemStacks() {
            new TyshItemStack("main", "TYSH 伺服器選單", Material.RECOVERY_COMPASS, Actions::openInventory);
            new TyshItemStack("lobby", "前往大廳", Material.EMERALD, (player) -> Actions.bungeeTeleport(player, "lobby"));
            new TyshItemStack("tysh", "前往桃高", Material.BRICKS, (player) -> Actions.bungeeTeleport(player, "tysh"));
            new TyshItemStack("discord", "前往 Discord", Material.ENDER_PEARL, (player) -> {
                String discordLink = "https://discord.gg/9MBYxXhmgA";
                TextComponent discordText1 = new TextComponent();
                discordText1.setColor(ChatColor.GREEN);
                discordText1.setText("點我加入 ");
                discordText1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, discordLink));
                TextComponent discordText2 = new TextComponent();
                discordText2.setColor(ChatColor.AQUA);
                discordText2.setText("Discord 群組");
                discordText1.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, discordLink));
                TextComponent discordText3 = new TextComponent();
                discordText3.setColor(ChatColor.AQUA);
                discordText3.setText(discordLink);
                discordText3.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, discordLink));
                player.spigot().sendMessage(new ComponentBuilder().append(discordText1).append(discordText2).append("\n").append(discordText3).create());
                player.closeInventory();
            });
            {
                TextComponent gameModeChangedText = new TextComponent();
                gameModeChangedText.setText("遊戲模式已變為 ");
                gameModeChangedText.setColor(ChatColor.GREEN);
                TextComponent gameModeText = new TextComponent();
                gameModeText.setColor(ChatColor.AQUA);
                new TyshItemStack("spectator", "旁觀者模式", Material.ENDER_EYE, (player) -> {
                    if (!player.hasPermission("minecraft.command.gamemode")) return;
                    player.setGameMode(GameMode.SPECTATOR);
                    TextComponent spectatorText = gameModeText.duplicate();
                    spectatorText.setText("旁觀者模式");
                    player.spigot().sendMessage(gameModeChangedText, spectatorText);
                    player.closeInventory();
                });
                new TyshItemStack("creative", "創造模式", Material.GRASS_BLOCK, (player) -> {
                    if (!player.hasPermission("minecraft.command.gamemode")) return;
                    player.setGameMode(GameMode.CREATIVE);
                    TextComponent creativeText = gameModeText.duplicate();
                    creativeText.setText("創造模式");
                    player.spigot().sendMessage(gameModeChangedText, creativeText);
                    player.closeInventory();
                });
                new TyshItemStack("adventure", "冒險模式", Material.MAP, (player) -> {
                    if (!player.hasPermission("minecraft.command.gamemode")) return;
                    player.setGameMode(GameMode.ADVENTURE);
                    TextComponent adventureText = gameModeText.duplicate();
                    adventureText.setText("冒險模式");
                    player.spigot().sendMessage(gameModeChangedText, adventureText);
                    player.closeInventory();
                });
            }
        }

        public final String id;
        public final Consumer<Player> action;

        public TyshItemStack(String id, String name, Material type, Consumer<Player> action) {
            super(type);

            this.id = id;
            this.action = action;

            ItemMeta itemMeta = this.getItemMeta();
            itemMeta.getPersistentDataContainer().set(TYSH_KEY, PersistentDataType.STRING, this.id);
            itemMeta.setItemName(name);
            this.setItemMeta(itemMeta);
            TYSH_ITEM_STACKS.put(id, this);
        }

        public static TyshItemStack getItemStack(String id) {
            return TYSH_ITEM_STACKS.get(id);
        }

        public static TyshItemStack getItemStack(ItemStack itemStack) {
            return getItemStack(getTyshKeyValue(itemStack));
        }

        public static void runAction(ItemStack itemStack, Player player) {
            TyshItemStack.getItemStack(itemStack).action.accept(player);
        }
    }

    public TyshItem() {
        TYSH_KEY = new NamespacedKey(TyshPlugin.TYSH_PLUGIN, "tysh");
        TyshItemStack.initTyshItemStacks();
    }

    public static boolean isTyshItemStack(ItemStack itemStack) {
        return Objects.isNull(itemStack) || !itemStack.hasItemMeta() || !itemStack.getItemMeta().getPersistentDataContainer().has(TYSH_KEY);
    }

    public static String getTyshKeyValue(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().get(TYSH_KEY, PersistentDataType.STRING);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ItemStack itemStack = TyshItemStack.getItemStack("main");
        event.getPlayer().getInventory().setItem(0, itemStack);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack currentItemStack = event.getCurrentItem();
        if (isTyshItemStack(currentItemStack)) return;
        event.setCancelled(true);
        TyshItemStack.runAction(currentItemStack, (Player) event.getWhoClicked());
    }

    @EventHandler
    public void onPlayerInteractive(PlayerInteractEvent event) {
        ItemStack currentItemStack = event.getItem();
        if (isTyshItemStack(currentItemStack)) return;
        event.setCancelled(true);
        TyshItemStack.runAction(currentItemStack, event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack currentItemStack = event.getItemDrop().getItemStack();
        if (isTyshItemStack(currentItemStack)) return;
        event.setCancelled(true);
        TyshItemStack.runAction(currentItemStack, event.getPlayer());
    }

    public static class Actions {
        public static void openInventory(Player player) {
            Inventory inventory = Bukkit.createInventory(null, 18, "TYSH 伺服器選單");
            inventory.setItem(0, TyshItemStack.getItemStack("lobby"));
            inventory.setItem(1, TyshItemStack.getItemStack("tysh"));
            inventory.setItem(8, TyshItemStack.getItemStack("discord"));
            inventory.setItem(9, TyshItemStack.getItemStack("spectator"));
            inventory.setItem(10, TyshItemStack.getItemStack("creative"));
            inventory.setItem(11, TyshItemStack.getItemStack("adventure"));
            player.openInventory(inventory);
        }

        public static void bungeeTeleport(Player player, String dest) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(dest);
            player.sendPluginMessage(TyshPlugin.TYSH_PLUGIN, "BungeeCord", out.toByteArray());
        }
    }
}
