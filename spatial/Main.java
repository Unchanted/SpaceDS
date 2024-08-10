package spatial;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Collectors;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import java.awt.AlphaComposite;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Main {

    public static void main(String[] args) {
        long seed = 1;

        int w = 1000, h = 1000, d = 1000;
        int r = 4;
        int n = 4;
        int N = 10000;

        // generate random points
        Random prng = new Random(seed);
        Sphere[] pts = new Sphere[N];
        for (int i = 0; i < N; i++) {
            pts[i] = new Sphere(prng.nextDouble() * w + 1, prng.nextDouble() * h + 1, prng.nextDouble() * d + 1, r);
        }

        // insert points
        OctTree tree = new OctTree(new Box(0, 0, 0, w, h, d), n);
        for (Sphere sphere : pts) {
            tree.insert(sphere);
        }
        tree.pprint(0, "root");

        // get octants
        LinkedList<Box> octants = new LinkedList<Box>();
        tree.octants(octants);

        // set perspective
        double xrot = 5 * Math.PI / 180, yrot = 5 * Math.PI / 180, zrot = 0 * Math.PI / 180;

        JFrame frame = new JFrame("QuadTree");
        Canvas3D canvas = new Canvas3D() {

            private static final long serialVersionUID = 2581345844322652928L;

            @Override
            public void paint(Graphics g) {
                Graphics2D G = (Graphics2D) g;
                // draw axes
                for (Line2D.Double line : this.project_lines(this.getAxes()))
                    G.draw(line);
                // plot octants
                // for (Box box : octants)
                //     for (Line2D.Double line : this.project_lines(box.lines()))
                //         G.draw(line);
                // plot points
                for (Point2D.Double p : this.project_points(Arrays.stream(pts).map(p -> p.getPoint()).collect(Collectors.toList())))
                    G.fill(new Ellipse2D.Double(p.x, p.y, r, r));
            }
        };
        canvas.setSize(w, h);
        canvas.setDimensions(w, h, d);
        canvas.setPerspective(xrot, yrot, zrot);
        canvas.addKeyListener(new KeyListener() {
            private final double RAD = Math.PI / 180;
            @Override
            public void keyPressed(KeyEvent e) {
                double[] prsp;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        prsp = canvas.getPerspective();
                        canvas.setPerspective(prsp[0] + RAD, prsp[1], prsp[2]);
                        canvas.repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        prsp = canvas.getPerspective();
                        canvas.setPerspective(prsp[0] - RAD, prsp[1], prsp[2]);
                        canvas.repaint();
                        break;
                    case KeyEvent.VK_LEFT:
                        prsp = canvas.getPerspective();
                        canvas.setPerspective(prsp[0], prsp[1] + RAD, prsp[2]);
                        canvas.repaint();
                        break;
                    case KeyEvent.VK_RIGHT:
                        prsp = canvas.getPerspective();
                        canvas.setPerspective(prsp[0], prsp[1] - RAD, prsp[2]);
                        canvas.repaint();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

    }
}
