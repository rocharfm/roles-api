package com.ecore.roles.service;

import com.ecore.roles.client.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamsService {

    Optional<Team> getTeam(UUID id);

    List<Team> getTeams();

    boolean verifyThatUserBelongsToTeam(UUID userId, UUID teamId);
}
