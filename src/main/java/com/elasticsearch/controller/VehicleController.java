package com.elasticsearch.controller;

import com.elasticsearch.document.VehicleEs;
import com.elasticsearch.search.SearchRequestDTO;
import com.elasticsearch.service.VehicleService;
import com.elasticsearch.service.helper.VehicleDummyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/vehicleytb")
public class VehicleController {
    private final VehicleService service;
    private final VehicleDummyDataService dummyDataService;

    @Autowired
    public VehicleController(VehicleService service, VehicleDummyDataService dummyDataService) {
        this.service = service;
        this.dummyDataService = dummyDataService;
    }

    @PostMapping
    public void index(@RequestBody final VehicleEs vehicleEs) {
        service.index(vehicleEs);
    }

    @PostMapping("/insertdummydata")
    public void insertDummyData() {
        dummyDataService.insertDummyData();
    }

    @GetMapping
    public List<VehicleEs> findAll(){
        return service.getAll();
    }

    @GetMapping("/all2")
    public List<VehicleEs> findAll2(){
        return service.getAll2();
    }

    @GetMapping("/{id}")
    public VehicleEs getById(@PathVariable final String id) {
        return service.getById(id);
    }

    @PostMapping("/search")
    public List<VehicleEs> search(@RequestBody final SearchRequestDTO dto) {
        return service.search(dto);
    }

    @GetMapping("/search/{date}")
    public List<VehicleEs> getAllVehiclesCreatedSince(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return service.getAllVehiclesCreatedSince(date);
    }

    @PostMapping("/searchcreatedsince/{date}")
    public List<VehicleEs> searchCreatedSince(
            @RequestBody final SearchRequestDTO dto,
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return service.searchCreatedSince(dto, date);
    }
}
