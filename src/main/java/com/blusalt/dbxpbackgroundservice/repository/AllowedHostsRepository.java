package com.blusalt.dbxpbackgroundservice.repository;

import com.blusalt.dbxpbackgroundservice.models.AllowedHosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowedHostsRepository extends JpaRepository<AllowedHosts, Long> {

}
