package spatial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    public LinkedList<Shape3D> elements() {
        return e;
    }

    public Box bounds() {
        return b;
    }

    public void subdivide() {
        double x = b.x, y = b.y, z = b.z;
        double w = b.w / 2, h = b.h / 2, d = b.d / 2;

        nw1 = new OctTree(new Box(x, y, z, w, h, d), n);
        ne1 = new OctTree(new Box(x + w, y, z, w, h, d), n);
        se1 = new OctTree(new Box(x + w, y + h, z, w, h, d), n);
        sw1 = new OctTree(new Box(x, y + h, z, w, h, d), n);
        nw2 = new OctTree(new Box(x, y, z + d, w, h, d), n);
        ne2 = new OctTree(new Box(x + w, y, z + d, w, h, d), n);
        se2 = new OctTree(new Box(x + w, y + h, z + d, w, h, d), n);
        sw2 = new OctTree(new Box(x, y + h, z + d, w, h, d), n);

        subdivided = true;

        for (Shape3D s : e) {
            nw1.insert(s);
            ne1.insert(s);
            se1.insert(s);
            sw1.insert(s);
            nw2.insert(s);
            ne2.insert(s);
            se2.insert(s);
            sw2.insert(s);
        }

        e.clear();
    }

    public boolean insert(Shape3D s) {
        // out-of-bounds, doesn't belong here
        if (!b.contains(s.getBounds()))
            return false;

        // check to see if there is more space
        if (!subdivided && e.size() < n) {
            e.add(s);
            return true;
        }

        // over-capacity, subdivide
        if (!subdivided)
            subdivide();

        // need to insert into subtree
        if (nw1.insert(s))
            return true;
        if (ne1.insert(s))
            return true;
        if (se1.insert(s))
            return true;
        if (sw1.insert(s))
            return true;
        if (nw2.insert(s))
            return true;
        if (ne2.insert(s))
            return true;
        if (se2.insert(s))
            return true;
        if (sw2.insert(s))
            return true;

        // unreachable, added for return type guarantee
        return false;
    }

    public List<Shape3D> query(Shape3D inputPoint) {
        List<Shape3D> queryList = new ArrayList<>();

        System.out.println(subdivided);
        // out-of-bounds, doesn't belong here
        if (!b.contains(inputPoint.getBounds()))
            return queryList;

        // check to see if there is more space
        if (!subdivided) {
            if (inputPoint.getBounds().intersects(b)) {
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

    public OctTree[] children() {
        return new OctTree[] { nw1, ne1, se1, sw1, nw2, ne2, se2, sw2 };
    }

    public Stream<OctTree> traverse() {
        return Stream.concat(Stream.of(this),
                Arrays.stream(children()).filter(Objects::nonNull).flatMap(OctTree::traverse));
    }

    public void clear() {
        nw1 = ne1 = se1 = sw1 = null;
        nw2 = ne2 = se2 = sw2 = null;
        e.clear();
        subdivided = false;
    }

    public void pprint(int lvl, String lab) {
        String tabs = new String(new char[lvl]).replace("\0", "\t");

        if (e.isEmpty())
            System.out.printf("%s%d %s\n", tabs, lvl, lab);
        for (Shape3D s : e) {
            System.out.printf("%s%d %s %s\n", tabs, lvl, lab, s.getBounds());
        }

        if (subdivided) {
            nw1.pprint(lvl + 1, "nw1");
            ne1.pprint(lvl + 1, "ne1");
            se1.pprint(lvl + 1, "se1");
            sw1.pprint(lvl + 1, "sw1");
            nw2.pprint(lvl + 1, "nw2");
            ne2.pprint(lvl + 1, "ne2");
            se2.pprint(lvl + 1, "se2");
            sw2.pprint(lvl + 1, "sw2");
        }
    }

}
