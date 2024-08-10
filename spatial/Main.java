package spatial;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.Random;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import java.awt.AlphaComposite;

public class Main {

    public static void main(String[] args) {

        int w = 1000, h = 1000, d = 1000;
        int n = 4;
        int N = 1000;

        Random prng = new Random();
        Sphere[] pts = new Sphere[N];
        for (int i = 0; i < N; i++) {
            pts[i] = new Sphere(prng.nextDouble() * w + 1, prng.nextDouble() * h + 1, prng.nextDouble() * d + 1, n);
        }

        OctTree tree = new OctTree(new Box(0, 0, 0, w, h, d), n);
        for (Sphere sphere : pts) {
            tree.insert(sphere);
        }
        tree.pprint(0, "root");

        double xrot = 5 * Math.PI / 180, yrot = 60 * Math.PI / 180, zrot = 0 * Math.PI / 180;

        double[][] angles = new double[2][3];
        angles[0][0] = Math.sin(xrot);
        angles[0][1] = Math.sin(yrot);
        angles[0][2] = Math.sin(zrot);
        angles[1][0] = Math.cos(xrot);
        angles[1][1] = Math.cos(yrot);
        angles[1][2] = Math.cos(zrot);

        double[][] rot = new double[3][3];
        rot[0][0] = angles[1][1] * angles[1][2];
        rot[0][1] = -angles[0][2] * angles[1][1];
        rot[0][2] = angles[0][1];
        rot[1][0] = angles[1][2] * -angles[0][1] * -angles[0][0] + angles[0][2] * angles[1][0];
        rot[1][1] = -angles[0][2] * -angles[0][1] * -angles[0][0] + angles[1][2] * angles[1][0];
        rot[1][2] = angles[1][1] * -angles[0][0];
        rot[2][0] = angles[1][2] * -angles[0][1] * angles[1][0] + angles[0][2] * angles[0][0];
        rot[2][1] = -angles[0][2] * -angles[0][1] * angles[1][0] + angles[1][2] * angles[0][0];
        rot[2][2] = angles[1][1] * angles[1][0];

        double[][] prj = new double[N][2];
        for (int i = 0; i < N; i++) {
            prj[i][0] = pts[i].getX() * rot[0][0] + pts[i].getY() * rot[0][1] + pts[i].getZ() * rot[0][2];
            prj[i][1] = pts[i].getX() * rot[1][0] + pts[i].getY() * rot[1][1] + pts[i].getZ() * rot[1][2];
        }

        double[][] axes = new double[3][4];
        // x
        axes[0][0] = -w * rot[0][0] + w / 2;
        axes[0][1] = -w * rot[1][0] + w / 2;
        axes[0][2] = w * rot[0][0] + w / 2;
        axes[0][3] = w * rot[1][0] + w / 2;
        // y
        axes[1][0] = -h * rot[0][1] + h / 2;
        axes[1][1] = -h * rot[1][1] + h / 2;
        axes[1][2] = h * rot[0][1] + h / 2;
        axes[1][3] = h * rot[1][1] + h / 2;
        // z
        axes[2][0] = -d * rot[0][2] + d / 2;
        axes[2][1] = -d * rot[1][2] + d / 2;
        axes[2][2] = d * rot[0][2] + d / 2;
        axes[2][3] = d * rot[1][2] + d / 2;

        LinkedList<Box> octants = new LinkedList<Box>();
        LinkedList<Line2D.Double> lines = new LinkedList<Line2D.Double>();
        tree.octants(octants);
        for (Box box : octants) {
            for (double[] line : box.lines()) {
                lines.add(new Line2D.Double(
                    line[0] * rot[0][0] + line[1] * rot[0][1] + line[2] * rot[0][2],
                    line[0] * rot[1][0] + line[1] * rot[1][1] + line[2] * rot[1][2],
                    line[3] * rot[0][0] + line[4] * rot[0][1] + line[5] * rot[0][2],
                    line[3] * rot[1][0] + line[4] * rot[1][1] + line[5] * rot[1][2]
                ));
            }
        }

        JFrame frame = new JFrame("QuadTree");
        Canvas canvas = new Canvas() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g) {
                Graphics2D G = (Graphics2D) g;
                for (double[] line : axes) {
                    G.draw(new Line2D.Double(line[0], line[1], line[2], line[3]));
                }
                for (Line2D.Double line : lines) {
                    G.draw(line);
                    System.out.printf("%f %f %f %f\n", line.x1, line.y1, line.x2, line.y2);
                }
                for (int i = 0; i < N; i++) {
                    float alpha = (float) (pts[i].getZ() / d);
                    alpha = (alpha <= 1) ? alpha : 1;
                    G.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    G.fill(new Ellipse2D.Double(prj[i][0], prj[i][1], 4, 4));
                }
            }
        };
        canvas.setSize(w, h);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

    }
}
