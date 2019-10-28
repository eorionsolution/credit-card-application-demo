package com.eorionsolution.demo.bpms.creditcardapplicationdemo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VariableDefinition {
    private int value;
    private String type = "Long";

    public VariableDefinition(int value) {
        this.value = value;
    }
}
