package de.cerus.flatcraft;

import co.aikar.commands.PaperCommandManager;
import de.cerus.flatcraft.cache.GameCache;
import de.cerus.flatcraft.command.FlatCraftCommand;
import de.cerus.flatcraft.game.storage.FilePapyrusStorage;
import de.cerus.flatcraft.game.storage.PapyrusStorage;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class FlatCraftPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final PapyrusStorage papyrusStorage = new FilePapyrusStorage(new File(this.getDataFolder(), "worlds"));
        final GameCache gameCache = new GameCache(papyrusStorage);

        final PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerDependency(GameCache.class, gameCache);
        commandManager.registerDependency(PapyrusStorage.class, papyrusStorage);
        commandManager.registerCommand(new FlatCraftCommand());
    }

}
