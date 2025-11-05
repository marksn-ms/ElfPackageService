/**
 * A package has an identifier, a weight and a joy value.
 */
public class Package {
    private final String packageName;
    public String getPackageName() { return packageName; }
    private final int weight;
    public int getWeight() { return weight; }
    private final int joy;
    public int getJoy() { return joy; }
    public Package(String packageName, int weight, int joy) {
        this.packageName = packageName;
        this.weight = weight;
        this.joy = joy;
    }
    public String toString() {
        return packageName + " (" + weight + "lbs, " + joy + "joy)";
    }
}
