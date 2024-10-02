package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

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
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;
    
    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job)
    throws IdInvalidException {
        Optional<Job> currentJob= this.jobService.fetchJobById(job.getId());
        if(!currentJob.isPresent()){
            throw new IdInvalidException("Job id = " + job.getId() + " không tồn tại!");
        }

        return ResponseEntity.ok(this.jobService.update(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> delete(@PathVariable long id)
    throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if(!currentJob.isPresent()){
            throw new IdInvalidException("Job id = " + id + " không tồn tại!");
        }

        this.jobService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Fetch a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) 
    throws IdInvalidException{
        Optional<Job> currentJob= this.jobService.fetchJobById(id);
        if(!currentJob.isPresent()){
            throw new IdInvalidException("Job id = " + id + " không tồn tại!");
        }
        return ResponseEntity.ok(currentJob.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
        @Filter Specification<Job> spec,
        Pageable pageable){
            return ResponseEntity.ok(this.jobService.fetchAll(spec, pageable));
        }
    

}
