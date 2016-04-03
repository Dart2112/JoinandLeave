package net.lapismc.joinleave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class main extends JavaPlugin implements Listener {

    public ArrayList<UUID> players = new ArrayList<>();
    public File file = new File(this.getDataFolder() + File.separator + "players.yml");
    public YamlConfiguration yaml;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        loadPlayers();
        saveDefaultConfig();
        Bukkit.getLogger().info("[JoinandLeave] Enabled v." + this.getDescription().getVersion() + "!");
    }

    @Override
    public void onDisable() {
        savePlayers();
        Bukkit.getLogger().info("[JoinandLeave] Disabled v." + this.getDescription().getVersion() + "!");
    }

    public void loadPlayers() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            yaml.load(file);
            if (!yaml.contains("players")) {
                yaml.createSection("players");
                yaml.save(file);
                return;
            }
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save, load or create join and leave messages players file");
            Bukkit.getLogger().log(Level.SEVERE, "Error follows, Please report this to dart2112");
            e.printStackTrace();
        }
        List<String> yamlPlayers = yaml.getStringList("players");
        for (String pUUID : yamlPlayers) {
            players.add(UUID.fromString(pUUID));
        }
    }

    public void savePlayers() {
        List<String> yamlPlayers = yaml.getStringList("players");
        for (UUID uuid : players) {
            yamlPlayers.add(uuid.toString());
        }
        yaml.set("players", yamlPlayers);
        try {
            yaml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save join and leave messages players file");
            Bukkit.getLogger().log(Level.SEVERE, "Error follows, Please report this to dart2112");
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!players.contains(e.getPlayer().getUniqueId())) {
            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("FirstJoin").replace("(player)", e.getPlayer().getName())));
            players.add(e.getPlayer().getUniqueId());
        }
        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Join").replace("(player)", e.getPlayer().getName())));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Quit").replace("(player)", e.getPlayer().getName())));
    }

}
