package permission;

import manager.sheetManager.SheetManager;

public enum PermissionCheck {
    NONE {
        @Override
        public void check(String username, PermissionsType permission, SheetManager manager) {
            manager.checkNotNonePermission(username, permission);
        }
    },
    WRITER_OR_OWNER {
        @Override
        public void check(String username, PermissionsType permission, SheetManager manager) {
            manager.checkWriterOrOwnerPermission(username, permission);
        }
    };

    public abstract void check(String username, PermissionsType permission, SheetManager manager);
}
