package com.google.sps;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class RunTests {
    public static void main (String[] args) {
        JUnitCore core = new JUnitCore();
        Result result = core.run(QueryTest.class);
        System.out.printf("Test ran: %s, Failed: %s%n", result.getRunCount(), result.getFailureCount());
    }
}