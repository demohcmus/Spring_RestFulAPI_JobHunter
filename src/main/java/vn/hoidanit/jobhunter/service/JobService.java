package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository,
    SkillRepository skillRepository){ 
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO create(Job job) {
        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // create job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(currentJob.getId());
        resCreateJobDTO.setName(currentJob.getName());
        resCreateJobDTO.setSalary(currentJob.getSalary());
        resCreateJobDTO.setQuantity(currentJob.getQuantity());
        resCreateJobDTO.setLocation(currentJob.getLocation());
        resCreateJobDTO.setLevel(currentJob.getLevel());
        resCreateJobDTO.setStartDate(currentJob.getStartDate());
        resCreateJobDTO.setEndDate(currentJob.getEndDate());
        resCreateJobDTO.setActive(currentJob.isActive());
        resCreateJobDTO.setCreatedAt(currentJob.getCreatedAt());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            resCreateJobDTO.setSkills(skills);
        }

        return resCreateJobDTO;

    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO update(Job job) {
        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // update job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(currentJob.getId());
        resUpdateJobDTO.setName(currentJob.getName());
        resUpdateJobDTO.setSalary(currentJob.getSalary());
        resUpdateJobDTO.setQuantity(currentJob.getQuantity());
        resUpdateJobDTO.setLocation(currentJob.getLocation());
        resUpdateJobDTO.setLevel(currentJob.getLevel());
        resUpdateJobDTO.setStartDate(currentJob.getStartDate());
        resUpdateJobDTO.setEndDate(currentJob.getEndDate());
        resUpdateJobDTO.setActive(currentJob.isActive());
        resUpdateJobDTO.setUpdatedAt(currentJob.getUpdatedAt());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            resUpdateJobDTO.setSkills(skills);
        }

        return resUpdateJobDTO;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification spec, Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
       
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(jobPage.getContent());

        return resultPaginationDTO;
    }
}
