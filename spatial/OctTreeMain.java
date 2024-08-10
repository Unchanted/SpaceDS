package spatial;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.awt.geom.Line2D;
import java.util.stream.Collectors;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.geom.Path2D;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class OctTreeMain {

    private final static int w = 1000, h = 1000, d = 1000;

    private static double θ = 0 * Math.PI / 180, ψ = 0 * Math.PI / 180, φ = 0 * Math.PI / 180;

    private static double[][] K = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
    private static double[][] R = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
    private static double[] C = { 0, 0, 0 };
    private static double[][] P = new double[3][4];

    private final static double RAD = 1 * Math.PI / 180;
    private final static double PI2 = 2 * Math.PI;

    private final static int n = 100;

    private final static int N = 100000;

    private final static double dx = 0.5;
    private final static double dy = 0.5;
    private final static double dz = 0.5;

    private final static int r = 2;

    private static boolean drawPoints = true;
    private static boolean drawOctants = true;
    private static boolean drawEmptyOctants = true;

    private static OctTree tree = new OctTree(new Box(-w / 2, -h / 2, -d / 2, w, h, d), n);

    public static void rwalk() {
        Random prng = new Random();
        double w1 = -w / 2, w2 = w / 2, h1 = -h / 2, h2 = h / 2, d1 = -d / 2, d2 = d / 2;
        double x = prng.nextDouble() * (w2 - w1) + w1;
        double y = prng.nextDouble() * (h2 - h1) + h1;
        double z = prng.nextDouble() * (d2 - d1) + d1;
        for (int i = 0; i < N; i++) {
            x += (prng.nextDouble() * dx + 1) * (prng.nextBoolean() ? 1 : -1);
            y += (prng.nextDouble() * dy + 1) * (prng.nextBoolean() ? 1 : -1);
            z += (prng.nextDouble() * dz + 1) * (prng.nextBoolean() ? 1 : -1);
            x = x < w1 ? w1 : x;
            y = y < h1 ? h1 : y;
            z = z < d1 ? d1 : z;
            x = x > w2 ? w2 : x;
            y = y > h2 ? h2 : y;
            z = z > d2 ? d2 : z;
            tree.insert(new Sphere(x, y, z, r));
        }
    }

    public static String mstr(double[][] M, CharSequence d) {
        return Arrays.stream(M).map(row -> Arrays.toString(row)).collect(Collectors.joining(d));
    }

    public static void mclr(double[][] M) {
        for (int i = 0; i < M.length; i++)
            for (int j = 0; j < M[0].length; j++)
                M[i][j] = 0;
    }

    public static double[][] mxpos(double[] M) {
        double[][] X = new double[M.length][1];
        for (int i = 0; i < M.length; i++)
            X[i][0] = M[i];
        return X;
    }

    public static void mmult(double[][] A, double[][] B, double[][] C) {
        int m = A.length, n = A[0].length, p = B[0].length;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < p; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
    }

    public static double[][] mmult(double[][] A, double[][] B) {
        int m = A.length, n = A[0].length, p = B[0].length;
        double[][] C = new double[m][p];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < p; j++)
                for (int k = 0; k < n; k++)
                    C[i][j] += A[i][k] * B[k][j];
        return C;
    }

    public static void calcR(double θ, double ψ, double φ, double[][] R) {
        double sinθ = Math.sin(θ), sinψ = Math.sin(ψ), sinφ = Math.sin(φ);
        double cosθ = Math.cos(θ), cosψ = Math.cos(ψ), cosφ = Math.cos(φ);
        R[0][0] = cosθ * cosψ;
        R[0][1] = cosθ * sinψ * sinφ - sinθ * cosφ;
        R[0][2] = cosθ * sinψ * cosφ + sinθ * sinφ;
        R[1][0] = sinθ * cosψ;
        R[1][1] = sinθ * sinψ * sinφ + cosθ * cosφ;
        R[1][2] = sinθ * sinψ * cosφ - cosθ * sinφ;
        R[2][0] = -sinψ;
        R[2][1] = cosψ * sinφ;
        R[2][2] = cosψ * cosφ;
    }

    public static void calcP(double[][] K, double[][] R, double[] C, double[][] P) {
        // P = KR[I|-C]
        double[][] KR = new double[3][3];
        double[][] IC = { { 1, 0, 0, -C[0] }, { 0, 1, 0, -C[1] }, { 0, 0, 1, -C[2] } };
        mmult(K, R, KR);
        mmult(KR, IC, P);
    }

    public static void update() {
        calcR(θ, ψ, φ, R);
        mclr(P);
        calcP(K, R, C, P);
    }

    public static void main(String[] args) {
        rwalk();
        update();

        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 2285823752059566895L;

            @Override
            public void paint(Graphics g) {
                Graphics2D G = (Graphics2D) g;
                // points
                if (drawPoints)
                    tree.traverse().parallel().map(OctTree::elements).flatMap(Collection::stream)
                            .filter(Sphere.class::isInstance).map(Sphere.class::cast).forEach(e -> {
                                double[][] x = mmult(P, e.getPoint().homogenize());
                                G.draw(new Ellipse2D.Double(x[0][0] + w / 2, x[1][0] + h / 2, e.getR(), e.getR()));
                            });
                // octants
                G.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.015f));
                if (drawOctants)
                    tree.traverse().parallel().forEach(e -> {
                        Path2D.Double path = new Path2D.Double();
                        for (Point3D[] pts : e.bounds().facePaths()) {
                            double[][] p = new double[3][1];
                            mmult(P, pts[0].homogenize(), p);
                            path.moveTo(p[0][0] + w / 2, p[1][0] + h / 2);
                            for (int i = 1; i < pts.length; i++) {
                                mclr(p);
                                mmult(P, pts[i].homogenize(), p);
                                path.lineTo(p[0][0] + w / 2, p[1][0] + h / 2);
                            }
                            path.closePath();
                            G.fill(path);
                            G.draw(path);
                            path.reset();
                        }
                    });
            }
        };
        panel.setDoubleBuffered(true);
        panel.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                boolean recalc = false;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C:
                        θ = ψ = φ = 0;
                        mclr(K);
                        K[0][0] = K[1][1] = K[2][2] = 1;
                        C[0] = C[1] = C[2] = 0;
                        recalc = true;
                        break;
                    case KeyEvent.VK_Z:
                        K[0][0] += 1;
                        K[1][1] += 1;
                        recalc = true;
                        break;
                    case KeyEvent.VK_X:
                        K[0][0] -= 1;
                        K[1][1] -= 1;
                        recalc = true;
                        break;
                    case KeyEvent.VK_A:
                        C[0] += 10;
                        recalc = true;
                        break;
                    case KeyEvent.VK_D:
                        C[0] -= 10;
                        recalc = true;
                        break;
                    case KeyEvent.VK_W:
                        C[1] += 10;
                        recalc = true;
                        break;
                    case KeyEvent.VK_S:
                        C[1] -= 10;
                        recalc = true;
                        break;
                    case KeyEvent.VK_Q:
                        C[2] += 10;
                        recalc = true;
                        break;
                    case KeyEvent.VK_E:
                        C[2] -= 10;
                        recalc = true;
                        break;
                    case KeyEvent.VK_U:
                        θ = (θ + 2 * RAD) % PI2;
                        recalc = true;
                        break;
                    case KeyEvent.VK_O:
                        θ = (θ - 2 * RAD) % PI2;
                        recalc = true;
                        break;
                    case KeyEvent.VK_J:
                        ψ = (ψ + 2 * RAD) % PI2;
                        recalc = true;
                        break;
                    case KeyEvent.VK_L:
                        ψ = (ψ - 2 * RAD) % PI2;
                        recalc = true;
                        break;
                    case KeyEvent.VK_I:
                        φ = (φ + 2 * RAD) % PI2;
                        recalc = true;
                        break;
                    case KeyEvent.VK_K:
                        φ = (φ - 2 * RAD) % PI2;
                        recalc = true;
                        break;
                    case KeyEvent.VK_V:
                        drawEmptyOctants = !drawEmptyOctants;
                        panel.repaint();
                        break;
                    case KeyEvent.VK_B:
                        drawOctants = !drawOctants;
                        panel.repaint();
                        break;
                    case KeyEvent.VK_N:
                        drawPoints = !drawPoints;
                        panel.repaint();
                        break;
                    case KeyEvent.VK_M:
                        tree.clear();
                        rwalk();
                        panel.repaint();
                        break;

                }
                if (recalc) {
                    update();
                    panel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

        });
        panel.setFocusable(true);
        panel.requestFocusInWindow();

        JFrame frame = new JFrame(":D");
        frame.setSize(w, h);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.add(panel);
        frame.setVisible(true);
    }
}
