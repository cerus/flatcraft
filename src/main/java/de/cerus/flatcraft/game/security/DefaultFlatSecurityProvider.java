package de.cerus.flatcraft.game.security;

import de.cerus.flatcraft.game.security.context.FlatSecurityContext;
import de.cerus.flatcraft.util.PermissionUtil;

public class DefaultFlatSecurityProvider implements FlatSecurityProvider {

    @Override
    public boolean checkAction(final FlatAction action, final FlatSecurityContext securityContext) {
        if (!action.getSecurityContextClass().isAssignableFrom(securityContext.getClass())) {
            System.err.println("Warning: FlatCraft security: Provided context class does not match the required context class");
            return false;
        }

        // We don't need to check anything by default except permissions
        switch (action) {
            case BREAK_BLOCK:
                return PermissionUtil.hasPermission(securityContext.getPlayer(), "flatcraft.game.action.blockbreak");
            case PLACE_BLOCK:
                return PermissionUtil.hasPermission(securityContext.getPlayer(), "flatcraft.game.action.blockplace");
            case MOVE:
                return PermissionUtil.hasPermission(securityContext.getPlayer(), "flatcraft.game.action.move");
        }
        return false;
    }

}
