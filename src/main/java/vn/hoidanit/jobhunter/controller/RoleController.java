package vn.hoidanit.jobhunter.controller;

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

import io.micrometer.core.instrument.Meter.Id;
import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role)
            throws IdInvalidException {

        // check name
        if (role.getName() != null && this.roleService.isNameExist(role.getName())) {
            throw new IdInvalidException("Role name = " + role.getName() + " đã tồn tại!");
        }

        Role newRole = this.roleService.create(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
    }


    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role)
            throws IdInvalidException {
        // check id
        Role currentRole = this.roleService.fetchRoleById(role.getId()).isPresent()
                ? this.roleService.fetchRoleById(role.getId()).get()
                : null;

        if (currentRole == null) {
            throw new IdInvalidException("Role id = " + role.getId() + " không tồn tại!");
        }
        // check name
        if (role.getName() != null && this.roleService.isNameExist(role.getName())) {
            throw new IdInvalidException("Role name = " + role.getName() + " đã tồn tại!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id)
    throws IdInvalidException{

        // check id
        Role role = this.roleService.fetchRoleById(id).isPresent()
                ? this.roleService.fetchRoleById(id).get()
                : null;
                if(role == null){
                    throw new IdInvalidException("Role id = " + id + " không tồn tại!");
                }
                this.roleService.delete(id);
                return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO> getAll(
        @Filter Specification<Role> spec,
        Pageable pageable){
return ResponseEntity.ok(this.roleService.fetchAll(spec, pageable));
        }
    
}
