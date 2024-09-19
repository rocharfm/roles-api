package com.ecore.roles.web.rest;

import com.ecore.roles.exception.RequiredParameterException;
import com.ecore.roles.model.Role;
import com.ecore.roles.service.RolesService;
import com.ecore.roles.web.RolesApi;
import com.ecore.roles.web.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ecore.roles.web.dto.RoleDto.fromModel;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/roles")
public class RolesRestController implements RolesApi {

    private final RolesService rolesService;

    @Override
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"})
    public ResponseEntity<RoleDto> createRole(
            @Valid @RequestBody RoleDto role) {
        return ResponseEntity
                .status(201)
                .body(fromModel(rolesService.CreateRole(role.toModel())));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseEntity<List<RoleDto>> getRoles(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID teamId) {
        List<RoleDto> roleDtoList = new ArrayList<>();

        if (userId == null && teamId == null) {
            List<Role> getRoles = rolesService.GetRoles();
            for (Role role : getRoles) {
                RoleDto roleDto = fromModel(role);
                roleDtoList.add(roleDto);
            }
        } else {
            if (userId == null) {
                throw new RequiredParameterException("userId");
            } else if (teamId == null) {
                throw new RequiredParameterException("teamId");
            } else {
                RoleDto roleDto = fromModel(rolesService.GetRole(userId, teamId));
                roleDtoList.add(roleDto);
            }
        }
        return ResponseEntity
                .status(200)
                .body(roleDtoList);
    }

    @Override
    @GetMapping(
            path = "/{roleId}",
            produces = {"application/json"})
    public ResponseEntity<RoleDto> getRole(
            @PathVariable UUID roleId) {
        return ResponseEntity
                .status(200)
                .body(fromModel(rolesService.GetRole(roleId)));
    }

}
