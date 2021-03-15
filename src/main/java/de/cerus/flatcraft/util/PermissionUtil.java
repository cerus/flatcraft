package de.cerus.flatcraft.util;

import org.bukkit.permissions.Permissible;

public class PermissionUtil {

    private PermissionUtil() {
    }

    public static boolean hasPermission(final Permissible permissible, final String perm) {
        final String[] parts = perm.split("\\.");
        if (parts.length == 1) {
            return permissible.hasPermission(perm);
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            final String part = parts[i];

            if (i > 0) {
                stringBuilder.append(".").append(part);
            } else {
                stringBuilder.append(part);
            }

            if (permissible.hasPermission(stringBuilder.toString() + ".*")) {
                return true;
            }
        }
        return permissible.hasPermission(perm);
    }

}
