/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.attribute.AttributeModifier
 *  org.bukkit.block.ShulkerBox
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityPickupItemEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCreativeEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerGameModeChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerSwapHandItemsEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.BlockStateMeta
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffect
 *  org.jetbrains.annotations.NotNull
 */
package me.maksde.antisavedhotbar;

import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public final class AntiSavedHotbarAbuse
extends JavaPlugin
implements Listener {
    private String bypassPermission;
    private boolean scanOnJoin;
    private boolean scanOnGamemodeChange;
    private boolean scanEnderChest;
    private boolean onlyScanCreative;
    private boolean blockCreativeIllegalItems;
    private boolean blockInventoryMoves;
    private boolean blockDropsAndPickups;
    private int periodicScanIntervalTicks;
    private int scanTaskId = -1;
    private int maxEnchantLevel;
    private boolean blockAboveVanillaMax;
    private boolean checkPotions;
    private int maxPotionAmplifier;
    private int maxPotionDurationSeconds;
    private boolean blockInfinitePotionEffects;
    private boolean checkAttributes;
    private double maxAttributeAmount;
    private double maxAttributePercent;
    private boolean checkShulkerContents;
    private boolean logRemovals;
    private boolean sendMessage;
    private String message;
    private Set<Material> blockedMaterials;
    private boolean blockCommandBlockPlace;
    private boolean blockCommandBlockInteract;
    private boolean blockTotemStacking;
    private String commandBlockMessage;
    private String totemMessage;

    public void onEnable() {
        this.saveDefaultConfig();
        this.loadSettings();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.startPeriodicScanner();
        this.getLogger().info("AntiSavedHotbarAbuse enabled");
    }

    public void onDisable() {
        if (this.scanTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.scanTaskId);
            this.scanTaskId = -1;
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("antisavedhotbar")) {
            return false;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("antisavedhotbar.reload")) {
                sender.sendMessage(String.valueOf(ChatColor.RED) + "\u041d\u0435\u043c\u0430\u0454 \u043f\u0440\u0430\u0432: antisavedhotbar.reload");
                return true;
            }
            this.reloadConfig();
            this.loadSettings();
            this.startPeriodicScanner();
            sender.sendMessage(String.valueOf(ChatColor.GREEN) + "AntiSavedHotbarAbuse config reloaded.");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("scan") && sender instanceof Player) {
            Player player = (Player)sender;
            if (!sender.hasPermission("antisavedhotbar.reload")) {
                sender.sendMessage(String.valueOf(ChatColor.RED) + "\u041d\u0435\u043c\u0430\u0454 \u043f\u0440\u0430\u0432: antisavedhotbar.reload");
                return true;
            }
            this.scanPlayer(player);
            sender.sendMessage(String.valueOf(ChatColor.GREEN) + "Inventory scanned.");
            return true;
        }
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "\u041a\u043e\u043c\u0430\u043d\u0434\u0438: /" + label + " reload, /" + label + " scan");
        return true;
    }

    private void loadSettings() {
        FileConfiguration c = this.getConfig();
        this.bypassPermission = c.getString("bypass-permission", "antisavedhotbar.bypass");
        this.scanOnJoin = c.getBoolean("scan-on-join", true);
        this.scanOnGamemodeChange = c.getBoolean("scan-on-gamemode-change", true);
        this.scanEnderChest = c.getBoolean("scan-ender-chest", true);
        this.onlyScanCreative = c.getBoolean("only-scan-creative", false);
        this.blockCreativeIllegalItems = c.getBoolean("block-creative-illegal-items", true);
        this.blockInventoryMoves = c.getBoolean("block-inventory-moves", true);
        this.blockDropsAndPickups = c.getBoolean("block-drops-and-pickups", true);
        this.periodicScanIntervalTicks = c.getInt("periodic-scan-interval-ticks", 100);
        this.maxEnchantLevel = c.getInt("max-enchant-level", 50);
        this.blockAboveVanillaMax = c.getBoolean("block-above-vanilla-max", false);
        this.checkPotions = c.getBoolean("check-potions", true);
        this.maxPotionAmplifier = c.getInt("max-potion-amplifier", 50);
        this.maxPotionDurationSeconds = c.getInt("max-potion-duration-seconds", 9600);
        this.blockInfinitePotionEffects = c.getBoolean("block-infinite-potion-effects", true);
        this.checkAttributes = c.getBoolean("check-attributes", true);
        this.maxAttributeAmount = c.getDouble("max-attribute-amount", 50.0);
        this.maxAttributePercent = c.getDouble("max-attribute-percent", 100.0);
        this.checkShulkerContents = c.getBoolean("check-shulker-contents", true);
        this.logRemovals = c.getBoolean("log-removals", true);
        this.sendMessage = c.getBoolean("send-message", true);
        this.message = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("message", "&cIllegal item blocked."));
        this.blockCommandBlockPlace = c.getBoolean("block-command-block-place", true);
        this.blockCommandBlockInteract = c.getBoolean("block-command-block-interact", true);
        this.blockTotemStacking = c.getBoolean("block-totem-stacking", true);
        this.commandBlockMessage = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("command-block-message", "&c\u041a\u043e\u043c\u0430\u043d\u0434\u043d\u0456 \u0431\u043b\u043e\u043a\u0438 \u0437\u0430\u0431\u043e\u0440\u043e\u043d\u0435\u043d\u043e \u0432\u0438\u043a\u043e\u0440\u0438\u0441\u0442\u043e\u0432\u0443\u0432\u0430\u0442\u0438."));
        this.totemMessage = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("totem-message", "&c\u0422\u043e\u0442\u0435\u043c\u0438 \u043d\u0435 \u043c\u043e\u0436\u043d\u0430 \u0441\u0442\u0430\u043a\u0430\u0442\u0438. \u0423 \u0441\u0442\u0430\u043ay \u0437\u0430\u043b\u0438\u0448\u0435\u043d\u043e 1 \u0442\u043e\u0442\u0435\u043c."));
        this.blockedMaterials = new HashSet<Material>();
        for (String name : c.getStringList("blocked-materials")) {
            Material mat = Material.matchMaterial((String)name);
            if (mat != null) {
                this.blockedMaterials.add(mat);
                continue;
            }
            this.getLogger().warning("Unknown material in config: " + name);
        }
    }

    private void startPeriodicScanner() {
        if (this.scanTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.scanTaskId);
            this.scanTaskId = -1;
        }
        if (this.periodicScanIntervalTicks <= 0) {
            return;
        }
        this.scanTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.scanPlayer(player);
            }
        }, (long)this.periodicScanIntervalTicks, (long)this.periodicScanIntervalTicks);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onCreative(InventoryCreativeEvent event) {
        if (!this.blockCreativeIllegalItems) {
            return;
        }
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (this.isBypassed(player)) {
            return;
        }
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        if (this.fixTotemStack(cursor) || this.fixTotemStack(current)) {
            event.setCursor(cursor);
            event.setCurrentItem(current);
            if (this.sendMessage) {
                player.sendMessage(this.totemMessage);
            }
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
        }
        if (this.isIllegal(cursor) || this.isIllegal(current)) {
            event.setCancelled(true);
            event.setCursor(null);
            event.setCurrentItem(null);
            this.warn(player, cursor != null && cursor.getType() != Material.AIR ? cursor : current);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!this.blockInventoryMoves) {
            return;
        }
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (this.isBypassed(player)) {
            return;
        }
        if (this.fixTotemStack(event.getCurrentItem()) || this.fixTotemStack(event.getCursor())) {
            event.setCurrentItem(event.getCurrentItem());
            event.setCursor(event.getCursor());
            if (this.sendMessage) {
                player.sendMessage(this.totemMessage);
            }
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
        }
        if (this.isIllegal(event.getCurrentItem()) || this.isIllegal(event.getCursor())) {
            event.setCancelled(true);
            event.setCurrentItem(null);
            event.setCursor(null);
            this.warn(player, null);
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
            return;
        }
        Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!this.blockInventoryMoves) {
            return;
        }
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (this.isBypassed(player)) {
            return;
        }
        if (this.isIllegal(event.getOldCursor())) {
            event.setCancelled(true);
            event.setCursor(null);
            this.warn(player, event.getOldCursor());
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
            return;
        }
        for (ItemStack item : event.getNewItems().values()) {
            if (!this.isIllegal(item)) continue;
            event.setCancelled(true);
            event.setCursor(null);
            this.warn(player, item);
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
            return;
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDrop(PlayerDropItemEvent event) {
        if (!this.blockDropsAndPickups) {
            return;
        }
        Player player = event.getPlayer();
        if (this.isBypassed(player)) {
            return;
        }
        ItemStack item = event.getItemDrop().getItemStack();
        if (this.isIllegal(item)) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            this.scanPlayer(player);
            this.warn(player, item);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!this.blockDropsAndPickups) {
            return;
        }
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        if (this.isBypassed(player)) {
            return;
        }
        ItemStack item = event.getItem().getItemStack();
        if (this.fixTotemStack(item)) {
            event.getItem().setItemStack(item);
        }
        if (this.isIllegal(item)) {
            event.setCancelled(true);
            event.getItem().remove();
            this.warn(player, item);
            Bukkit.getScheduler().runTask((Plugin)this, () -> this.scanPlayer(player));
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (!this.blockInventoryMoves) {
            return;
        }
        Player player = event.getPlayer();
        if (this.isBypassed(player)) {
            return;
        }
        if (this.isIllegal(event.getMainHandItem()) || this.isIllegal(event.getOffHandItem())) {
            event.setCancelled(true);
            this.scanPlayer(player);
            this.warn(player, null);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (this.isBypassed(player)) {
            return;
        }
        if (this.blockCommandBlockInteract && event.getClickedBlock() != null && this.isCommandBlock(event.getClickedBlock().getType())) {
            event.setCancelled(true);
            player.sendMessage(this.commandBlockMessage);
            if (this.logRemovals) {
                this.getLogger().warning("Blocked command block interaction from " + player.getName());
            }
            return;
        }
        ItemStack item = event.getItem();
        if (this.isIllegal(item)) {
            event.setCancelled(true);
            this.scanPlayer(player);
            this.warn(player, item);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.isBypassed(player)) {
            return;
        }
        ItemStack item = event.getItemInHand();
        if (this.blockCommandBlockPlace && this.isCommandBlock(item.getType())) {
            event.setCancelled(true);
            this.scanPlayer(player);
            player.sendMessage(this.commandBlockMessage);
            if (this.logRemovals) {
                this.getLogger().warning("Blocked command block placement from " + player.getName());
            }
            return;
        }
        if (this.isIllegal(item)) {
            event.setCancelled(true);
            this.scanPlayer(player);
            this.warn(player, item);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (!this.scanOnJoin) {
            return;
        }
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.scanPlayer(player), 20L);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onGamemode(PlayerGameModeChangeEvent event) {
        if (!this.scanOnGamemodeChange) {
            return;
        }
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> this.scanPlayer(player), 2L);
    }

    private void scanPlayer(Player player) {
        if (!player.isOnline() || this.isBypassed(player)) {
            return;
        }
        if (this.onlyScanCreative && player.getGameMode() != GameMode.CREATIVE) {
            return;
        }
        int removed = this.cleanInventory((Inventory)player.getInventory());
        if (this.scanEnderChest) {
            removed += this.cleanInventory(player.getEnderChest());
        }
        if (removed > 0 && this.sendMessage) {
            player.sendMessage(this.message);
        }
        if (removed > 0 && this.logRemovals) {
            this.getLogger().warning("Removed " + removed + " illegal item stack(s) from " + player.getName());
        }
    }

    private int cleanInventory(Inventory inventory) {
        int changed = 0;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; ++i) {
            ItemStack item = contents[i];
            if (this.isIllegal(item)) {
                inventory.setItem(i, null);
                ++changed;
                continue;
            }
            if (!this.fixTotemStack(item)) continue;
            inventory.setItem(i, item);
            ++changed;
        }
        return changed;
    }

    private boolean isBypassed(Player player) {
        return this.bypassPermission != null && !this.bypassPermission.isBlank() && player.hasPermission(this.bypassPermission);
    }

    private void warn(Player player, ItemStack item) {
        if (this.sendMessage) {
            player.sendMessage(this.message);
        }
        if (this.logRemovals) {
            String itemName = item == null ? "unknown" : item.getType().name();
            this.getLogger().warning("Blocked illegal creative item from " + player.getName() + ": " + itemName);
        }
    }

    private boolean fixTotemStack(ItemStack item) {
        if (!this.blockTotemStacking) {
            return false;
        }
        if (item == null || item.getType() != Material.TOTEM_OF_UNDYING) {
            return false;
        }
        if (item.getAmount() <= 1) {
            return false;
        }
        item.setAmount(1);
        return true;
    }

    private boolean isCommandBlock(Material material) {
        return material == Material.COMMAND_BLOCK || material == Material.CHAIN_COMMAND_BLOCK || material == Material.REPEATING_COMMAND_BLOCK;
    }

    private boolean isIllegal(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        if (this.blockedMaterials.contains(item.getType())) {
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        if (this.hasBadEnchantments(meta.getEnchants())) {
            return true;
        }
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) meta;
            if (this.hasBadEnchantments(bookMeta.getStoredEnchants())) {
                return true;
            }
        }
        if (this.checkPotions && meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            if (this.hasBadPotionEffects(potionMeta)) {
                return true;
            }
        }
        if (this.checkAttributes && this.hasBadAttributes(meta)) {
            return true;
        }
        if (this.checkShulkerContents && meta instanceof BlockStateMeta) {
            BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
            if (blockStateMeta.getBlockState() instanceof ShulkerBox) {
                ShulkerBox box = (ShulkerBox) blockStateMeta.getBlockState();
                for (ItemStack inside : box.getInventory().getContents()) {
                    if (this.isIllegal(inside)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasBadEnchantments(Map<Enchantment, Integer> enchants) {
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            int level = entry.getValue();
            if (level > this.maxEnchantLevel) {
                return true;
            }
            if (!this.blockAboveVanillaMax || level <= entry.getKey().getMaxLevel()) continue;
            return true;
        }
        return false;
    }

    private boolean hasBadPotionEffects(PotionMeta potionMeta) {
        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            int displayedLevel = effect.getAmplifier() + 1;
            if (displayedLevel > this.maxPotionAmplifier) {
                return true;
            }
            if (this.blockInfinitePotionEffects && effect.isInfinite()) {
                return true;
            }
            if (effect.isInfinite() || effect.getDuration() <= this.maxPotionDurationSeconds * 20) continue;
            return true;
        }
        return false;
    }

    private boolean hasBadAttributes(ItemMeta meta) {
        Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
        if (modifiers == null) {
            return false;
        }
        for (AttributeModifier modifier : modifiers.values()) {
            if (this.isBadAttributeModifier(modifier)) {
                return true;
            }
        }
        return false;
    }

    // Operation-aware check. Flat ADD_NUMBER modifiers are compared against
    // maxAttributeAmount, but ADD_SCALAR / MULTIPLY_SCALAR_1 modifiers are
    // percentage-based (e.g. amount = 10.0 means "+1000%"), so a small raw
    // number there can still mean a massive real-world boost (speed, etc).
    // Those are compared against maxAttributePercent instead.
    private boolean isBadAttributeModifier(AttributeModifier modifier) {
        double amount = Math.abs(modifier.getAmount());
        AttributeModifier.Operation operation = modifier.getOperation();
        if (operation == AttributeModifier.Operation.ADD_SCALAR || operation == AttributeModifier.Operation.MULTIPLY_SCALAR_1) {
            double percent = amount * 100.0;
            return percent > this.maxAttributePercent;
        }
        return amount > this.maxAttributeAmount;
    }
}
