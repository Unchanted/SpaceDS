package spatial;

public class Sphere implements Shape3D {

    public double x;
    public double y;
    public double z;
    private double r;
    private Box bounds;

    public Sphere(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        double d = 2 * r;
        this.bounds = new Box(this.x, this.y, this.z, d, d, d);
    }

    public void setR(double r) {
        this.r = r;
        double d = 2 * this.r;
        this.bounds = new Box(x, y, z, d, d, d);
    }

    public double getR() {
        return r;
    }

    @Override
    public Box getBounds() {
        return bounds;
    }

    @Override
    public Point3D getPoint() {
        return new Point3D(x, y, z);
    }

}