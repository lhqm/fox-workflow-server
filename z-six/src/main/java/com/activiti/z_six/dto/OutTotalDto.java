package com.activiti.z_six.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class OutTotalDto {

    private Long total;

    private List<Map<String, Object>> list;
}
