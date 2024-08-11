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
        return x + w;
    }

    public double getMaxY() {
        return y + h;
    }

    public double getMaxZ() {
        return z + d;
    }

    public Point3D[][] lines() {
        return new Point3D[][] {
                // horizontal lines
                { new Point3D(x, y, z), new Point3D(getMaxX(), y, z) },
                { new Point3D(x, getMaxY(), z), new Point3D(getMaxX(), getMaxY(), z) },
                { new Point3D(x, y, getMaxZ()), new Point3D(getMaxX(), y, getMaxZ()) },
                { new Point3D(x, getMaxY(), getMaxZ()), new Point3D(getMaxX(), getMaxY(), getMaxZ()) },

                // vertical lines
                { new Point3D(x, y, z), new Point3D(x, getMaxY(), z) },
                { new Point3D(getMaxX(), y, z), new Point3D(getMaxX(), getMaxY(), z) },
                { new Point3D(x, y, getMaxZ()), new Point3D(x, getMaxY(), getMaxZ()) },
                { new Point3D(getMaxX(), y, getMaxZ()), new Point3D(getMaxX(), getMaxY(), getMaxZ()) },

                // depth lines
                { new Point3D(x, y, z), new Point3D(x, y, getMaxZ()) },
                { new Point3D(getMaxX(), y, z), new Point3D(getMaxX(), y, getMaxZ()) },
                { new Point3D(x, getMaxY(), z), new Point3D(x, getMaxY(), getMaxZ()) },
                { new Point3D(getMaxX(), getMaxY(), z), new Point3D(getMaxX(), getMaxY(), getMaxZ()) } };
    }

    public Point3D[][] facePaths() {
        return new Point3D[][] {
                // front
                // x, y, z -> X, y, z
                // X, y, z -> X, Y, z
                // X, Y, z -> x, Y, z
                // x, Y, z -> x, y, z
                { new Point3D(x, y, z), new Point3D(getMaxX(), y, z), new Point3D(getMaxX(), y, z),
                        new Point3D(getMaxX(), getMaxY(), z), new Point3D(getMaxX(), getMaxY(), z),
                        new Point3D(x, getMaxY(), z), new Point3D(x, getMaxY(), z), new Point3D(x, y, z) },
                // back
                // x, y, Z -> X, y, Z
                // X, y, Z -> X, Y, Z
                // X, Y, Z -> x, Y, Z
                // x, Y, Z -> x, y, Z
                { new Point3D(x, y, getMaxZ()), new Point3D(getMaxX(), y, getMaxZ()),
                        new Point3D(getMaxX(), y, getMaxZ()), new Point3D(getMaxX(), getMaxY(), getMaxZ()),
                        new Point3D(getMaxX(), getMaxY(), getMaxZ()), new Point3D(x, getMaxY(), getMaxZ()),
                        new Point3D(x, getMaxY(), getMaxZ()), new Point3D(x, y, getMaxZ()) },
                // left
                // x, y, z -> x, y, Z
                // x, y, Z -> x, Y, Z
                // x, Y, Z -> x, Y, z
                // x, Y, z -> x, y, z
                { new Point3D(x, y, z), new Point3D(x, y, getMaxZ()), new Point3D(x, y, getMaxZ()),
                        new Point3D(x, getMaxY(), getMaxZ()), new Point3D(x, getMaxY(), getMaxZ()),
                        new Point3D(x, getMaxY(), z), new Point3D(x, getMaxY(), z), new Point3D(x, y, z) },
                // right
                // X, y, z -> X, y, Z
                // X, y, Z -> X, Y, Z
                // X, Y, Z -> X, Y, z
                // X, Y, z -> X, y, z
                { new Point3D(getMaxX(), y, z), new Point3D(getMaxX(), y, getMaxZ()),
                        new Point3D(getMaxX(), y, getMaxZ()), new Point3D(getMaxX(), getMaxY(), getMaxZ()),
                        new Point3D(getMaxX(), getMaxY(), getMaxZ()), new Point3D(getMaxX(), getMaxY(), z),
                        new Point3D(getMaxX(), getMaxY(), z), new Point3D(getMaxX(), y, z) },
                // top
                // x, y, z -> x, y, Z
                // x, y, Z -> X, y, Z
                // X, y, Z -> X, y, z
                // X, y, z -> x, y, z
                { new Point3D(x, y, z), new Point3D(x, y, getMaxZ()), new Point3D(x, y, getMaxZ()),
                        new Point3D(getMaxX(), y, getMaxZ()), new Point3D(getMaxX(), y, getMaxZ()),
                        new Point3D(getMaxX(), y, z), new Point3D(getMaxX(), y, z), new Point3D(x, y, z) },
                // bottom
                // x, Y, z -> x, Y, Z
                // x, Y, Z -> X, Y, Z
                // X, Y, Z -> X, Y, z
                // X, Y, z -> x, Y, z
                { new Point3D(x, getMaxY(), z), new Point3D(x, getMaxY(), getMaxZ()),
                        new Point3D(x, getMaxY(), getMaxZ()), new Point3D(getMaxX(), getMaxY(), getMaxZ()),
                        new Point3D(getMaxX(), getMaxY(), getMaxZ()), new Point3D(getMaxX(), getMaxY(), z),
                        new Point3D(getMaxX(), getMaxY(), z), new Point3D(x, getMaxY(), z) } };
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
        return new Point3D(x, y, z);
    }

    @Override
    public String toString() {
        return String.format("%s[%f,%f,%f,%f,%f,%f]", this.getClass().getName(), this.x, this.y, this.z, this.w, this.h,
                this.d);
    }
}
