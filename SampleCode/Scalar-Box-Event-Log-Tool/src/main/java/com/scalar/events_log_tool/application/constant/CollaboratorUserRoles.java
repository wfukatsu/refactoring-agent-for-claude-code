package com.scalar.events_log_tool.application.constant;

public enum CollaboratorUserRoles {
    OWNER, CO_OWNER, MEMBER, REVIEWER;

    public static boolean isValidRole(String role) {
        for (CollaboratorUserRoles userRole : CollaboratorUserRoles.values()) {
            if (userRole.name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
