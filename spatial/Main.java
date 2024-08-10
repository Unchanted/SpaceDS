package spatial;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Random;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import java.awt.AlphaComposite;

public class Main {

    public static void main(String[] args) {
        int N = 100;

        int w = 1000;
        int h = 1000;
        int d = 1000;
        int n = 2;

        Random prng = new Random();
        Sphere[] pts = new Sphere[N];
        for (int i = 0; i < N; i++) {
            pts[i] = new Sphere(prng.nextDouble() * w + 1, prng.nextDouble() * h + 1, prng.nextDouble() * d + 1, 4);
        }

        OctTree tree = new OctTree(new Box(0, 0, 0, w, h, d), n);
        System.out.println(tree);
        for (Sphere sphere : pts) {
            tree.insert(sphere);
        }

        tree.pprint(0, "root");
    }
}
