package spatial;

public class Sphere implements Shape3D {

    public double x;
    public double y;
    public double z;
    public double r;
    private Box bounds;

    public Sphere(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        double d = 2 * r;
        this.bounds = new Box(this.x, this.y, this.z, d, d, d);
    }

    public void updateRadius(double radiusUpdate) {
        this.r = radiusUpdate;
        double d = 2 * this.r;
        this.bounds = new Box(this.x, this.y, this.z, d, d, d);
    }

    @Override
    public Box getBounds() {
        return bounds;
    }

    @Override
    public Point3D getPoint() {
        return new Point3D(this.x, this.y, this.z);
    }

}