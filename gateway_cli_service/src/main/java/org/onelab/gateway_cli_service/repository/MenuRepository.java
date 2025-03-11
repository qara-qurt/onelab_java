package org.onelab.gateway_cli_service.repository;

import org.onelab.gateway_cli_service.entity.Menu;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface MenuRepository extends ElasticsearchRepository<Menu,String> {
    Optional<Menu> findByName(String name);
}
