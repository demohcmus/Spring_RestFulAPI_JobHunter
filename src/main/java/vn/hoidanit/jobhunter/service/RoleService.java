package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isNameExist(String name) {
        return roleRepository.existsByName(name);
    }

    public Role create(Role role){

        // check list permission
        if(role.getPermissions()!=null){
            List<Long> reqPermissions= role.getPermissions()
            .stream().map(x->x.getId())
            .collect(Collectors.toList());

            List<Permission> dbPermissions= this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        
        return this.roleRepository.save(role);
    }

    public Optional<Role> fetchRoleById(Long id){
        return this.roleRepository.findById(id);
    }

    public Role update(Role role){
        // check list permission
        if(role.getPermissions()!=null){
            List<Long> reqPermissions= role.getPermissions()
            .stream().map(x->x.getId())
            .collect(Collectors.toList());

            List<Permission> dbPermissions= this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }
        
        return this.roleRepository.save(role);
    }

    public void delete(long id){
        this.roleRepository.deleteById(id);
    }

public ResultPaginationDTO fetchAll(Specification spec, Pageable pageable){
    Page<Role> rolePage = this.roleRepository.findAll(spec, pageable);

    ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
    ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

    meta.setPage(pageable.getPageNumber()+1);
    meta.setPageSize(pageable.getPageSize());

    meta.setPages(rolePage.getTotalPages());
    meta.setTotal(rolePage.getTotalElements());

    resultPaginationDTO.setMeta(meta);
    resultPaginationDTO.setResult(rolePage.getContent());

    return resultPaginationDTO;
}
    
}
