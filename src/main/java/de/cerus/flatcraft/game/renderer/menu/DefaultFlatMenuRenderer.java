package de.cerus.flatcraft.game.renderer.menu;

import de.cerus.flatcraft.game.FlatCraftGame;
import de.cerus.flatcraft.game.chunk.FlatBlock;
import de.cerus.flatcraft.game.renderer.FlatCanvas;
import de.cerus.flatcraft.util.Vec3;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

/**
 * Default menu renderer
 */
public class DefaultFlatMenuRenderer implements FlatMenuRenderer {

    @Override
    public List<Component> render(final FlatCraftGame game, final int maxHeight, final int maxWidth) {
        final FlatCanvas canvas = new FlatCanvas(maxWidth, maxHeight - 2);

        // Separator
        for (int x = 0; x < maxWidth; x++) {
            canvas.setBlock(x, maxHeight - 6, '▂', new Vec3(73, 73, 73));
        }

        final Vec3 colorActive = new Vec3(255, 255, 255);
        final Vec3 colorInactive = new Vec3(120, 120, 120);

        // --- Menu ---
        // Walk mode button
        canvas.setBlock(2, maxHeight - 4, '☜', game.getMode() == FlatCraftGame.MODE_WALK ? colorActive : colorInactive);
        canvas.event(HoverEvent.showText(Component.text("Walk mode", TextColor.color(game.getMode() == FlatCraftGame.MODE_WALK
                ? colorActive : colorInactive))), 2, maxHeight - 4);
        canvas.event(ClickEvent.runCommand("/flatcraft mode walk"), 2, maxHeight - 4);

        // Place mode button
        canvas.setBlock(4, maxHeight - 4, '▆', game.getMode() == FlatCraftGame.MODE_PLACE ? colorActive : colorInactive);
        canvas.event(HoverEvent.showText(Component.text("Place mode", TextColor.color(game.getMode() == FlatCraftGame.MODE_PLACE
                ? colorActive : colorInactive))), 4, maxHeight - 4);
        canvas.event(ClickEvent.runCommand("/flatcraft mode place"), 4, maxHeight - 4);

        // Break mode button
        canvas.setBlock(6, maxHeight - 4, '⚒', game.getMode() == FlatCraftGame.MODE_BREAK ? colorActive : colorInactive);
        canvas.event(HoverEvent.showText(Component.text("Break mode", TextColor.color(game.getMode() == FlatCraftGame.MODE_BREAK
                ? colorActive : colorInactive))), 6, maxHeight - 4);
        canvas.event(ClickEvent.runCommand("/flatcraft mode break"), 6, maxHeight - 4);

        // Save button
        canvas.setBlock(8, 13, '✔', new Vec3(81, 222, 36));
        canvas.event(ClickEvent.runCommand("/flatcraft unload"), 8, 13);
        canvas.event(HoverEvent.showText(Component.text("§aSave game and quit")), 8, 13);

        // --- Inventory ---
        FlatBlock.BLOCK_MAP.forEach((integer, block) -> {
            if (block == FlatBlock.BLOCK_AIR) {
                return;
            }
            this.setInventoryBlock(game, canvas, block, integer - 1, integer);
        });

        final List<Component> components = canvas.toComponentList();
        components.add(0, Component.text("   Inventory"));
        components.add(Component.text(" Pos: X " + game.getWorld().getPlayer().getX() + " Y " + game.getWorld().getPlayer().getY()));
        return components;
    }

    private void setInventoryBlock(final FlatCraftGame game, final FlatCanvas canvas, final FlatBlock block, final int index, final int commandIndex) {
        final int x = (((index % 3) + 1) * 2);
        final int y = (((index / 3) + 1) * 2) - 1;
        canvas.setBlock(x, y, block.getDisplayChar(), block.getColor());
        canvas.event(HoverEvent.showText(Component.text("§f" + block.getFormattedName())), x, y);
        canvas.event(ClickEvent.runCommand("/flatcraft select " + commandIndex), x, y);
        if (game.getSelectedBlock() == block) {
            canvas.setBlock(x, y + 1, '▀', new Vec3(255, 255, 255));
        }
    }

}
