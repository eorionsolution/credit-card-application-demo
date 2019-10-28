package com.eorionsolution.demo.bpms.creditcardapplicationdemo.processes.dmn;

import com.eorionsolution.demo.bpms.creditcardapplicationdemo.processes.BaseProcessEngineTestCase;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import static org.junit.Assert.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

@EnableRuleMigrationSupport
@Deployment(resources = {"dmn/green-red-classification.dmn"})
public class GreenRedClassificationTest extends BaseProcessEngineTestCase {
    @Test
    public void dummy() {
        Assert.assertTrue(true);
    }

    @Test
    public void shouldGetRedWhenYoungerThan18() {
        var decisionResult = decisionService().evaluateDecisionTableByKey("green-red-classification",
                Variables.createVariables().putValue("age", 17).putValue("limitation", 1));
        assertEquals(1, decisionResult.getResultList().size());
        assertEquals("red", decisionResult.getSingleResult().getEntry("resultClass"));
    }

    @Test
    public void shouldGetGreenWhenAgeBetween18And60AndLimitationLessThan5000() {
        var decisionResult = decisionService().evaluateDecisionTableByKey("green-red-classification",
                Variables.createVariables().putValue("age", 18).putValue("limitation", 5000));
        assertEquals(1, decisionResult.getResultList().size());
        assertEquals("green", decisionResult.getSingleResult().getEntry("resultClass"));
    }

    @Test
    public void shouldGetYellowWhenAgeBetween18And60AndLimitationMoreThan5000() {
        var decisionResult = decisionService().evaluateDecisionTableByKey("green-red-classification",
                Variables.createVariables().putValue("age", 60).putValue("limitation", 5001));
        assertEquals(1, decisionResult.getResultList().size());
        assertEquals("yellow", decisionResult.getSingleResult().getEntry("resultClass"));
    }

    @Test
    public void shouldGetYellowWhenElderThan60() {
        var decisionResult = decisionService().evaluateDecisionTableByKey("green-red-classification",
                Variables.createVariables().putValue("age", 61).putValue("limitation", 1));
        assertEquals(1, decisionResult.getResultList().size());
        assertEquals("yellow", decisionResult.getSingleResult().getEntry("resultClass"));
    }

    @Test
    public void shouldGetYellowWhenLimitationMoreThan20000() {
        var decisionResult = decisionService().evaluateDecisionTableByKey("green-red-classification",
                Variables.createVariables().putValue("age", 59).putValue("limitation", 20001));
        assertEquals(2, decisionResult.getResultList().size());
        assertEquals("yellow", decisionResult.getFirstResult().getEntry("resultClass"));
    }
}
