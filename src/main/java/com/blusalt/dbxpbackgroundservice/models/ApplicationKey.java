package com.blusalt.dbxpbackgroundservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "application_keys")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationKey extends AuditEntityWithUUID {

    @Column(unique = true)
    private String serviceName;
    private String secreteKey;
    private Date dateCreated;

}
