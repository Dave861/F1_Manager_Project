package com.david.f1_manager.test;

import com.david.f1_manager.Performable;
import com.david.f1_manager.car.*;

/**
 * Simple test class for Car
 * Tests polymorphism, interface implementation, and performance calculation
 */
public class CarTest {

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=== Car Test Suite ===\n");

        testPerformanceCalculationWithAllParts();
        testPerformanceCalculationWithNullParts();
        testPolymorphicBehavior();
        testPerformableInterface();

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

    private static void testPerformanceCalculationWithAllParts() {
        System.out.print("Test: Performance Calculation with All Parts... ");
        try {
            Car car = new Car("C1", "Test Car");

            // Create parts with 100 performance
            Engine engine = new Engine("E1", "V6 Turbo", 100);
            Aerodynamics aero = new Aerodynamics("A1", "Front Wing", 100);
            Tires tires = new Tires("T1", "Soft Compound", 100, Tires.TireCompound.SOFT);
            Suspension suspension = new Suspension("S1", "Advanced Suspension", 100);
            Gearbox gearbox = new Gearbox("G1", "8-Speed", 100);

            car.setEngine(engine);
            car.setAerodynamics(aero);
            car.setTires(tires);
            car.setSuspension(suspension);
            car.setGearbox(gearbox);

            // Expected: 100 * (0.35 + 0.25 + 0.20 + 0.10 + 0.10) = 100.0
            double performance = car.calculateOverallPerformance();
            double expected = 100.0;

            if (Math.abs(performance - expected) < 0.01) {
                pass();
            } else {
                fail("Expected performance: " + expected + ", got: " + performance);
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void testPerformanceCalculationWithNullParts() {
        System.out.print("Test: Performance Calculation with Null Parts... ");
        try {
            Car car = new Car("C2", "Incomplete Car");

            // Only add engine (35% weight)
            Engine engine = new Engine("E2", "V6 Turbo", 80);
            car.setEngine(engine);

            // Expected: 80 * 0.35 = 28.0
            double performance = car.calculateOverallPerformance();
            double expected = 28.0;

            if (Math.abs(performance - expected) < 0.01) {
                pass();
            } else {
                fail("Expected performance: " + expected + ", got: " + performance);
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void testPolymorphicBehavior() {
        System.out.print("Test: Polymorphic Behavior (getPartTypeMultiplier)... ");
        try {
            // Create different types of CarPart objects
            CarPart engine = new Engine("E3", "Test", 100);
            CarPart aero = new Aerodynamics("A3", "Test", 100);
            CarPart tires = new Tires("T3", "Test", 100, Tires.TireCompound.MEDIUM);
            CarPart suspension = new Suspension("S3", "Test", 100);
            CarPart gearbox = new Gearbox("G3", "Test", 100);

            // Each calls the same method polymorphically but returns different values
            double engineMult = engine.getPartTypeMultiplier();
            double aeroMult = aero.getPartTypeMultiplier();
            double tiresMult = tires.getPartTypeMultiplier();
            double suspMult = suspension.getPartTypeMultiplier();
            double gearMult = gearbox.getPartTypeMultiplier();

            if (engineMult == 0.35 && aeroMult == 0.25 && tiresMult == 0.20 &&
                suspMult == 0.10 && gearMult == 0.10) {
                pass();
            } else {
                fail("Polymorphic multipliers don't match expected values");
            }
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    private static void testPerformableInterface() {
        System.out.print("Test: Performable Interface Implementation... ");
        try {
            Car car = new Car("C3", "Interface Test Car");
            car.setEngine(new Engine("E4", "Test", 90));

            // Test that Car implements Performable interface
            if (!(car instanceof Performable)) {
                fail("Car does not implement Performable interface");
                return;
            }

            // Test that getPerformanceRating() works
            double rating = car.getPerformanceRating();
            double expected = 90 * 0.35; // Only engine

            if (Math.abs(rating - expected) < 0.01) {
                pass();
            } else {
                fail("Expected performance rating: " + expected + ", got: " + rating);
            }
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
