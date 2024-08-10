package spatial;

public class Box implements Shape3D {
    public double x;
    public double y;
    public double z;
    public double w;
    public double h;
    public double d;

    public Box(double x, double y, double z, double w, double h, double d) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.h = h;
        this.d = d;
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

    public double[][] lines() {
        double[][] lines = new double[12][6];

        // horizontal lines
        lines[0] = new double[] { this.x, this.y, this.z, this.getMaxX(), this.y, this.z };
        lines[1] = new double[] { this.x, this.getMaxY(), this.z, this.w, this.getMaxY(), this.z };
        lines[2] = new double[] { this.x, this.y, this.getMaxZ(), this.w, this.y, this.getMaxZ() };
        lines[3] = new double[] { this.x, this.getMaxY(), this.getMaxZ(), this.w, this.getMaxY(), this.getMaxZ() };

        // vertical lines
        lines[4] = new double[] { this.x, this.y, this.z, this.x, this.getMaxY(), this.z };
        lines[5] = new double[] { this.w, this.y, this.z, this.w, this.getMaxY(), this.z };
        lines[6] = new double[] { this.x, this.y, this.getMaxZ(), this.x, this.getMaxY(), this.getMaxZ() };
        lines[7] = new double[] { this.w, this.y, this.getMaxZ(), this.w, this.getMaxY(), this.getMaxZ() };

        // z lines
        lines[8] = new double[] { this.x, this.y, this.z, this.x, this.y, this.getMaxZ() };
        lines[9] = new double[] { this.w, this.y, this.z, this.w, this.y, this.getMaxZ() };
        lines[10] = new double[] { this.x, this.getMaxY(), this.z, this.x, this.getMaxY(), this.getMaxZ() };
        lines[11] = new double[] { this.w, this.getMaxY(), this.z, this.w, this.getMaxY(), this.getMaxZ() };

        return lines;
    }

    public boolean contains(Box b) {
        boolean x = this.x <= b.x && b.getMaxX() <= this.getMaxX();
        boolean y = this.y <= b.y && b.getMaxY() <= this.getMaxY();
        boolean z = this.z <= b.z && b.getMaxZ() <= this.getMaxZ();
        return x && y && z;
    }

    public boolean intersects(Box b) {
        boolean x = this.getMaxX() >= b.x && this.x >= b.getMaxX();
        boolean y = this.getMaxY() >= b.y && this.y >= b.getMaxY();
        boolean z = this.getMaxZ() >= b.z && this.z >= b.getMaxZ();
        return x && y && z;
    }

    @Override
    public Box getBounds() {
        return this;
    }

    @Override
    public Point3D getPoint() {
        return new Point3D(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return String.format("%s[%f,%f,%f,%f,%f,%f]", this.getClass().getName(), this.x, this.y, this.z, this.w, this.h,
                this.d);
    }
}
