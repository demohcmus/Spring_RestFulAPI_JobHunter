package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission update(Permission permission) {
        Permission currPermission = this.permissionRepository.findById(permission.getId()).orElse(null);
        if (currPermission != null) {
            currPermission.setName(permission.getName());
            currPermission.setApiPath(permission.getApiPath());
            currPermission.setMethod(permission.getMethod());
            currPermission.setModule(permission.getModule());

            // update
            return this.permissionRepository.save(permission);
        }
        return null;

    }

    public Permission fetchPermissionById(Long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByNameAndApiPathAndMethodAndModule(permission.getName(),
                permission.getApiPath(), permission.getMethod(), permission.getModule());
    }

    public ResultPaginationDTO fetchAll(Specification spec, Pageable pageable) {
        Page<Permission> permissionPage = this.permissionRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(permissionPage.getTotalPages());
        meta.setTotal(permissionPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(permissionPage.getContent());

        return resultPaginationDTO;
    }

    public Permission fetchById(long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    
    public void delete(long id) {
        // delete role_permission
        Optional<Permission> permissionqOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionqOptional.get();
        currentPermission.getRoles().forEach(r -> r.getPermissions().remove(currentPermission));

        this.permissionRepository.delete(currentPermission);
    }

    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchById(p.getId());
        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName()))
                return true;
        }
        return false;
    }


}
