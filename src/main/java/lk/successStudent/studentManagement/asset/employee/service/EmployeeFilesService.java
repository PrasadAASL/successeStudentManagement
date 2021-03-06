package lk.successStudent.studentManagement.asset.employee.service;


import lk.successStudent.studentManagement.asset.commonAsset.model.FileInfo;
import lk.successStudent.studentManagement.asset.employee.controller.EmployeeController;
import lk.successStudent.studentManagement.asset.employee.dao.EmployeeFilesDao;
import lk.successStudent.studentManagement.asset.employee.entity.Employee;
import lk.successStudent.studentManagement.asset.employee.entity.EmployeeFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.stream.Collectors;
import java.util.*;

@Service
@CacheConfig( cacheNames = "employeeFiles" )
public class EmployeeFilesService {
    private final EmployeeFilesDao employeeFilesDao;

    @Autowired
    public EmployeeFilesService(EmployeeFilesDao employeeFilesDao) {
        this.employeeFilesDao = employeeFilesDao;
    }

    public EmployeeFiles findByName(String filename) {
        return employeeFilesDao.findByName(filename);
    }

    public void persist(EmployeeFiles storedFile) {
        employeeFilesDao.save(storedFile);
    }


    public List< EmployeeFiles > search(EmployeeFiles employeeFiles) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example< EmployeeFiles > employeeFilesExample = Example.of(employeeFiles, matcher);
        return employeeFilesDao.findAll(employeeFilesExample);
    }

    public EmployeeFiles findById(Integer id) {
        return employeeFilesDao.getOne(id);
    }

    public EmployeeFiles findByNewID(String filename) {
        return employeeFilesDao.findByNewId(filename);
    }

    @Cacheable
    public List<FileInfo> employeeFileDownloadLinks(Employee employee) {
        return employeeFilesDao.findByEmployeeOrderByIdDesc(employee)
                .stream()
                .map(employeeFiles -> {
                    String filename = employeeFiles.getName();
                    String url = MvcUriComponentsBuilder
                            .fromMethodName(EmployeeController.class, "downloadFile", employeeFiles.getNewId())
                            .build()
                            .toString();
                    return new FileInfo(filename, employeeFiles.getCreatedAt(), url);
                })
                .collect(Collectors.toList());
    }
}
