package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.events_log_tool.application.dto.Collaborator;
import org.springframework.stereotype.Component;

@Component
public class CommonService {

    private final ObjectMapper objectMapper;

    public CommonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Check if the user is an owner
     */
    public boolean isOwner(Collaborator aclJson, String userEmail) {

        return aclJson.getOwnedBy() != null && aclJson.getOwnedBy().getEmailId().equals(userEmail);
    }

    /**
     * Check if the user is a co-owner
     */
    public boolean isCoOwner(Collaborator aclJson, String userEmail) {

        return aclJson.getCoOwners().stream().anyMatch(coOwner -> coOwner.getEmailId().equals(userEmail));
    }

}
