package com.elasticsearch.repository;

import com.elasticsearch.document.PersonEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonRepository extends ElasticsearchRepository<PersonEs, String> {
}
