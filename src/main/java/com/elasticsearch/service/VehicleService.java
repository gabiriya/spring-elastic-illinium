package com.elasticsearch.service;

import com.elasticsearch.document.VehicleEs;
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
import java.util.Date;
import java.util.List;

@Service
public class VehicleService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

    private final RestHighLevelClient client;

    @Autowired
    public VehicleService(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * Used to search for vehicles based on data provided in the {@link SearchRequestDTO} DTO. For more info take a look
     * at DTO javadoc.
     *
     * @param dto DTO containing info about what to search for.
     * @return Returns a list of found vehicles.
     */
    public List<VehicleEs> search(final SearchRequestDTO dto) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                dto
        );

        return searchInternal(request);
    }

    /**
     * Used to get all vehicles that have been created since forwarded date.
     *
     * @param date Date that is forwarded to the search.
     * @return Returns all vehicles created since forwarded date.
     */
    public List<VehicleEs> getAllVehiclesCreatedSince(final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                "created",
                date
        );

        return searchInternal(request);
    }

    public List<VehicleEs> searchCreatedSince(final SearchRequestDTO dto, final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                dto,
                date
        );

        return searchInternal(request);
    }

    private List<VehicleEs> searchInternal(final SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<VehicleEs> vehicleEs = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                vehicleEs.add(
                        MAPPER.readValue(hit.getSourceAsString(), VehicleEs.class)
                );
            }

            return vehicleEs;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }
//   save vehicule
    public Boolean index(final VehicleEs vehicleEs) {
        try {
            final String vehicleAsString = MAPPER.writeValueAsString(vehicleEs);

            final IndexRequest request = new IndexRequest(Indices.VEHICLE_INDEX);
            request.id(vehicleEs.getId());
            request.source(vehicleAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public VehicleEs getById(final String vehicleId) {
        try {
            // documentFields = response
            final GetResponse documentFields = client.get(
                    // get request (index , indexId)
                    new GetRequest(Indices.VEHICLE_INDEX, vehicleId),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), VehicleEs.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<VehicleEs> getAll(){
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX,
                "created",
                null
        );

        return searchInternal(request);
    }

    public List<VehicleEs> getAll2(){
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.VEHICLE_INDEX
        );

        return searchInternal(request);
    }
}
