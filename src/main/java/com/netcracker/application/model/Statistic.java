package com.netcracker.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {
    private String taskName;

    private String date;

    private String activeTime;

    private Double activePercent;
}
