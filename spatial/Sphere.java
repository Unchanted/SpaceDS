package spatial;

public class Sphere implements Shape3D {

    public double x;
    public double y;
    public double z;
    public double r;

    public Sphere(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    @Override
    public Box getBounds() {
        double d = 2 * r;
        return new Box(this.x, this.y, this.z, d, d, d);
    }

    @Override
    public Point3D getPoint() {
        return new Point3D(this.x, this.y, this.z);
    }

}
