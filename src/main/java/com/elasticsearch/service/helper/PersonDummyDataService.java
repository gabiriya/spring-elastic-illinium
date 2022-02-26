package com.elasticsearch.service.helper;

import com.elasticsearch.document.PersonEs;
import com.elasticsearch.document.VehicleEs;
import com.elasticsearch.service.PersonService;
import com.elasticsearch.service.VehicleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PersonDummyDataService {

    private final PersonService personService;
    private final VehicleService vehicleService;

    public PersonDummyDataService(final PersonService personService, VehicleService vehicleService) {
        this.personService = personService;
        this.vehicleService = vehicleService;
    }

    public void insertDummyData() {
        // index : method to save an index

        List<VehicleEs> list1 = new ArrayList<>();
        list1.add(vehicleService.getById("1"));
        list1.add(vehicleService.getById("2"));
        list1.add(vehicleService.getById("3"));

        personService.index(buildPerson("10", "Amin",list1));
        personService.index(buildPerson("20", "Anas",list1));
        personService.index(buildPerson("30", "Youns",list1));

        List<VehicleEs> list2 = new ArrayList<>();
        list2.add(vehicleService.getById("4"));
        list2.add(vehicleService.getById("5"));
        list2.add(vehicleService.getById("6"));

        personService.index(buildPerson("40", "Jawad",list2));
        personService.index(buildPerson("50", "Ihssan",list2));
        personService.index(buildPerson("60", "Rachid",list2));

        personService.index(buildPerson("70", "Salwa",list1));
        personService.index(buildPerson("80", "Salma",list2));

        personService.index(buildPerson("90", "Ikram",list1));
        personService.index(buildPerson("100", "Mohsin",list2));
    }

    private static PersonEs buildPerson(final String id,
                                        final String name,
                                        final List<VehicleEs> cars) {
        PersonEs person = new PersonEs();
        person.setId(id);
        person.setName(name);
        person.setVehicles(cars);
        return person;
    }
}
