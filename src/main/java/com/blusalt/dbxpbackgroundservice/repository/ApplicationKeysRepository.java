package com.blusalt.dbxpbackgroundservice.repository;

import com.blusalt.dbxpbackgroundservice.models.ApplicationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationKeysRepository extends JpaRepository<ApplicationKey, Long> {
    Optional<ApplicationKey> findApplicationKeysByServiceName(String appName);

    ApplicationKey findByServiceName(String appName);
}

