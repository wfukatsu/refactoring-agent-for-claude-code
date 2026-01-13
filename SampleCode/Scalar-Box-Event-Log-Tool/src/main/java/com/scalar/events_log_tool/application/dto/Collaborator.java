package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Collaborator {
    private CollaboratorUser ownedBy;
    @JsonProperty("coOwners")
    private List<CollaboratorUser> coOwners;
    @JsonProperty("members")
    private List<CollaboratorUser> members;
    @JsonProperty("reviewers")
    private List<CollaboratorUser> reviewers;

}
