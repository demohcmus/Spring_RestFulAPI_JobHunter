package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        // check permission
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission đã tồn tại!");
        }

        Permission newPermission = this.permissionService.create(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPermission);
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission)
            throws IdInvalidException {

        // check valid
        Permission currPermission = this.permissionService.fetchPermissionById(permission.getId());
        if (currPermission == null) {
            throw new IdInvalidException("Permission id = " + permission.getId() + " không tồn tại!");
        }

        if (this.permissionService.isPermissionExist(permission)) {
            // check name
            if (this.permissionService.isSameName(permission)) {
                throw new IdInvalidException("Permission đã tồn tại.");
            }

        }
        

        return ResponseEntity.ok(this.permissionService.update(currPermission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id)
            throws IdInvalidException {
        // check id
        Permission currentPermission = this.permissionService.fetchPermissionById(id);
        if (currentPermission == null) {
            throw new IdInvalidException("Permission id = " + id + " không tồn tại!");
        }

        this.permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permissions")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.fetchAll(spec, pageable));
    }

}
