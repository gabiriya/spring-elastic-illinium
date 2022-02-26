package com.elasticsearch.controller;

import com.elasticsearch.document.VehicleEs;
import com.elasticsearch.search.SearchRequestDTO;
import com.elasticsearch.service.PersonService;
import com.elasticsearch.document.PersonEs;
import com.elasticsearch.service.helper.PersonDummyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personytb")
public class PersonController {
    private final PersonService service;
    private final PersonDummyDataService insertDummyData;

    @Autowired
    public PersonController(PersonService service, PersonDummyDataService insertDummyData) {
        this.service = service;
        this.insertDummyData = insertDummyData;
    }

    @PostMapping
    public ResponseEntity<String> save(@RequestBody final PersonEs personEs) {
        if (service.index(personEs)){
            return new ResponseEntity<>("Saved !!",HttpStatus.CREATED) ;
        }else {
            return new ResponseEntity<>("Error",HttpStatus.FAILED_DEPENDENCY) ;
        }
    }

    @GetMapping("/{id}")
    public PersonEs findById(@PathVariable final String id) {
        return service.getById(id);
    }


    @PostMapping("/insertdummydata")
    public void insertDummyData() {
        insertDummyData.insertDummyData();
    }

    @GetMapping
    public List<PersonEs> findAll(){
        return service.getAll();
    }

    @PostMapping("/search")
    public List<PersonEs> search(@RequestBody final SearchRequestDTO dto) {
        return service.search(dto);
    }

}
