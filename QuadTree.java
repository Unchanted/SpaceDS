import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.Random;

public class QuadTree {
    private Rectangle2D b;
    private int n;

    private LinkedList<Shape> e = new LinkedList<Shape>();

    private QuadTree nw;
    private QuadTree ne;
    private QuadTree sw;
    private QuadTree se;

    private boolean subdivided = false;

    public QuadTree(Rectangle2D.Double b, int n) {
        this.b = b;
        this.n = n;
    }

    public void subdivide() {
        double x = this.b.getX(), y = this.b.getY();
        double w = this.b.getWidth() / 2, h = this.b.getHeight() / 2;
        this.nw = new QuadTree(new Rectangle2D.Double(x, y, w, h), this.n);
        this.ne = new QuadTree(new Rectangle2D.Double(x + w, y, w, h), this.n);
        this.se = new QuadTree(new Rectangle2D.Double(x + w, y + h, w, h), this.n);
        this.sw = new QuadTree(new Rectangle2D.Double(x, y + h, w, h), this.n);

        this.subdivided = true;

        for (Shape s : this.e) {
            this.nw.insert(s);
            this.ne.insert(s);
            this.se.insert(s);
            this.sw.insert(s);
        }

        this.e.clear();
    }

    public boolean insert(Shape s) {
        // out-of-bounds, doesn't belong here
        if (!this.b.contains(s.getBounds2D()))
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
        if (this.nw.insert(s))
            return true;
        if (this.ne.insert(s))
            return true;
        if (this.se.insert(s))
            return true;
        if (this.sw.insert(s))
            return true;

        // unreachable, added for return type guarantee
        return false;
    }

    public void pprint(int lvl, String lab) {
        String tabs = new String(new char[lvl]).replace("\0", "\t");

        if (this.e.isEmpty())
            System.out.printf("%s%d %s\n", tabs, lvl, lab);
        for (Shape s : this.e) {
            System.out.printf("%s%d %s %s\n", tabs, lvl, lab, s.getBounds2D());
        }

        if (this.subdivided) {
            this.nw.pprint(lvl + 1, "nw");
            this.ne.pprint(lvl + 1, "ne");
            this.se.pprint(lvl + 1, "se");
            this.sw.pprint(lvl + 1, "sw");
        }
    }

    public static void main(String[] args) {
        long seed = 1;

        int n = 4;
        int N = 100;

        QuadTree tree = new QuadTree(new Rectangle2D.Double(0, 0, 1000, 1000), n);

        Random prng = new Random(seed);
        for (int i = 0; i < N; i++)
            tree.insert(new Ellipse2D.Double(prng.nextDouble() * 1000, prng.nextDouble() * 1000, 4, 4));

        tree.pprint(0, "root");
    }
}
