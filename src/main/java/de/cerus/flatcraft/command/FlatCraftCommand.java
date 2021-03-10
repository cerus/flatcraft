package de.cerus.flatcraft.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import de.cerus.flatcraft.cache.GameCache;
import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.storage.PapyrusStorage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@CommandAlias("flatcraft|flat")
public class FlatCraftCommand extends BaseCommand {

    private final Set<UUID> flag = new HashSet<>();

    @Dependency
    private GameCache gameCache;
    @Dependency
    private PapyrusStorage papyrusStorage;

    @Default
    public void handle(final Player player) {
        player.sendMessage("§8[§eFlatcraft§8] §7/flat load");
        player.sendMessage("§8[§eFlatcraft§8] §7/flat show");
        player.sendMessage("§8[§eFlatcraft§8] §7/flat unload");
    }

    @Subcommand("load")
    public void handleLoad(final Player player, @Optional final String seed) {
        if (this.flag.contains(player.getUniqueId())) {
            return;
        }

        if (this.gameCache.isPlaying(player)) {
            player.sendMessage("§8[§eFlatcraft§8] §eYour world is already loaded!");
            player.sendMessage("§8[§eFlatcraft§8] §7You can view your world by typing §b/flat show");
            return;
        }

        player.sendMessage("§8[§eFlatcraft§8] §7§oPlease wait...");
        this.papyrusStorage.exists(player.getUniqueId()).whenComplete((hasWorld, t) -> {
            if (!hasWorld) {
                player.sendMessage("§8[§eFlatcraft§8] §7§oGenerating world...");
                this.papyrusStorage.generate(player.getUniqueId(), seed == null ? null : seed.hashCode())
                        .whenComplete((world, throwable) -> this.handleLoad(player, null));
                return;
            }

            this.gameCache.loadGame(player).whenComplete((game, throwable) -> {
                if (throwable != null) {
                    player.sendMessage("§8[§eFlatcraft§8] §cError: " + throwable.getMessage());
                    return;
                }

                this.flag.remove(player.getUniqueId());
                this.handleShow(player);
            });
        });
    }

    @Subcommand("unload")
    public void handleUnload(final Player player) {
        if (this.flag.contains(player.getUniqueId())) {
            return;
        }

        if (!this.gameCache.isPlaying(player)) {
            player.sendMessage("§8[§eFlatcraft§8] §eYour world is not loaded!");
            return;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.sendMessage("§8[§eFlatcraft§8] §aYour game has been saved!");
        this.gameCache.unloadGame(player);
    }

    @Subcommand("show")
    public void handleShow(final Player player) {
        if (this.flag.contains(player.getUniqueId())) {
            return;
        }

        if (!this.gameCache.isPlaying(player)) {
            player.sendMessage("§8[§eFlatcraft§8] §eYour world is not loaded!");
            player.sendMessage("§8[§eFlatcraft§8] §7You can load your world by typing §b/flat load");
            return;
        }

        final FlatCraftGame game = this.gameCache.getCachedGame(player);
        game.render().forEach(player::sendMessage);
    }


    @Subcommand("interact")
    public void handleInteract(final Player player, final int x, final int y) {
        if (this.flag.contains(player.getUniqueId())) {
            return;
        }

        if (!this.gameCache.isPlaying(player)) {
            return;
        }

        this.gameCache.resetExpiration(player);
        if (this.gameCache.getCachedGame(player).handleInteract(x, y)) {
            this.handleShow(player);
        }
    }

    @Subcommand("select")
    public void handleSelect(final Player player, final int block) {
        if (this.flag.contains(player.getUniqueId())) {
            return;
        }

        if (!this.gameCache.isPlaying(player)) {
            return;
        }

        this.gameCache.resetExpiration(player);
        if (this.gameCache.getCachedGame(player).selectBlock(FlatBlock.BLOCK_MAP.get(block))) {
            this.handleShow(player);
        }
    }

    @Subcommand("mode")
    public void handleMode(final Player player, final String mode) {
        if (this.flag.contains(player.getUniqueId())) {
            return;
        }

        if (!this.gameCache.isPlaying(player)) {
            return;
        }

        final FlatCraftGame game = this.gameCache.getCachedGame(player);
        switch (mode.toLowerCase()) {
            case "walk":
                game.setMode(FlatCraftGame.MODE_WALK);
                break;
            case "place":
                game.setMode(FlatCraftGame.MODE_PLACE);
                break;
            case "break":
                game.setMode(FlatCraftGame.MODE_BREAK);
                break;
            default:
                return;
        }
        this.gameCache.resetExpiration(player);
        this.handleShow(player);
    }

}
