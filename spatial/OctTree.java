package spatial;

import java.util.LinkedList;

public class OctTree {
    private Box b;
    private int n;

    private LinkedList<Shape3D> e = new LinkedList<Shape3D>();

    private OctTree nw1;
    private OctTree ne1;
    private OctTree se1;
    private OctTree sw1;
    private OctTree nw2;
    private OctTree ne2;
    private OctTree se2;
    private OctTree sw2;

    private boolean subdivided = false;

    public OctTree(Box b, int n) {
        this.b = b;
        this.n = n;
    }

    public void subdivide() {
        double x = this.b.getX(), y = this.b.getY(), z = this.b.getZ();
        double w = this.b.getWidth() / 2, h = this.b.getHeight() / 2, d = this.b.getDepth();
        this.nw1 = new OctTree(new Box(x, y, z, w, h, d), this.n);
        this.ne1 = new OctTree(new Box(x + w, y, z, w, h, d), this.n);
        this.se1 = new OctTree(new Box(x + w, y + h, z, w, h, d), this.n);
        this.sw1 = new OctTree(new Box(x, y + h, z, w, h, d), this.n);
        this.nw2 = new OctTree(new Box(x, y, z + d, w, h, d), this.n);
        this.ne2 = new OctTree(new Box(x + w, y, z + d, w, h, d), this.n);
        this.se2 = new OctTree(new Box(x + w, y + h, z + d, w, h, d), this.n);
        this.sw2 = new OctTree(new Box(x, y + h, z + d, w, h, d), this.n);

        this.subdivided = true;

        for (Shape3D s : this.e) {
            this.nw1.insert(s);
            this.ne1.insert(s);
            this.se1.insert(s);
            this.sw1.insert(s);
            this.nw2.insert(s);
            this.ne2.insert(s);
            this.se2.insert(s);
            this.sw2.insert(s);
        }

        this.e.clear();
    }

    public boolean insert(Shape3D s) {
        // out-of-bounds, doesn't belong here
        if (!this.b.contains(s.getBounds()))
            return false;

        // check to see if there is more space
        if (!this.subdivided && e.size() < this.n) {
            this.e.add(s);
            return true;
        }

        // over-capacity, subdivide
        if (!this.subdivided)
            this.subdivide();

        // need to insert into subtree
        if (this.nw1.insert(s))
            return true;
        if (this.ne1.insert(s))
            return true;
        if (this.se1.insert(s))
            return true;
        if (this.sw1.insert(s))
            return true;
        if (this.nw2.insert(s))
            return true;
        if (this.ne2.insert(s))
            return true;
        if (this.se2.insert(s))
            return true;
        if (this.sw2.insert(s))
            return true;

        // unreachable, added for return type guarantee
        return false;
    }

    public void pprint(int lvl, String lab) {
        String tabs = new String(new char[lvl]).replace("\0", "\t");

        if (this.e.isEmpty())
            System.out.printf("%s%d %s\n", tabs, lvl, lab);
        for (Shape3D s : this.e) {
            System.out.printf("%s%d %s %s\n", tabs, lvl, lab, s.getBounds());
        }

        if (this.subdivided) {
            this.nw1.pprint(lvl + 1, "nw1");
            this.ne1.pprint(lvl + 1, "ne1");
            this.se1.pprint(lvl + 1, "se1");
            this.sw1.pprint(lvl + 1, "sw1");
            this.nw2.pprint(lvl + 1, "nw2");
            this.ne2.pprint(lvl + 1, "ne2");
            this.se2.pprint(lvl + 1, "se2");
            this.sw2.pprint(lvl + 1, "sw2");
        }
    }

}
