package spatial;

public class Box implements Shape3D {
    private double x;
    private double y;
    private double z;
    private double w;
    private double h;
    private double d;

    public Box(double x, double y, double z, double w, double h, double d) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.h = h;
        this.d = d;
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

    public double getWidth() {
        return this.w;
    }

    public double getHeight() {
        return this.h;
    }

    public double getDepth() {
        return this.d;
    }

    public double getMaxX() {
        return this.x + this.w;
    }

    public double getMaxY() {
        return this.y + this.h;
    }

    public double getMaxZ() {
        return this.z + this.d;
    }

    public boolean contains(Box b) {
        boolean x = this.x <= b.getX() && b.getMaxX() <= this.getMaxX();
        boolean y = this.y <= b.getY() && b.getMaxY() <= this.getMaxY();
        boolean z = this.z <= b.getZ() && b.getMaxZ() <= this.getMaxZ();
        // boolean result = x && y && z;
        // System.out.println(this);
        // System.out.println(b);
        // System.out.printf("%f <= %f && %f <= %f -> %b\n", this.x, b.getX(), b.getMaxX(), this.getMaxX(), x);
        // System.out.printf("%f <= %f && %f <= %f -> %b\n", this.x, b.getY(), b.getMaxY(), this.getMaxY(), y);
        // System.out.printf("%f <= %f && %f <= %f -> %b\n", this.x, b.getZ(), b.getMaxZ(), this.getMaxZ(), z);
        // System.out.println(result);
        // System.out.println();
        return x && y && z;
    }

    @Override
    public Box getBounds() {
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s[%f,%f,%f,%f,%f,%f]", this.getClass().getName(), this.x, this.y, this.z, this.w, this.h,
                this.d);
    }
}
