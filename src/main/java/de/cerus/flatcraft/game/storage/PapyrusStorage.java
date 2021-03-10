package de.cerus.flatcraft.game.storage;

import de.cerus.flatcraft.game.FlatWorld;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a simple storage driver interface
 * Can store one world per player
 */
public interface PapyrusStorage {

    /**
     * Stores a world belonging to a player
     *
     * @param world The world
     * @param owner The owning player
     *
     * @return A callback
     */
    CompletableFuture<Void> store(FlatWorld world, UUID owner);

    /**
     * Loads the world of the owning player
     * Should return a fresh world if player has no world stored
     *
     * @param owner The owning player
     *
     * @return A callback
     */
    CompletableFuture<FlatWorld> load(UUID owner);

    /**
     * Generates a world if the player has none
     * Should load the world if the player has one
     *
     * @param owner The owner
     * @param seed  The seed (nullable)
     *
     * @return A callback
     */
    CompletableFuture<FlatWorld> generate(UUID owner, @Nullable Integer seed);

    CompletableFuture<Boolean> exists(UUID owner);

}
