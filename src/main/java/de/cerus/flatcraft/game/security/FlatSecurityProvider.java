package de.cerus.flatcraft.game.security;

import de.cerus.flatcraft.game.security.context.BlockSecurityContext;
import de.cerus.flatcraft.game.security.context.FlatSecurityContext;
import de.cerus.flatcraft.game.security.context.MoveSecurityContext;

/**
 * Checks if player is allowed to do certain actions
 */
public interface FlatSecurityProvider {

    boolean checkAction(FlatAction action, FlatSecurityContext securityContext);

    enum FlatAction {
        BREAK_BLOCK(BlockSecurityContext.class),
        PLACE_BLOCK(BlockSecurityContext.class),
        MOVE(MoveSecurityContext.class);

        private final Class<? extends FlatSecurityContext> securityContextClass;

        FlatAction(final Class<? extends FlatSecurityContext> securityContextClass) {
            this.securityContextClass = securityContextClass;
        }

        public Class<? extends FlatSecurityContext> getSecurityContextClass() {
            return this.securityContextClass;
        }

    }

}
