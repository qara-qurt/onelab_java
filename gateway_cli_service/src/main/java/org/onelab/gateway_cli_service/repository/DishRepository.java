package org.onelab.gateway_cli_service.repository;

import org.onelab.gateway_cli_service.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DishRepository extends ElasticsearchRepository<Dish, String> {
    Optional<Dish> findByName(String name);

    @Query("""
        {
          "bool": {
            "should": [
              { "match": { "name": "?0" }}, 
              { "match": { "description": "?0" }},
              { "match": { "meaning": "?0" }}, 
              { "match_phrase_prefix": { "name": "?0" }}, 
              { "match_phrase_prefix": { "description": "?0" }}, 
              { "match_phrase_prefix": { "meaning": "?0" }}, 
              { "wildcard": { "name": { "value": "*?0*", "boost": 2 }}}, 
              { "wildcard": { "description": { "value": "*?0*", "boost": 2 }}}, 
              { "wildcard": { "meaning": { "value": "*?0*", "boost": 2 }}}, 
              { "fuzzy": { "name": { "value": "?0", "fuzziness": "AUTO" }}}, 
              { "fuzzy": { "description": { "value": "?0", "fuzziness": "AUTO" }}}, 
              { "fuzzy": { "meaning": { "value": "?0", "fuzziness": "AUTO" }}}
            ]
          }
        }
        """)
    Page<Dish> searchByFields(String name, Pageable pageable);


    @Query("""
        {
          "match_all": {}
        }
        """)
    Page<Dish> findAllDishes(Pageable pageable);

}
