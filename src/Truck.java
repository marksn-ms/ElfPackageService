/**
 * A delivery truck has an identifier and weight capacity.
 */
public class Truck {
    private final String truckName;
    public String getTruckName() { return truckName; }
    private final int truckCapacity;
    public int getTruckCapacity() { return truckCapacity; }
    public Truck(String truckName, int truckCapacity) {
        this.truckName = truckName;
        this.truckCapacity = truckCapacity;
    }
    public String toString() {
        return truckName + " (" + truckCapacity + "lbs)";
    }
}
