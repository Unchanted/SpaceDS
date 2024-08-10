package spatial;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.Random;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;

public class QuadTree {
    private Rectangle2D b;
    private int n;

    private LinkedList<Shape> e = new LinkedList<Shape>();

    private QuadTree nw;
    private QuadTree ne;
    private QuadTree se;
    private QuadTree sw;

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

    public void draw(Graphics2D g) {
        g.draw(this.b);
        for (Shape s : this.e)
            g.draw(s);
        if (this.subdivided) {
            this.nw.draw(g);
            this.ne.draw(g);
            this.se.draw(g);
            this.sw.draw(g);
        }
    }

    public static void main(String[] args) {
        long seed = 1;

        int N = 1000;

        int n = 4;
        int w = 1000;
        int h = 1000;

        QuadTree tree = new QuadTree(new Rectangle2D.Double(0, 0, w, h), n);

        Random prng = new Random(seed);
        for (int i = 0; i < N; i++)
            tree.insert(new Ellipse2D.Double(prng.nextDouble() * w + 1, prng.nextDouble() * h + 1, 4, 4));

        tree.pprint(0, "root");

        JFrame frame = new JFrame("QuadTree");
        Canvas canvas = new Canvas() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                tree.draw((Graphics2D) g);
            }
        };
        canvas.setSize(w, h);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
