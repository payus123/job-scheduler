package com.blusalt.dbxpbackgroundservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_config")
public class BluTaskConfig extends AuditEntityWithUUID {

    @Id
    @GeneratedValue(generator = "increment")

    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private String taskType;
    private String status;
    @Length(max = 30000)
    private String taskParams;
       private String taskCron;
       private Date lastUpdate;
       private String frequency;
       private String serviceName;
  
}
