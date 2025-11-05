import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Result class to hold the mapping of trucks to packages.
 */
public class DeliveryPlan {
    private final Map<Truck, List<Package>> truckAssignments;
    private int totalJoy;

    public DeliveryPlan() {
        this.truckAssignments = new HashMap<>();
        this.totalJoy = 0;
    }

    public void assignPackage(Truck truck, Package pkg) {
        truckAssignments.computeIfAbsent(truck, k -> new ArrayList<>()).add(pkg);
        totalJoy += pkg.getJoy();
    }

    public Map<Truck, List<Package>> getAssignments() {
        return truckAssignments;
    }

    public int getTotalJoy() {
        return totalJoy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Truck, List<Package>> entry : truckAssignments.entrySet()) {
            if (!sb.isEmpty()) { sb.append("\n"); }
            sb.append(entry.getKey()).append(" carries: ").append(entry.getValue());
        }
        return sb.toString();
    }
}
