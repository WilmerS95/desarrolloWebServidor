package com.solutec.competition_service.service;

import com.solutec.competition_service.dto.GroupDTO;
import com.solutec.competition_service.entity.Group;
import com.solutec.competition_service.entity.Team;
import com.solutec.competition_service.entity.Tournament;
import com.solutec.competition_service.repository.GroupRepository;
import com.solutec.competition_service.repository.TeamRepository;
import com.solutec.competition_service.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository; // <-- inyectado

    public GroupDTO createGroup(Long tournamentId, GroupDTO dto) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        Group group = new Group();
        group.setTournament(tournament);
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setTeams(new HashSet<>());

        Group saved = groupRepository.save(group);
        return mapToDTO(saved);
    }

    public GroupDTO getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return mapToDTO(group);
    }

    public List<GroupDTO> getGroupsByTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        return groupRepository.findByTournament(tournament)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public GroupDTO updateGroup(Long id, GroupDTO dto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        group.setName(dto.getName());
        group.setDescription(dto.getDescription());

        Group updated = groupRepository.save(group);
        return mapToDTO(updated);
    }

    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new RuntimeException("Grupo no encontrado");
        }
        groupRepository.deleteById(id);
    }

    public void addTeamToGroup(Long groupId, Long teamId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // inicializar si es null
        if (group.getTeams() == null) group.setTeams(new HashSet<>());
        if (team.getGroups() == null) team.setGroups(new HashSet<>());

        group.getTeams().add(team);
        team.getGroups().add(group);
        groupRepository.save(group);
        teamRepository.save(team);
    }

    public void removeTeamFromGroup(Long groupId, Long teamId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        if (group.getTeams() != null) {
            group.getTeams().removeIf(t -> t.getTeamID().equals(teamId));
        }
        if (team.getGroups() != null) {
            team.getGroups().removeIf(g -> g.getGroupID().equals(groupId));
        }

        groupRepository.save(group);
        teamRepository.save(team);
    }

    private GroupDTO mapToDTO(Group group) {
        GroupDTO dto = new GroupDTO();
        dto.setGroupID(group.getGroupID());
        dto.setTournamentID(group.getTournament().getTournamentID());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());

        // incluir IDs de equipos para el frontend
        Set<Long> teamIds = null;
        if (group.getTeams() != null) {
            teamIds = group.getTeams().stream().map(Team::getTeamID).collect(Collectors.toSet());
        }
        dto.setTeamIDs(teamIds);

        return dto;
    }
}