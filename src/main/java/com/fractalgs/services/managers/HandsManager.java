package com.fractalgs.services.managers;

import com.fractalgs.data.SmeltProgress;
import com.fractalgs.utils.api.WorldHelper;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.BenchRequirement;
import com.hypixel.hytale.protocol.ItemResourceType;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HandsManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String HANDS_ID = "Armor_Copper_Hands";

    private static final Integer SMELT_TIME_TICKS = 140;

    private record ThermalRecipe(String outputId, Integer inputQty, Integer outputQty) {}

    private final Map<String, ThermalRecipe> recipeIds = new HashMap<>();
    private final Map<Integer, ThermalRecipe> recipeTags = new HashMap<>();
    private final Map<String, Integer> recipeResourceNames = new HashMap<>();

    private final Map<UUID, SmeltProgress> playerProgress = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> activeSmeltLoops = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> activeRepairLoops = new ConcurrentHashMap<>();

    private final Map<UUID, Integer> repairAccumulator = new ConcurrentHashMap<>();

    private boolean recipesScanned = false;

    private static final String[] REPAIR_BLACKLIST = {
            "Armor_Copper_Helmet",
            "Armor_Copper_Chest",
            "Armor_Copper_Legs",
            "Armor_Copper_Hands"
    };

    public void register(JavaPlugin plugin) {

        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {

            Holder<EntityStore> holder = event.getHolder();

            Player player = holder.getComponent(Player.getComponentType());

            if (Objects.nonNull(player)) {

                if (!recipesScanned) {

                    scanRecipes();

                    recipesScanned = true;
                }

                startLoopSafe(player);
            }
        });

        plugin.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {

            if (event.getEntity() instanceof Player player)
                startLoopSafe(player);

        });
    }

    private void startLoopSafe(Player player) {

        if (!activeSmeltLoops.getOrDefault(player.getUuid(), false)) {

            activeSmeltLoops.put(player.getUuid(), true);

            smeltingLoop(player);

        }

        if (!activeRepairLoops.getOrDefault(player.getUuid(), false)) {

            activeRepairLoops.put(player.getUuid(), true);

            repairLoop(player);

        }
    }

    private void smeltingLoop(Player player) {

        try {

            if (Objects.isNull(player.getWorld())
                    || !player.getWorld().isAlive()) {

                activeSmeltLoops.remove(player.getUuid());

                return;
            }

            if (isWearingHands(player)) {

                processSmeltingTick(player);

                WorldHelper.waitTicks(player.getWorld(), 5, () -> smeltingLoop(player));

            } else {

                activeSmeltLoops.remove(player.getUuid());
                playerProgress.remove(player.getUuid());

            }

        } catch (Exception e) {

            activeSmeltLoops.remove(player.getUuid());

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void repairLoop(Player player) {

        try {

            if (Objects.isNull(player.getWorld())
                    || !player.getWorld().isAlive()) {

                activeRepairLoops.remove(player.getUuid());
                repairAccumulator.remove(player.getUuid());

                return;
            }

            if (isWearingHands(player)) {

                boolean repaired = repairItemInHand(player);

                if (repaired) {

                    int accumulated = repairAccumulator.getOrDefault(player.getUuid(), 0) + 1;

                    if (accumulated >= 25) {

                        damageGloves(player);

                        accumulated = 0;

                    }

                    repairAccumulator.put(player.getUuid(), accumulated);
                }

                WorldHelper.waitTicks(player.getWorld(), 100, () -> repairLoop(player));

            } else {

                activeRepairLoops.remove(player.getUuid());
                repairAccumulator.remove(player.getUuid());

            }

        } catch (Exception e) {

            activeRepairLoops.remove(player.getUuid());

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void processSmeltingTick(Player player) {

        ItemStack heldItem = player.getInventory().getItemInHand();

        if (Objects.isNull(heldItem)
                || heldItem.isEmpty()) {

            playerProgress.remove(player.getUuid());

            return;
        }

        String inputId = heldItem.getItemId();

        ThermalRecipe recipe = findRecipe(inputId);

        if (Objects.nonNull(recipe)) {

            SmeltProgress progress = playerProgress.computeIfAbsent(player.getUuid(), k ->
                    new SmeltProgress());

            if (!inputId.equals(progress.getId())) {

                progress.setId(inputId);
                progress.setProgressTicks(0);

            }

            progress.setProgressTicks(progress.getProgressTicks() + 5);

            if (progress.getProgressTicks() >= SMELT_TIME_TICKS) {

                boolean success = smeltOneItem(player, heldItem, recipe);

                if (success) {

                    progress.setProgressTicks(0);

                } else {

                    progress.setProgressTicks(SMELT_TIME_TICKS);

                }
            }

        } else {

            playerProgress.remove(player.getUuid());

        }
    }

    private boolean smeltOneItem(Player player, ItemStack heldItem, ThermalRecipe recipe) {

        if (heldItem.getQuantity() < recipe.inputQty)
            return false;

        int currentQty = heldItem.getQuantity();
        int remainingQty = currentQty - recipe.inputQty;

        ItemStack product = new ItemStack(recipe.outputId, recipe.outputQty);

        ItemContainer inv = player.getInventory().getCombinedHotbarFirst();

        int slot = player.getInventory().getActiveHotbarSlot();

        if (Objects.equals(remainingQty, 0)) {

            player.getInventory().getHotbar().replaceItemStackInSlot((short) slot, heldItem, product);

            player.sendInventory();

            return true;
        }

        if (inv.canAddItemStack(product)) {

            ItemStack reducedStack = new ItemStack(heldItem.getItemId(), remainingQty, heldItem.getMetadata());

            player.getInventory().getHotbar().replaceItemStackInSlot((short) slot, heldItem, reducedStack);

            inv.addItemStack(product);

            player.sendInventory();

            return true;

        } else {

            return false;

        }
    }

    private ThermalRecipe findRecipe(String inputId) {

        if (recipeIds.containsKey(inputId))
            return recipeIds.get(inputId);

        Item itemAsset = Item.getAssetMap().getAsset(inputId);

        if (Objects.nonNull(itemAsset)) {

            ItemResourceType[] resTypes = itemAsset.getResourceTypes();

            if (Objects.nonNull(resTypes)) {

                for (ItemResourceType res : resTypes) {

                    if (Objects.nonNull(res.id)
                            && recipeResourceNames.containsKey(res.id))
                        return recipeTags.get(recipeResourceNames.get(res.id));

                }
            }

            if (Objects.nonNull(itemAsset.getData())) {

                IntSet itemTags = itemAsset.getData().getExpandedTagIndexes();

                IntIterator it = itemTags.iterator();

                while (it.hasNext()) {

                    int tId = it.nextInt();

                    if (recipeTags.containsKey(tId))
                        return recipeTags.get(tId);
                }
            }
        }

        return null;
    }

    private void scanRecipes() {

        try {
            DefaultAssetMap<String, CraftingRecipe> assetMap = CraftingRecipe.getAssetMap();

            if (Objects.isNull(assetMap))
                return;

            for (CraftingRecipe recipe : assetMap.getAssetMap().values()) {

                if (Objects.isNull(recipe.getInput())
                        || recipe.getInput().length != 1)
                    continue;

                if (Objects.isNull(recipe.getPrimaryOutput()))
                    continue;

                if (!isThermalBench(recipe))
                    continue;

                MaterialQuantity inputMQ = recipe.getInput()[0];
                MaterialQuantity outputMQ = recipe.getPrimaryOutput();

                String outId = outputMQ.getItemId();

                if (Objects.isNull(outId))
                    continue;

                ThermalRecipe thermalData = new ThermalRecipe(outId, inputMQ.getQuantity(), outputMQ.getQuantity());

                String inId = inputMQ.getItemId();

                if (Objects.nonNull(inId)) {

                    recipeIds.put(inId, thermalData);

                } else {

                    String tagString = getField(inputMQ, "resourceTypeId");

                    if (Objects.isNull(tagString)
                            || tagString.equals("null"))
                        tagString = getField(inputMQ, "tag");

                    if (Objects.nonNull(tagString)
                            && !tagString.equals("null")) {

                        int tagId = AssetRegistry.getOrCreateTagIndex(tagString);

                        recipeTags.put(tagId, thermalData);
                        recipeResourceNames.put(tagString, tagId);

                    }
                }
            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private String getField(Object obj, String fieldName) {

        try {

            Field f = obj.getClass().getDeclaredField(fieldName);

            f.setAccessible(true);

            return (String) f.get(obj);

        } catch (Exception e) {

            return null;

        }
    }

    private boolean isThermalBench(CraftingRecipe recipe) {

        BenchRequirement[] reqs = recipe.getBenchRequirement();

        if (Objects.isNull(reqs))
            return false;

        for (BenchRequirement req : reqs) {

            if (Objects.nonNull(req.id)) {

                String id = req.id.toLowerCase();

                if (id.contains("furnace")
                        || id.contains("campfire")
                        || id.contains("smelter"))
                    return true;

            }

            if (Objects.nonNull(req.categories )) {

                for (String cat : req.categories) {
                    if (cat.toLowerCase().contains("cooking")
                            || cat.toLowerCase().contains("smelting"))
                        return true;
                }

            }
        }

        return false;
    }

    private boolean repairItemInHand(Player player) {

        Inventory inventory = player.getInventory();

        byte activeSlot = inventory.getActiveHotbarSlot();

        if (Objects.equals(activeSlot, (byte) -1))
            return false;

        ItemContainer hotbar = inventory.getHotbar();
        ItemStack currentItem = hotbar.getItemStack(activeSlot);

        if (Objects.isNull(currentItem)
                || currentItem.isEmpty())
            return false;

        for (String blacklist : REPAIR_BLACKLIST) {

            if (blacklist.equals(currentItem.getItemId()))
                return false;

        }

        if (currentItem.getDurability() < currentItem.getMaxDurability()) {

            hotbar.replaceItemStackInSlot(activeSlot, currentItem, currentItem.withIncreasedDurability(1.0));

            player.sendInventory();

            return true;
        }

        return false;
    }

    private void damageGloves(Player player) {

        ItemContainer armor = player.getInventory().getArmor();

        for (int i = 0; i < armor.getCapacity(); i++) {

            ItemStack stack = armor.getItemStack((short) i);

            if (Objects.nonNull(stack)
                    && HANDS_ID.equals(stack.getItemId())) {

                ItemStack damaged = stack.withIncreasedDurability(-1.0);

                armor.replaceItemStackInSlot((short) i, stack, damaged);

                player.sendInventory();

                return;
            }
        }
    }

    public static boolean isWearingHands(Player player) {

        try {

            if (Objects.isNull(player.getInventory())
                    || Objects.isNull(player.getInventory().getArmor()))
                return false;

            ItemContainer armor = player.getInventory().getArmor();

            for (int i = 0; i < armor.getCapacity(); i++) {

                ItemStack stack = armor.getItemStack((short) i);

                if (Objects.nonNull(stack)
                        && HANDS_ID.equals(stack.getItemId()))
                    return true;
            }

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }

        return false;
    }
}