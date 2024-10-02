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

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;
    
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage ("Create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) 
    throws IdInvalidException {
        // check name
        if(skill.getName()!=null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("Skill name = " + skill.getName()+" đã tồn tại!");
        }
        Skill newSkill = this.skillService.createSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill skill)
    throws IdInvalidException {
        // check id
        Skill updateSkill = this.skillService.fetchSkillById(skill.getId());
        if(updateSkill == null) {
            throw new IdInvalidException("Skill id = " + skill.getId() + " không tồn tại!");
        }

        // check name
        if(skill.getName()!=null && this.skillService.isNameExist(skill.getName())) {
            throw new IdInvalidException("Skill name = " + skill.getName()+" đã tồn tại!");
        }
        updateSkill.setName(skill.getName());

        return ResponseEntity.ok(this.skillService.handleUpdateSkill(updateSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(
        @Filter Specification<Skill> spec,
        Pageable pageable) {
        return ResponseEntity.ok(this.skillService.fetchAllSkill(spec, pageable));
    }

    @DeleteMapping ("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id)
    throws IdInvalidException {
        
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if(currentSkill == null) {
            throw new IdInvalidException("Skill id = " + id + " không tồn tại!");
        }

        this.skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
