package com.fractalgs.utils.api;

import com.hypixel.hytale.server.core.universe.world.World;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class WorldHelper {

    public static void executeOnWorldThread(World world, Runnable task) {
        world.execute(task);
    }

    public static void waitTicks(World world, int ticks, Runnable callback) {

        if (ticks <= 0) {

            executeOnWorldThread(world, callback);

            return;
        }

        Timer timer = new Timer("WorldHelper-WaitTicks", true);

        final long startTick = world.getTick();
        final long targetTick = startTick + ticks;
        
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                long currentTick = world.getTick();

                if (currentTick >= targetTick) {

                    world.execute(() -> {

                        try {

                            callback.run();

                        } catch (Exception e) {

                            world.getLogger().at(Level.WARNING).log("Error in waitTicks callback: " + e.getMessage());

                        }
                    });

                    timer.cancel();
                }
            }
        }, 0, 50);
    }

}
