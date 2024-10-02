package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import vn.hoidanit.jobhunter.domain.Role;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        // check role
        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId()).get();
            user.setRole(role != null ? role : null);
        }

        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(meta);

        // remove sentitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream()
                .map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(listUser);
        return resultPaginationDTO;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setName(reqUser.getName());
            currentUser.setGender(reqUser.getGender());
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setAge(reqUser.getAge());

            // check company
            if (reqUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            }

            // check role
            if (reqUser.getRole() != null) {
                Role role = this.roleService.fetchRoleById(reqUser.getRole().getId()).get();
                currentUser.setRole(role != null ? role : null);
            }

            // update user
            currentUser = userRepository.save(currentUser);
            return null;
        }

        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String username) {
        return this.userRepository.existsByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        if (user.getCompany() != null) {
            ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser(
                    user.getCompany().getId(),
                    user.getCompany().getName());
            res.setCompany(company);
        }

        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }
        
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            ResUpdateUserDTO.CompanyUser company = new ResUpdateUserDTO.CompanyUser(
                    user.getCompany().getId(),
                    user.getCompany().getName());
            res.setCompany(company);
        }

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
