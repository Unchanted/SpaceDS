package spatial;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.Objects;

public class QuadTree {
    private Rectangle2D b;
    private int n;

    private LinkedList<Shape> e = new LinkedList<>();

    private QuadTree nw;
    private QuadTree ne;
    private QuadTree se;
    private QuadTree sw;

    private boolean subdivided = false;

    public QuadTree(Rectangle2D.Double b, int n) {
        this.b = b;
        this.n = n;
    }

    public LinkedList<Shape> elements() {
        return e;
    }

    public Rectangle2D bounds() {
        return b;
    }

    public void subdivide() {
        double x = b.getX(), y = b.getY();
        double w = b.getWidth() / 2, h = b.getHeight() / 2;

        nw = new QuadTree(new Rectangle2D.Double(x, y, w, h), n);
        ne = new QuadTree(new Rectangle2D.Double(x + w, y, w, h), n);
        se = new QuadTree(new Rectangle2D.Double(x + w, y + h, w, h), n);
        sw = new QuadTree(new Rectangle2D.Double(x, y + h, w, h), n);

        subdivided = true;

        for (Shape s : e) {
            nw.insert(s);
            ne.insert(s);
            se.insert(s);
            sw.insert(s);
        }

        e.clear();
    }

    public boolean insert(Shape s) {
        // out-of-bounds, doesn't belong here
        if (!b.contains(s.getBounds2D()))
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
        if (nw.insert(s))
            return true;
        if (ne.insert(s))
            return true;
        if (se.insert(s))
            return true;
        if (sw.insert(s))
            return true;

        // unreachable, added for return type guaranteeq
        return false;
    }

    public QuadTree[] children() {
        return new QuadTree[] { nw, ne, se, sw };
    }

    public Stream<QuadTree> traverse() {
        return Stream.concat(Stream.of(this),
                Arrays.stream(children()).filter(Objects::nonNull).flatMap(QuadTree::traverse));
    }

    public void clear() {
        nw = ne = se = sw = null;
        e.clear();
        subdivided = false;
    }

    public void pprint(int lvl, String lab) {
        String tabs = new String(new char[lvl]).replace("\0", "\t");

        if (e.isEmpty())
            System.out.printf("%s%d %s\n", tabs, lvl, lab);
        for (Shape s : e) {
            System.out.printf("%s%d %s %s\n", tabs, lvl, lab, s.getBounds2D());
        }

        if (subdivided) {
            nw.pprint(lvl + 1, "nw");
            ne.pprint(lvl + 1, "ne");
            se.pprint(lvl + 1, "se");
            sw.pprint(lvl + 1, "sw");
        }
    }

}
