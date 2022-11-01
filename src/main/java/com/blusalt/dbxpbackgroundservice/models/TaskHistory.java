package com.blusalt.dbxpbackgroundservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "task_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskHistory {
    @Id
    @GeneratedValue(generator = "increment")

    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private Date timeStarted;
    private Date timeFinished;
    private String status;
    @Length(max = 30000)
    private String exceptionMessage;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taskId")
    BluTaskConfig taskId;
    @Length(max = 30000)
    private String taskParam;
    private Long duration;
    private String serviceName;
}
