package spatial;

public class Sphere implements Shape3D {

    private double x;
    private double y;
    private double z;
    private double r;

    public Sphere(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Point3D getPoint() {
        return new Point3D(this.x, this.y, this.z);
    }

    @Override
    public Box getBounds() {
        double d = 2 * r;
        return new Box(this.x, this.y, this.z, d, d, d);
    }

}
