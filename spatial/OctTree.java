package spatial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        double x = this.b.x, y = this.b.y, z = this.b.z;
        double w = this.b.w / 2, h = this.b.h / 2, d = this.b.d / 2;
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

    public List<Shape3D> query(Shape3D inputPoint) {
        List<Shape3D> queryList = new ArrayList<>();

        System.out.println(subdivided);
        // out-of-bounds, doesn't belong here
        if (!this.b.contains(inputPoint.getBounds()))
            return queryList;

        // check to see if there is more space
        if (!this.subdivided) {
            if (inputPoint.getBounds().intersects(this.b)) {
                queryList.addAll(e);
            } else {
                for (Shape3D point : e) {
                    if (inputPoint.getBounds().intersects(point.getBounds())) {
                        queryList.add(point);
                    }
                }
            }
        }

        // need to insert into subtree
        queryList.addAll(nw1.query(inputPoint));
        queryList.addAll(ne1.query(inputPoint));
        queryList.addAll(se1.query(inputPoint));
        queryList.addAll(sw1.query(inputPoint));
        queryList.addAll(nw2.query(inputPoint));
        queryList.addAll(ne2.query(inputPoint));
        queryList.addAll(se2.query(inputPoint));
        queryList.addAll(sw2.query(inputPoint));

        // unreachable, added for return type guarantee
        return queryList;
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

    public LinkedList<Shape3D> elements() {
        return this.e;
    }

    public Box bounds() {
        return this.b;
    }

    public LinkedList<OctTree>  trees() {
        LinkedList<OctTree> trees = new LinkedList<OctTree> ();
        this.trees(trees);
        return trees;
    }

    public void trees(LinkedList<OctTree> trees) {
        if (this.subdivided) {
            this.nw1.trees(trees);
            this.ne1.trees(trees);
            this.se1.trees(trees);
            this.sw1.trees(trees);
            this.nw2.trees(trees);
            this.ne2.trees(trees);
            this.se2.trees(trees);
            this.sw2.trees(trees);
        } else {
            trees.add(this);
        }
    }

    public void clear() {
        this.nw1 = null;
        this.ne1 = null;
        this.se1 = null;
        this.sw1 = null;
        this.nw2 = null;
        this.ne2 = null;
        this.se2 = null;
        this.sw2 = null;
        this.e.clear();
        this.subdivided = false;
    }

}
