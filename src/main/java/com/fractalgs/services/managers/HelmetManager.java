package com.fractalgs.services.managers;

import com.fractalgs.services.events.LookAtBlockService;
import com.fractalgs.services.ui.HelmetTelemetryHud;
import com.fractalgs.utils.api.WorldHelper;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.worldgen.biome.Biome;
import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import com.hypixel.hytale.server.worldgen.zone.ZoneDiscoveryConfig;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HelmetManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final String HELMET_ID = "Armor_Copper_Head";

    // -------- LookAtBlock integration --------
    private final LookAtBlockService lookService;

    // -------- REALTIME CONFIG --------
    private static final int POS_EVERY_TICKS = 2;
    private static final int BIOME_MAX_EVERY_TICKS = 20;
    private static final Map<Ref<EntityStore>, TrackState> TRACKING = new ConcurrentHashMap<>();
    private static final Map<Ref<EntityStore>, HelmetTelemetryHud> HUDS = new ConcurrentHashMap<>();

    private static volatile Class<?> cachedGenClass = null;
    private static volatile Method cachedZoneBiomeMethod = null;

    private static final class TrackState {
        int ticksSinceBiome = 9999;
        int lastChunkX = Integer.MIN_VALUE;
        int lastChunkZ = Integer.MIN_VALUE;
        String lastZoneAndBiome = "N/A";

        double lastX = Double.NaN;
        double lastY = Double.NaN;
        double lastZ = Double.NaN;

        String lastLookMaterial = null;
        String lastLookMod = null;
        String lastLookIconItemId = null;
    }

    public HelmetManager(LookAtBlockService lookService) {
        this.lookService = lookService;
    }

    public void register(JavaPlugin plugin) {

        plugin.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {

            Holder<EntityStore> holder = event.getHolder();
            Player player = holder.getComponent(Player.getComponentType());

            if (Objects.nonNull(player))
                WorldHelper.waitTicks(event.getWorld(), 10, () -> checkPlayer(player));
        });

        plugin.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, event -> {

            if (event.getEntity() instanceof Player player)
                if (Objects.equals(event.getItemContainer(), player.getInventory().getArmor()))
                    checkPlayer(player);
        });
    }

    private void checkPlayer(Player player) {
        try {

            if (isWearingHelmet(player)) {
                String coords = getCoords(player);
                LOGGER.at(Level.INFO).log(
                        "Player %s is wearing a helmet at coordinates: %s"
                                .formatted(player.getDisplayName(), coords));

                String zoneAndBiome = getZoneAndBiome(player);
                LOGGER.at(Level.INFO).log(
                        "Player %s is in %s"
                                .formatted(player.getDisplayName(), zoneAndBiome));

                startRealtime(player);
            } else {
                stopRealtime(player);
            }

        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log(e.getMessage());
        }
    }

    public static PlayerRef getPlayerRef(Player player) {
        Ref<EntityStore> ref = player.getReference();
        Store<EntityStore> store = ref.getStore();
        return store.getComponent(ref, PlayerRef.getComponentType());
    }

    public static boolean isWearingHelmet(Player player) {
        try {

            if (Objects.isNull(player.getInventory())
                    || Objects.isNull(player.getInventory().getArmor()))
                return false;

            ItemContainer armor = player.getInventory().getArmor();

            for (int i = 0; i < armor.getCapacity(); i++) {

                ItemStack stack = armor.getItemStack((short) i);

                if (Objects.nonNull(stack)
                        && HELMET_ID.equals(stack.getItemId()))
                    return true;
            }

        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log(e.getMessage());
            return false;
        }

        return false;
    }

    public static String getCoords(Player player) {
        try {
            if (player == null) return "Player is null.";

            PlayerRef pr = getPlayerRef(player);
            if (pr == null) return "No PlayerRef.";

            Vector3d pos = pr.getTransform().getPosition();
            if (pos == null) return "Position is null.";

            return String.format("X: %.2f Y: %.2f Z: %.2f", pos.getX(), pos.getY(), pos.getZ());
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log(e.getMessage());
            return "Unable to get coordinates.";
        }
    }

    public static String getZoneAndBiome(Player player) {
        try {
            World world = player.getWorld();
            if (world == null) return "No world.";

            PlayerRef playerRef = getPlayerRef(player);
            if (playerRef == null) return "No PlayerRef.";

            Vector3d pos = playerRef.getTransform().getPosition();
            int x = (int) pos.getX();
            int z = (int) pos.getZ();
            int seed = (int) world.getWorldConfig().getSeed();

            IWorldGen gen = world.getChunkStore().getGenerator();
            if (gen == null) return "No world generator.";

            Method m = getZoneBiomeMethodCached(gen);
            if (m == null) return "WorldGen has no getZoneBiomeResultAt(int,int,int) in this build.";

            Object resultObj = m.invoke(gen, seed, x, z);
            if (!(resultObj instanceof ZoneBiomeResult r)) return "getZoneBiomeResultAt returned " + resultObj;

            Biome biome = r.getBiome();
            Zone zone = r.getZoneResult().getZone();
            ZoneDiscoveryConfig dc = zone.discoveryConfig();

            return "Bioma: " + biome.getName()
                    + " | Zona: " + dc.zone()
                    + " | Region/Tier: " + zone.name();

        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log(e.getMessage());
            return "Unable to get zone/biome.";
        }
    }

    // ================= REALTIME LOOP =================
    private void startRealtime(Player player) {
        Ref<EntityStore> key = player.getReference();
        TrackState prev = TRACKING.putIfAbsent(key, new TrackState());
        if (prev != null) return;

        PlayerRef pr = getPlayerRef(player);
        if (pr != null) {
            var hud = new HelmetTelemetryHud(pr);
            HUDS.put(key, hud);
            player.getHudManager().setCustomHud(pr, hud);
            hud.show();
        }

        tickRealtime(player, key);
    }

    private void stopRealtime(Player player) {
        TRACKING.remove(player.getReference());

        PlayerRef pr = getPlayerRef(player);
        if (pr != null) {
            player.getHudManager().setCustomHud(pr, null);
        }

        HUDS.remove(player.getReference());
        if (lookService != null) lookService.clear(player);
    }

    private void tickRealtime(Player player, Ref<EntityStore> key) {
        TrackState st = TRACKING.get(key);
        if (st == null) return;

        if (!isWearingHelmet(player)) {
            stopRealtime(player);
            return;
        }

        try {
            PlayerRef pr = getPlayerRef(player);
            World world = player.getWorld();

            if (pr == null || world == null) {
                World w = player.getWorld();
                if (w != null) WorldHelper.waitTicks(w, 5, () -> tickRealtime(player, key));
                return;
            }

            Vector3d pos = pr.getTransform().getPosition();
            double x = pos.getX(), y = pos.getY(), z = pos.getZ();

            int chunkX = ((int) x) >> 4;
            int chunkZ = ((int) z) >> 4;

            st.ticksSinceBiome += POS_EVERY_TICKS;
            boolean chunkChanged = (chunkX != st.lastChunkX) || (chunkZ != st.lastChunkZ);

            if (chunkChanged || st.ticksSinceBiome >= BIOME_MAX_EVERY_TICKS) {
                st.lastChunkX = chunkX;
                st.lastChunkZ = chunkZ;
                st.ticksSinceBiome = 0;
                st.lastZoneAndBiome = computeZoneAndBiome(world, (int) x, (int) z);
            }

            LookAtBlockService.LookData look =
                    (lookService != null) ? lookService.getLastLook(player) : null;

            pushToCustomUi(player, x, y, z, st.lastZoneAndBiome, look);

        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log(e.getMessage());
            stopRealtime(player);
            return;
        }

        World w = player.getWorld();
        if (w != null) WorldHelper.waitTicks(w, POS_EVERY_TICKS, () -> tickRealtime(player, key));
        else stopRealtime(player);
    }

    private static String computeZoneAndBiome(World world, int x, int z) {
        try {
            int seed = (int) world.getWorldConfig().getSeed();
            IWorldGen gen = world.getChunkStore().getGenerator();
            if (gen == null) return "No worldgen";

            Method m = getZoneBiomeMethodCached(gen);
            if (m == null) return "No getZoneBiomeResultAt";

            Object resultObj = m.invoke(gen, seed, x, z);
            if (!(resultObj instanceof ZoneBiomeResult r)) return "Bad ZoneBiomeResult";

            Biome biome = r.getBiome();
            Zone zone = r.getZoneResult().getZone();
            ZoneDiscoveryConfig dc = zone.discoveryConfig();

            String biomeName = humanizeId(biome != null ? biome.getName() : null);
            String zoneName  = humanizeId(dc != null ? dc.zone() : null);

            return zoneName + " | " + biomeName;
        } catch (Exception e) {
            return "Biome lookup failed";
        }
    }

    private static String humanizeId(String id) {
        if (id == null || id.isBlank()) return "N/A";

        String s = id.replace('_', ' ').trim().replaceAll("\\s+", " ");

        boolean hasUpper = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) { hasUpper = true; break; }
        }

        if (hasUpper) return s;

        String[] parts = s.toLowerCase(Locale.ROOT).split(" ");
        StringBuilder out = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            out.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(' ');
        }
        return out.toString().trim();
    }

    private static Method getZoneBiomeMethodCached(IWorldGen gen) {
        Class<?> cls = gen.getClass();
        if (cachedGenClass == cls) return cachedZoneBiomeMethod;

        try {
            Method m = cls.getMethod("getZoneBiomeResultAt", int.class, int.class, int.class);
            cachedGenClass = cls;
            cachedZoneBiomeMethod = m;
            return m;
        } catch (NoSuchMethodException e) {
            cachedGenClass = cls;
            cachedZoneBiomeMethod = null;
            return null;
        }
    }

    private final com.fractalgs.services.ui.providers.HelmetHudAssembler assembler =
            new com.fractalgs.services.ui.providers.HelmetHudAssembler(
                    java.util.List.of(
                            new com.fractalgs.services.ui.providers.LookDataProvider(),
                            new com.fractalgs.services.ui.providers.CoordsProvider(),
                            new com.fractalgs.services.ui.providers.ZoneBiomeProvider()
                    )
            );

    private void pushToCustomUi(Player player,
                                double x, double y, double z,
                                String zoneAndBiome,
                                LookAtBlockService.LookData look) {

        TrackState st = TRACKING.get(player.getReference());
        if (st == null) return;

        boolean moved =
                Double.isNaN(st.lastX) ||
                        Math.abs(x - st.lastX) > 0.05 ||
                        Math.abs(y - st.lastY) > 0.05 ||
                        Math.abs(z - st.lastZ) > 0.05;

        String mat = (look != null) ? look.materialId() : null;
        String mod = (look != null) ? look.modOwner() : null;
        String icon = (look != null) ? look.iconItemId() : null;

        boolean lookChanged =
                !Objects.equals(st.lastLookMaterial, mat) ||
                        !Objects.equals(st.lastLookMod, mod) ||
                        !Objects.equals(st.lastLookIconItemId, icon);

        if (!moved && !lookChanged) return;

        st.lastX = x; st.lastY = y; st.lastZ = z;
        st.lastLookMaterial = mat;
        st.lastLookMod = mod;
        st.lastLookIconItemId = icon;

        var hud = HUDS.get(player.getReference());
        if (hud == null) return;

        var ctx = new com.fractalgs.services.ui.providers.HelmetHudContext(
                x, y, z,
                zoneAndBiome,
                look
        );

        var data = assembler.build(ctx);
        hud.setData(data);
    }
}