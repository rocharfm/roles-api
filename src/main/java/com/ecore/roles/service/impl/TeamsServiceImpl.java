package com.ecore.roles.service.impl;

import com.ecore.roles.client.TeamsClient;
import com.ecore.roles.client.model.Team;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.service.TeamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamsServiceImpl implements TeamsService {

    private final TeamsClient teamsClient;

    @Autowired
    public TeamsServiceImpl(TeamsClient teamsClient) {
        this.teamsClient = teamsClient;
    }

    public Optional<Team> getTeam(UUID id) {
        return Optional.ofNullable(teamsClient.getTeam(id).getBody());
    }

    public List<Team> getTeams() {
        return teamsClient.getTeams().getBody();
    }

    public boolean verifyThatUserBelongsToTeam(UUID userId, UUID teamId) {
        Optional<Team> team = getTeam(teamId);
        if (team.isPresent()) {
            return team.get().getTeamMemberIds().stream().anyMatch(i -> i.equals(userId));
        }
        throw new ResourceNotFoundException(Team.class, teamId);
    }
}
