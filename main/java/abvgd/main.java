package abvgd;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;

public class main extends JavaPlugin implements Listener {
    Biome currBiome = stringToBiome(Objects.requireNonNull(getConfig().getString("biomes.currentBiome")));
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }
    public static Biome stringToBiome(String biomeString) {
        try {
            return Biome.valueOf(biomeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("seasons")) {
            if (!(commandSender instanceof Player)) return true;
            Player player = (Player) commandSender;
            if (args.length == 0) {
                return false;
            }
            String season = args[0];
            switch (season) {
                case "winter":
                    currBiome = Biome.SNOWY_TAIGA;
                    getConfig().set("biomes.currentBiome", currBiome.name());
                    break;
                case "summer":
                    currBiome = Biome.WARM_OCEAN;
                    getConfig().set("biomes.currentBiome", currBiome.name());
                    break;
                case "spring":
                    currBiome = Biome.BAMBOO_JUNGLE;
                    getConfig().set("biomes.currentBiome", currBiome.name());
                    break;
                case "fall":
                    currBiome = Biome.DESERT;
                    getConfig().set("biomes.currentBiome", currBiome.name());
                    break;
                default:
                    player.sendMessage("§cInvalid season <winter, summer, spring, fall>");
            }
            if (season.equals("winter") || season.equals("summer") || season.equals("fall") || season.equals("spring")) {
                player.sendMessage("§aBiome successfully changed to -> " + "§r§e§l" + season);
            }
            saveConfig();
        }
        return true;
    }
    @EventHandler
    public void onLoadChunk(ChunkLoadEvent e) {
        ArrayList<Block> blockArray = new ArrayList<Block>();
        ArrayList<Block> waterArray = new ArrayList<Block>();
        if (e.getWorld().getEnvironment() == World.Environment.NORMAL) {
            Chunk chunk = e.getChunk();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 256; k++) {
                        Block block = chunk.getBlock(i, k, j);
                        if (currBiome != Biome.SNOWY_TAIGA) {
                            if (block.getType().equals(Material.SNOW)) blockArray.add(block);
                            if (block.getType().equals(Material.ICE)) waterArray.add(block);
                            block.setBiome(currBiome);
                        }
                        else {
                            block.setBiome(currBiome);
                        }
                    }
                }
            }
        }
        if (!blockArray.isEmpty()) for (Block block : blockArray) SchedulerRemove(block);
        if (!waterArray.isEmpty()) for (Block block : waterArray) SchedulerWaterRemove(block);
    }
    public void SchedulerRemove(Block block) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            block.setType(Material.AIR);
        }, 40);
    }
    public void SchedulerWaterRemove(Block block) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            block.setType(Material.WATER);
        }, 40);
    }
}
