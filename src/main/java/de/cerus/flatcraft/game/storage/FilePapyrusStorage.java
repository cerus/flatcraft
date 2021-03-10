package de.cerus.flatcraft.game.storage;

import de.cerus.flatcraft.game.FlatWorld;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.Nullable;

/**
 * Storage driver implementation for a normal file system
 */
public class FilePapyrusStorage implements PapyrusStorage {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final File directory;

    public FilePapyrusStorage(final File directory) {
        this.directory = directory;
    }

    @Override
    public CompletableFuture<Void> store(final FlatWorld world, final UUID owner) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        this.executorService.submit(() -> {
            final File file = new File(this.directory, owner + ".pyrs");

            // Make dirs and create file
            this.directory.mkdirs();
            try {
                file.createNewFile();
            } catch (final IOException e) {
                // Oh no - Error out
                future.completeExceptionally(e);
                return;
            }

            // Open stream and store world
            try (final FileOutputStream outputStream = new FileOutputStream(file)) {
                world.store(outputStream);
            } catch (final IOException e) {
                // Oh no - Error out
                future.completeExceptionally(e);
                return;
            }
            future.complete(null);
        });
        return future;
    }

    @Override
    public CompletableFuture<FlatWorld> load(final UUID owner) {
        final CompletableFuture<FlatWorld> future = new CompletableFuture<>();
        this.executorService.submit(() -> {
            // Check if world is stored
            final File file = new File(this.directory, owner + ".pyrs");
            if (!file.exists()) {
                future.complete(null);
                return;
            }

            // Open stream and load world
            try (final FileInputStream inputStream = new FileInputStream(file)) {
                final FlatWorld flatWorld = new FlatWorld(0);
                flatWorld.read(inputStream);
                future.complete(flatWorld);
            } catch (final IOException e) {
                // Oh no - Error out
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    @Override
    public CompletableFuture<FlatWorld> generate(final UUID owner, @Nullable final Integer seed) {
        if (new File(this.directory, owner + ".pyrs").exists()) {
            return this.load(owner);
        }

        final FlatWorld flatWorld = new FlatWorld(seed == null ? String.valueOf(System.currentTimeMillis()).hashCode() : seed);
        return this.store(flatWorld, owner).thenApply(unused -> flatWorld);
    }

    @Override
    public CompletableFuture<Boolean> exists(final UUID owner) {
        return CompletableFuture.completedFuture(new File(this.directory, owner + ".pyrs").exists());
    }

}
