package org.onelab.gateway_cli_service.repository;

import org.onelab.gateway_cli_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends ElasticsearchRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);

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
    Page<User> searchByFields(String name, Pageable pageable);


    @Query("""
        {
          "match_all": {}
        }
        """)
    Page<User> findAllUsers(Pageable pageable);

}