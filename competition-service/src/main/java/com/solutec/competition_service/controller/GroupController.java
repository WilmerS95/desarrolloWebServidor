package com.solutec.competition_service.controller;

import com.solutec.competition_service.dto.GroupDTO;
import com.solutec.competition_service.service.GroupService;
import com.solutec.competition_service.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/competitions/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final TeamService teamService;

    @PostMapping("/tournaments/{tournamentId}")
    public ResponseEntity<GroupDTO> createGroup(@PathVariable Long tournamentId, @RequestBody GroupDTO dto) {
        return ResponseEntity.ok(groupService.createGroup(tournamentId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/tournaments/{tournamentId}")
    public ResponseEntity<List<GroupDTO>> getGroupsByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(groupService.getGroupsByTournament(tournamentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id, @RequestBody GroupDTO dto) {
        return ResponseEntity.ok(groupService.updateGroup(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    // Añadir equipo al grupo
    @PostMapping("/{groupId}/teams/{teamId}")
    public ResponseEntity<?> addTeamToGroup(@PathVariable Long groupId, @PathVariable Long teamId) {
        groupService.addTeamToGroup(groupId, teamId);
        return ResponseEntity.ok(Map.of("message", "Equipo agregado al grupo"));
    }

    // Remover equipo del grupo
    @DeleteMapping("/{groupId}/teams/{teamId}")
    public ResponseEntity<?> removeTeamFromGroup(@PathVariable Long groupId, @PathVariable Long teamId) {
        groupService.removeTeamFromGroup(groupId, teamId);
        return ResponseEntity.ok(Map.of("message", "Equipo removido del grupo"));
    }
}