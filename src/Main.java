import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Santa has requested a large delivery.  Read details from file
 * in json format and print a list of trucks and which packages
 * are assigned to each truck.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Elf Package Service (EPS) Christmas Planner, v1.0");

        // In case you aren't sure where to put your json files...
        //System.out.println("Current working directory: " + System.getProperty("user.dir"));

        List<Truck> trucks = readTrucks("trucks2.json");
        List<Package> packages = readPackages("packages2.json");

        // Print the parsed data (for verification)
        System.out.println("Trucks: " +
                trucks.stream()
                        .map(Truck::toString)
                        .collect(Collectors.joining(", ")));

        System.out.println("Packages: " +
                packages.stream()
                        .map(Package::toString)
                        .collect(Collectors.joining(", ")));

        // Time the brute force approach
        long bruteForceTime = System.nanoTime();
        DeliveryPlan bruteForcePlan = solveBruteForce(new ArrayList<Truck>(trucks), new ArrayList<Package>(packages));
        bruteForceTime = System.nanoTime() - bruteForceTime;
        System.out.println("=== BRUTE FORCE ===");
        System.out.println(bruteForcePlan);
        System.out.println("Execution Time: " + bruteForceTime
                + " nanoseconds, Total Joy: " + bruteForcePlan.getTotalJoy());

        // Time the dynamic programming approach
        long dynamicProgrammingTime = System.nanoTime();
        DeliveryPlan dpPlan = solveDynamicProgramming(new ArrayList<Truck>(trucks), new ArrayList<Package>(packages));
        dynamicProgrammingTime = System.nanoTime() - dynamicProgrammingTime;
        System.out.println("=== DYNAMIC PROGRAMMING ===");
        System.out.println(dpPlan);
        System.out.println("Execution Time: " + dynamicProgrammingTime
                + " nanoseconds, Total Joy: " + dpPlan.getTotalJoy());

        // Show the performance comparison
        System.out.println("=== PERFORMANCE COMPARISON ===");
        System.out.println("Brute Force: " + (bruteForceTime / 1_000_000.0) + " ms");
        System.out.println("Dynamic Programming: " + (dynamicProgrammingTime / 1_000_000.0) + " ms");

        double speedup = (double) bruteForceTime / dynamicProgrammingTime;
        System.out.println("DP is " + String.format("%.2f", speedup) + "x faster!");

        System.out.printf("\nThank you for using Elf Package Service (EPS) Christmas Planner.");
    }

    /**
     * Open trucks.json, read the list of trucks and return as a list.
     * @return List of Truck objects
     */
    private static List<Truck> readTrucks(String filename) {
        List<Truck> trucks = new ArrayList<>();
        try (FileReader reader = new FileReader(filename)) {
            Gson gson = new Gson();
            Type truckListType = new TypeToken<List<Truck>>(){}.getType();
            trucks = gson.fromJson(reader, truckListType);// Parse to JsonObject first
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trucks;
    }

    /**
     * Open packages.json, read the list of packages and return as a list.
     * @return List of Package objects
     */
    private static List<Package> readPackages(String filename) {
        List<Package> packages = new ArrayList<>();
        try (FileReader reader = new FileReader(filename)) {
            Gson gson = new Gson();
            Type packageListType = new TypeToken<List<Package>>(){}.getType();
            packages = gson.fromJson(reader, packageListType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packages;
    }

    /**
     * BRUTE FORCE APPROACH
     * Tries every possible combination of packages for each truck.
     * Time Complexity: O(2^n) where n is the number of packages
     */
    public static DeliveryPlan solveBruteForce(List<Truck> trucks, List<Package> packages) {
        DeliveryPlan bestPlan = new DeliveryPlan();

        // For each truck, try all possible combinations of packages
        for (Truck truck : trucks) {
            List<Package> bestPackagesForTruck = new ArrayList<>();
            int bestJoyForTruck = 0;

            // Generate all possible subsets of packages (2^n combinations)
            int totalCombinations = (int) Math.pow(2, packages.size());

            for (int i = 0; i < totalCombinations; i++) {
                List<Package> currentCombination = new ArrayList<>();
                int currentWeight = 0;
                int currentJoy = 0;

                // Check each bit to see if package should be included
                for (int j = 0; j < packages.size(); j++) {
                    if ((i & (1 << j)) != 0) {  // If bit j is set
                        Package pkg = packages.get(j);
                        currentCombination.add(pkg);
                        currentWeight += pkg.getWeight();
                        currentJoy += pkg.getJoy();
                    }
                }

                // If this combination fits and is better, save it
                if (currentWeight <= truck.getTruckCapacity() && currentJoy > bestJoyForTruck) {
                    bestJoyForTruck = currentJoy;
                    bestPackagesForTruck = new ArrayList<>(currentCombination);
                }
            }

            // Assign the best packages to this truck
            for (Package pkg : bestPackagesForTruck) {
                bestPlan.assignPackage(truck, pkg);
            }

            // Remove used packages from the list for next truck
            packages.removeAll(bestPackagesForTruck);
        }

        return bestPlan;
    }

    /**
     * DYNAMIC PROGRAMMING APPROACH (0/1 Knapsack)
     * Uses memoization to avoid recalculating the same subproblems.
     * Time Complexity: O(n * W) where n is packages and W is capacity
     */
    public static DeliveryPlan solveDynamicProgramming(List<Truck> trucks, List<Package> packages) {
        DeliveryPlan plan = new DeliveryPlan();

        // Process each truck independently
        for (Truck truck : trucks) {
            int capacity = truck.getTruckCapacity();
            int n = packages.size();

            // Create DP table: dp[i][w] = max joy using first i packages with weight limit w
            int[][] dp = new int[n + 1][capacity + 1];

            // Fill the DP table
            for (int i = 1; i <= n; i++) {
                Package pkg = packages.get(i - 1);
                int weight = pkg.getWeight();
                int joy = pkg.getJoy();

                for (int w = 0; w <= capacity; w++) {
                    // Option 1: Don't include this package
                    dp[i][w] = dp[i - 1][w];

                    // Option 2: Include this package (if it fits)
                    if (weight <= w) {
                        int joyWithPackage = dp[i - 1][w - weight] + joy;
                        dp[i][w] = Math.max(dp[i][w], joyWithPackage);
                    }
                }
            }

            // Backtrack to find which packages were selected
            List<Package> selectedPackages = new ArrayList<>();
            int w = capacity;
            for (int i = n; i > 0 && w > 0; i--) {
                // If value changed, this package was included
                if (dp[i][w] != dp[i - 1][w]) {
                    Package pkg = packages.get(i - 1);
                    selectedPackages.add(pkg);
                    w -= pkg.getWeight();
                }
            }

            // Assign selected packages to this truck
            for (Package pkg : selectedPackages) {
                plan.assignPackage(truck, pkg);
            }

            // Remove used packages for next truck
            packages.removeAll(selectedPackages);
        }

        return plan;
    }
}