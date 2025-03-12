package org.onelab.restaurant_service.repository;

import org.onelab.restaurant_service.entity.MenuDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MenuElasticRepository extends ElasticsearchRepository<MenuDocument, String> {
    @Query("""
        {
          "bool": {
            "should": [
              {
                "multi_match": {
                  "query": "?0",
                  "fields": [ "name^3", "dishes.name^2" ],
                  "fuzziness": "AUTO",
                  "type": "best_fields"
                }
              }
            ]
          }
        }
        """)
    Page<MenuDocument> searchByNameOrDishName(String query, Pageable pageable);
}
