package de.cerus.flatcraft.cache;

import co.aikar.commands.lib.expiringmap.ExpiringMap;
import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.FlatWorld;
import de.cerus.flatcraft.game.storage.PapyrusStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Cache for running games
 */
public class GameCache {

    private final Map<UUID, FlatCraftGame> gameMap = new HashMap<>();
    private final PapyrusStorage papyrusStorage;
    private final ExpiringMap<UUID, Long> lastUseMap = ExpiringMap.builder()
            .expiration(5, TimeUnit.MINUTES)
            .expirationListener((o, o2) -> {
                // Save and remove inactive games
                FlatCraftGame game = this.gameMap.remove(o);
                this.dispose(game, (UUID) o, true);
            })
            .build();

    public GameCache(final PapyrusStorage papyrusStorage) {
        this.papyrusStorage = papyrusStorage;
    }

    /**
     * Retrieves a cached game
     *
     * @param owner The owner of the game
     *
     * @return A cached game or null
     */
    public FlatCraftGame getCachedGame(final Player owner) {
        if (System.currentTimeMillis() - this.lastUseMap.get(owner.getUniqueId()) > TimeUnit.MINUTES.toMillis(5)) {
            return null;
        }
        return this.gameMap.get(owner.getUniqueId());
    }

    /**
     * Reset the expiration timer
     *
     * @param owner The player
     */
    public void resetExpiration(final Player owner) {
        this.lastUseMap.put(owner.getUniqueId(), System.currentTimeMillis());
        this.lastUseMap.resetExpiration(owner.getUniqueId());
    }

    /**
     * Save and unload a game
     *
     * @param owner The owner of the game
     */
    public void unloadGame(final Player owner) {
        this.dispose(this.gameMap.remove(owner.getUniqueId()), owner.getUniqueId(), false);
    }

    /**
     * Load a game
     *
     * @param owner The owner of the game
     *
     * @return A callback
     */
    public CompletableFuture<FlatCraftGame> loadGame(final Player owner) {
        return this.papyrusStorage.load(owner.getUniqueId())
                .thenApply(world -> world == null ? new FlatWorld(String.valueOf(System.currentTimeMillis()).hashCode()) : world)
                .whenComplete((world, throwable) -> {
                    this.gameMap.put(owner.getUniqueId(), new FlatCraftGame(world));
                    this.lastUseMap.put(owner.getUniqueId(), System.currentTimeMillis());
                })
                .thenApply(world -> this.gameMap.get(owner.getUniqueId()));
    }

    /**
     * Check if player has a cached game or not
     *
     * @param player
     *
     * @return
     */
    public boolean isPlaying(final Player player) {
        return this.gameMap.containsKey(player.getUniqueId());
    }

    public long getTimeUntilExpiration(final Player owner) {
        return this.lastUseMap.get(owner.getUniqueId()) + TimeUnit.MINUTES.toMillis(5) - System.currentTimeMillis();
    }

    private void dispose(final FlatCraftGame game, final UUID owner, final boolean msg) {
        final Player player = Bukkit.getPlayer(owner);
        if (player != null && msg) {
            player.sendMessage("§8[§eFlatcraft§8] §7Your world has been unloaded and saved " +
                    "because you have been inactive for more than 5 minutes.");
        }

        this.papyrusStorage.store(game.getWorld(), owner);
    }

}
