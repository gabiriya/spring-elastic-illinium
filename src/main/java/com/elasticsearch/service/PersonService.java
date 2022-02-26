package com.elasticsearch.service;

import com.elasticsearch.document.PersonEs;
import com.elasticsearch.helper.Indices;
import com.elasticsearch.search.SearchRequestDTO;
import com.elasticsearch.search.util.SearchUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PersonService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);

    private final RestHighLevelClient client;

    @Autowired
    public PersonService(RestHighLevelClient client) {
        this.client = client;
    }

    public List<PersonEs> search(final SearchRequestDTO dto) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.PERSON_INDEX,
                dto
        );

        return searchInternal(request);
    }


//    public List<PersonEs> getAllVehiclesCreatedSince(final Date date) {
//        final SearchRequest request = SearchUtil.buildSearchRequest(
//                Indices.PERSON_INDEX,
//                "created",
//                date
//        );
//
//        return searchInternal(request);
//    }

//    public List<PersonEs> searchCreatedSince(final SearchRequestDTO dto, final Date date) {
//        final SearchRequest request = SearchUtil.buildSearchRequest(
//                Indices.PERSON_INDEX,
//                dto,
//                date
//        );
//
//        return searchInternal(request);
//    }

    private List<PersonEs> searchInternal(final SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<PersonEs> personEs = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                personEs.add(
                        MAPPER.readValue(hit.getSourceAsString(), PersonEs.class)
                );
            }

            return personEs;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    //   save Person
    public Boolean index(final PersonEs person) {
        try {
            final String personAsString = MAPPER.writeValueAsString(person);

            final IndexRequest request = new IndexRequest(Indices.PERSON_INDEX);
            request.id(person.getId());
            request.source(personAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public PersonEs getById(final String personId) {
        try {
            // documentFields = response
            final GetResponse documentFields = client.get(
                    // get request (index , indexId)
                    new GetRequest(Indices.PERSON_INDEX, personId),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), PersonEs.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<PersonEs> getAll(){
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.PERSON_INDEX
        );

        return searchInternal(request);
    }
}
