package com.eorionsolution.demo.bpms.creditcardapplicationdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CamundaVariable {
    private Map<String, VariableDefinition> variables = new HashMap<>();
    private String businessKey = "demo";
}
