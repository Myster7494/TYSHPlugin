package me.myster.tyshPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

public class TyshItem implements Listener {
    public static NamespacedKey TYSH_KEY;

    public static class TyshItemStack extends ItemStack {
        public enum TyshItemName {
            MAIN("main"), LOBBY("lobby"), TYSH("tysh"),
            DISCORD("discord"),
            SPECTATOR("spectator"), CREATIVE("creative"), ADVENTURE("adventure"),
            NIGHT_VISION("night_vision");

            public final String name;

            TyshItemName(String name) {
                this.name = name;
            }

            public static TyshItemName get(String targetName) {
                for (TyshItemName name : TyshItemName.values())
                    if (Objects.equals(name.name, targetName))
                        return name;
                throw new NoSuchElementException();
            }
        }

        private static final TreeMap<TyshItemName, TyshItemStack> TYSH_ITEM_STACKS = new TreeMap<>();

        public static void initTyshItemStacks() {
            new TyshItemStack(TyshItemName.MAIN, "TYSH 伺服器選單（點擊使用）", Material.RECOVERY_COMPASS, Actions::openInventory);
            new TyshItemStack(TyshItemName.LOBBY, "前往大廳", Material.EMERALD, (player) -> {
                Actions.bungeeTeleport(player, "lobby");
                player.closeInventory();
            });
            new TyshItemStack(TyshItemName.TYSH, "前往桃高", Material.BRICKS, (player) -> {
                Actions.bungeeTeleport(player, "tysh");
                player.closeInventory();
            });
            new TyshItemStack(TyshItemName.DISCORD, "前往 Discord", Material.ENDER_PEARL, (player) -> {
                String discordLink = "https://discord.gg/9MBYxXhmgA";
                TextComponentBuilder builder = new TextComponentBuilder().setClickEvent(ClickEvent.Action.OPEN_URL, discordLink);
                player.spigot().sendMessage(
                        builder.duplicate().setText("點我加入").setColor(ChatColor.GREEN).build(),
                        builder.duplicate().setText(" Discord 群組").setColor(ChatColor.AQUA).build(),
                        TextComponents.LINE_FEED,
                        builder.duplicate().setText(discordLink).setColor(ChatColor.AQUA).build()
                );
                player.closeInventory();
            });
            {
                TextComponent gameModeChangedText = TextComponentBuilder.create("遊戲模式已變為 ", ChatColor.GREEN);
                TextComponentBuilder gameModeTextBuilder = new TextComponentBuilder().setColor(ChatColor.AQUA);
                new TyshItemStack(TyshItemName.SPECTATOR, "旁觀者模式", Material.ENDER_EYE, (player) -> {
                    if (!player.hasPermission("minecraft.command.gamemode")) return;
                    player.setGameMode(GameMode.SPECTATOR);
                    player.spigot().sendMessage(gameModeChangedText, gameModeTextBuilder.duplicate().setText("旁觀者模式").build());
                    player.closeInventory();
                });
                new TyshItemStack(TyshItemName.CREATIVE, "創造模式", Material.GRASS_BLOCK, (player) -> {
                    if (!player.hasPermission("minecraft.command.gamemode")) return;
                    player.setGameMode(GameMode.CREATIVE);
                    player.spigot().sendMessage(gameModeChangedText, gameModeTextBuilder.duplicate().setText("創造模式").build());
                    player.closeInventory();
                });
                new TyshItemStack(TyshItemName.ADVENTURE, "冒險模式", Material.MAP, (player) -> {
                    if (!player.hasPermission("minecraft.command.gamemode")) return;
                    player.setGameMode(GameMode.ADVENTURE);
                    player.spigot().sendMessage(gameModeChangedText, gameModeTextBuilder.duplicate().setText("冒險模式").build());
                    player.closeInventory();
                });
            }
            new TyshItemStack(TyshItemName.NIGHT_VISION, "夜視", Material.SPYGLASS, (player) -> {
                if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 255, false, false, true));
                    player.spigot().sendMessage(TextComponentBuilder.create("已開啟夜視", ChatColor.GREEN));
                } else {
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player.spigot().sendMessage(TextComponentBuilder.create("已關閉夜視", ChatColor.GREEN));
                }
                player.closeInventory();
            });
        }

        public final String displayName;
        public final TyshItemName name;
        public final Consumer<Player> action;

        public TyshItemStack(TyshItemName name, String displayName, Material type, Consumer<Player> action) {
            super(type);

            this.name = name;
            this.displayName = displayName;
            this.action = action;

            ItemMeta itemMeta = this.getItemMeta();
            itemMeta.getPersistentDataContainer().set(TYSH_KEY, PersistentDataType.STRING, this.name.name);
            itemMeta.setItemName(displayName);
            this.setItemMeta(itemMeta);
            TYSH_ITEM_STACKS.put(name, this);
        }

        public static TyshItemStack getItemStack(TyshItemName name) {
            return TYSH_ITEM_STACKS.get(name);
        }

        public static TyshItemStack getItemStack(String name) {
            return getItemStack(TyshItemName.get(name));
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
            inventory.setItem(0, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.LOBBY));
            inventory.setItem(1, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.TYSH));
            inventory.setItem(8, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.DISCORD));
            if (player.hasPermission("minecraft.command.gamemode")) {
                inventory.setItem(9, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.SPECTATOR));
                inventory.setItem(10, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.CREATIVE));
                inventory.setItem(11, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.ADVENTURE));
            }
            inventory.setItem(17, TyshItemStack.getItemStack(TyshItemStack.TyshItemName.NIGHT_VISION));
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
