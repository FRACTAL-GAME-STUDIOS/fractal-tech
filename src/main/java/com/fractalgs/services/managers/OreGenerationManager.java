package com.fractalgs.services.managers;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class OreGenerationManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String[] ORE_CANDIDATES = {
            "Lost_OreStone"
    };

    private static final String[] REPLACEABLE_NAMES = {
            "Rock_Stone"
    };

    private static final int SAMPLES_PER_CHUNK = 100;
    private static final int MAX_VEIN_SIZE = 3;
    private static final int MIN_HEIGHT = 0;
    private static final int MAX_HEIGHT = 100;

    private final Random random = new Random();

    private final Set<Integer> replaceableIds = new HashSet<>();

    private final Set<Long> processedChunks = ConcurrentHashMap.newKeySet();

    private final Path dbFile = Path.of("ore_gen_database.dat");

    private boolean initialized = false;

    private volatile boolean isDirty = false;

    private int oreId = -1;

    private BlockType oreBlockType = null;

    public void register(JavaPlugin plugin) {

        loadDatabase();

        plugin.getEventRegistry().registerGlobal(ChunkPreLoadProcessEvent.class, event -> {

            if (!initialized)
                initializeIDs();

            if (oreId <= 0
                    || oreBlockType == null)
                return;

            WorldChunk chunk = event.getChunk();

            long chunkKey = getChunkKey(chunk.getX(), chunk.getZ());

            boolean isNew = event.isNewlyGenerated();
            boolean isProcessed = processedChunks.contains(chunkKey);

            if (isNew || !isProcessed) {

                generateOres(chunk);

                processedChunks.add(chunkKey);
                isDirty = true;

            }
        });

        Thread autoSaveThread = new Thread(() -> {

            while (true) {

                try {

                    Thread.sleep(300000);

                    if (isDirty)
                        saveDatabase();

                } catch (InterruptedException e) {
                    break;
                }
            }

        });

        autoSaveThread.setDaemon(true);
        autoSaveThread.start();

    }

    public void shutdown() {

        saveDatabase();

    }

    private void initializeIDs() {

        BlockTypeAssetMap<String, BlockType> map = BlockType.getAssetMap();

        for (String candidate : ORE_CANDIDATES) {

            int id = map.getIndex(candidate);

            if (id > 0) {

                this.oreId = id;

                this.oreBlockType = map.getAsset(id);

                break;
            }
        }

        for (String name : REPLACEABLE_NAMES) {

            int id = map.getIndex(name);

            if (id > 0)
                replaceableIds.add(id);
        }

        initialized = true;
    }

    private void generateOres(WorldChunk chunk) {

        for (int i = 0; i < SAMPLES_PER_CHUNK; i++) {

            int x = random.nextInt(32);
            int z = random.nextInt(32);
            int y = random.nextInt(MAX_HEIGHT - MIN_HEIGHT) + MIN_HEIGHT;

            int veinSize = random.nextInt(MAX_VEIN_SIZE) + 1;

            generateVein(chunk, x, y, z, veinSize);
        }
    }

    private void generateVein(WorldChunk chunk, int startX, int startY, int startZ, int size) {

        int x = startX;
        int y = startY;
        int z = startZ;

        for (int j = 0; j < size; j++) {

            if (x >= 0 && x < 32
                    && z >= 0 && z < 32
                    && y > 0) {

                try {

                    int currentId = chunk.getBlock(x, y, z);

                    if (replaceableIds.contains(currentId)
                            && Objects.nonNull(this.oreBlockType))
                        chunk.setBlock(x, y, z, this.oreId, this.oreBlockType, 0, 0, 0);

                } catch (Exception ignored) {}
            }

            int dir = random.nextInt(6);

            switch (dir) {
                case 0: x++; break;
                case 1: x--; break;
                case 2: y++; break;
                case 3: y--; break;
                case 4: z++; break;
                case 5: z--; break;
            }
        }
    }

    private long getChunkKey(int x, int z) {

        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;

    }

    private void loadDatabase() {

        if (!Files.exists(dbFile))
            return;

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(dbFile.toFile())))) {

            int count = dis.readInt();

            for (int i = 0; i < count; i++)
                processedChunks.add(dis.readLong());

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }

    private void saveDatabase() {

        if (!isDirty)
            return;

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dbFile.toFile())))) {

            dos.writeInt(processedChunks.size());

            for (Long key : processedChunks)
                dos.writeLong(key);

            isDirty = false;

        } catch (Exception e) {

            LOGGER.at(Level.WARNING).log(e.getMessage());

        }
    }
}