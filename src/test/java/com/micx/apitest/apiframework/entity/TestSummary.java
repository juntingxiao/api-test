package com.micx.apitest.apiframework.entity;

import lombok.Data;

@Data
public class TestSummary {
    private Integer success;
    private Integer fail;
    private Integer total;

}
