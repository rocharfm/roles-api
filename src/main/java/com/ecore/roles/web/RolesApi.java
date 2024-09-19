package com.ecore.roles.web;

import com.ecore.roles.web.dto.RoleDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface RolesApi {

    ResponseEntity<RoleDto> createRole(
            RoleDto role);

    ResponseEntity<List<RoleDto>> getAllRoles();

    ResponseEntity<RoleDto> searchRole(
            UUID userId,
            UUID teamId);

    ResponseEntity<RoleDto> getRole(
            UUID roleId);

}
