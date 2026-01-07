package com.david.f1_manager.test;

import com.david.f1_manager.Driver;

/**
 * Simple test class for Driver
 * Tests encapsulation, exception handling, and data validation
 */
public class DriverTest {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=== Driver Test Suite ===\n");

        testValidDriverCreation();
        testSkillBoundsEnforcement();
        testGettersAndSetters();
        testExceptionHandling();

        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        System.out.println("Total:  " + (passedTests + failedTests));

        if (failedTests == 0) {
            System.out.println("\nAll tests passed!");
        } else {
            System.out.println("\nSome tests failed!");
        }
    }

    private static void testValidDriverCreation() {
        System.out.print("Test: Valid Driver Creation... ");
        try {
            Driver driver = new Driver("D1", "Lewis Hamilton", 95);

            if (driver.getId().equals("D1") &&
                driver.getName().equals("Lewis Hamilton") &&
                driver.getSkill() == 95) {
                pass();
            } else {
                fail("Driver properties don't match expected values");
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void testSkillBoundsEnforcement() {
        System.out.print("Test: Skill Bounds Enforcement... ");
        try {
            // Test upper bound clamping
            Driver driver1 = new Driver("D2", "Test Driver", 150);
            if (driver1.getSkill() != 100) {
                fail("Expected skill to be clamped to 100, got: " + driver1.getSkill());
                return;
            }

            // Test lower bound clamping
            Driver driver2 = new Driver("D3", "Test Driver", -10);
            if (driver2.getSkill() != 1) {
                fail("Expected skill to be clamped to 1, got: " + driver2.getSkill());
                return;
            }

            pass();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void testGettersAndSetters() {
        System.out.print("Test: Getters and Setters... ");
        try {
            Driver driver = new Driver("D4", "Max Verstappen", 98);

            // Test setters
            driver.setId("D5");
            driver.setName("Sergio Perez");
            driver.setSkill(92);

            // Test getters
            if (!driver.getId().equals("D5") ||
                !driver.getName().equals("Sergio Perez") ||
                driver.getSkill() != 92) {
                fail("Getters/Setters not working correctly");
                return;
            }

            pass();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void testExceptionHandling() {
        System.out.print("Test: Exception Handling (try-catch-finally)... ");
        try {
            Driver driver = new Driver("D6", "Test Driver", 50);

            // This should trigger the exception handling in setSkill
            // The value should be clamped to 1
            driver.setSkill(-50);

            if (driver.getSkill() != 1) {
                fail("Expected skill to be clamped to 1 after exception, got: " + driver.getSkill());
                return;
            }

            // This should trigger exception handling and clamp to 100
            driver.setSkill(200);

            if (driver.getSkill() != 100) {
                fail("Expected skill to be clamped to 100 after exception, got: " + driver.getSkill());
                return;
            }

            pass();
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void pass() {
        System.out.println("PASSED");
        passedTests++;
    }

    private static void fail(String reason) {
        System.out.println("FAILED - " + reason);
        failedTests++;
    }
}
