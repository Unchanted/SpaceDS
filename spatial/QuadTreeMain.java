package spatial;

import java.util.Collection;
import java.util.Random;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.Canvas;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class QuadTreeMain {

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

        Canvas canvas = new Canvas() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                Graphics2D G = (Graphics2D) g;
                tree.traverse().map(tree -> tree.elements()).flatMap(Collection::stream).forEach(ele -> G.draw(ele));
                tree.traverse().forEach(tree -> G.draw(tree.bounds()));
            }
        };
        canvas.setSize(w, h);

        JFrame frame = new JFrame("QuadTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
