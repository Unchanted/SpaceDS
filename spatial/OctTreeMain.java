package spatial;

import java.awt.Canvas;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.awt.geom.Line2D;
import java.util.stream.Collectors;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
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

    private final static int n = 10;

    private final static int N = 5000;

    private final static double dx = 10;
    private final static double dy = 10;
    private final static double dz = 10;

    private final static int r = 5;

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

        Canvas canvas = new Canvas() {
            private static final long serialVersionUID = 2285823752059566895L;

            @Override
            public void paint(Graphics g) {
                Graphics2D G = (Graphics2D) g;
                if (drawPoints)
                    tree.traverse().map(e -> e.elements()).flatMap(Collection::stream).filter(e -> e instanceof Sphere)
                            .forEach(e -> {
                                Sphere s = (Sphere) e;
                                double[][] x = mmult(P, e.getPoint().homogenize());
                                G.draw(new Ellipse2D.Double(x[0][0] + w / 2, x[1][0] + h / 2, s.getR(), s.getR()));
                            });
                if (drawOctants)
                    tree.traverse().forEach(e -> {
                        if (drawEmptyOctants || !e.elements().isEmpty())
                            Arrays.stream(e.bounds().lines()).forEach(f -> {
                                double[][] x1 = mmult(P, f[0].homogenize()), x2 = mmult(P, f[1].homogenize());
                                G.draw(new Line2D.Double(x1[0][0] + w / 2, x1[1][0] + h / 2, x2[0][0] + w / 2, x2[1][0] + h / 2));
                            });
                    });
            }
        };
        canvas.setSize(w, h);
        canvas.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                boolean recalc = false;
                switch (e.getKeyCode()) {
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
                        canvas.repaint();
                        break;
                    case KeyEvent.VK_B:
                        drawOctants = !drawOctants;
                        canvas.repaint();
                        break;
                    case KeyEvent.VK_N:
                        drawPoints = !drawPoints;
                        canvas.repaint();
                        break;
                    case KeyEvent.VK_M:
                        tree.clear();
                        rwalk();
                        canvas.repaint();
                        break;

                }
                if (recalc) {
                    update();
                    canvas.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

        });

        JFrame frame = new JFrame(":D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }
}
