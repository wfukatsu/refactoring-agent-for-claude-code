package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAuditSetCollaborators {
    @JsonProperty("coOwners")
    private List<CollaboratorUser> coOwners;
    @JsonProperty("members")
    private List<CollaboratorUser> members;
    @JsonProperty("reviewers")
    private List<CollaboratorUser> reviewers;
    private List<String> grpIDs;
}
