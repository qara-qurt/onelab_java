package org.onelab.user_service.repository;

import org.onelab.user_service.entity.UserDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserElasticRepository extends ElasticsearchRepository<UserDocument, String> {
    @Query("""
        {
          "bool": {
            "should": [
              {
                "multi_match": {
                  "query": "?0",
                  "fields": [ "name^3", "surname^2", "username" ],
                  "fuzziness": "AUTO",
                  "type": "best_fields"
                }
              }
            ]
          }
        }
        """)
    Page<UserDocument> searchByFields(String name, Pageable pageable);

    @Query("""
        {
          "range": {
            "balance": {
              "gte": "?0",
              "lte": "?1"
            }
          }
        }
        """)
    Page<UserDocument> findByBalanceBetween(Double minBalance, Double maxBalance, Pageable pageable);

    @Query("""
        {
          "range": {
            "birth_date": {
              "gte": "?0",
              "lte": "?1",
              "format": "yyyy-MM-dd"
            }
          }
        }
        """)
    Page<UserDocument> findByBirthDateBetween(String startDate, String endDate, Pageable pageable);
}
