package vn.hoidanit.jobhunter.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company){
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id){
        this.companyRepository.deleteById(id);
    }

    public Company fetchCompanyById(long id){
        return this.companyRepository.findById(id).isPresent()? this.companyRepository.findById(id).get(): null;
    }

    public List<Company> fetchAllCompany(){
        return this.companyRepository.findAll();
    }

    public Company handleUpdateCompany(Company company){
        Optional<Company> companyOptional= this.companyRepository.findById(company.getId());
        if(companyOptional.isPresent()){
            Company currentCompany = companyOptional.get();
            currentCompany.setName(company.getName());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

}
