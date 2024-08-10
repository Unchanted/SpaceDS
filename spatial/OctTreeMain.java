package spatial;

import java.awt.Canvas;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.awt.geom.Line2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

class OctTreeMain {

    private static final int W = 1000, H = 1000, D = 1000;
    private static final double RAD = Math.PI / 180;
    private static final int N = 5000, RADIUS = 5, N_POINTS = 10;
    private static final double DX = 10, DY = 10, DZ = 10;

    private static double theta = 0, psi = 0, phi = 0;
    private static double[][] K = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
    private static double[][] R = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
    private static double[] C = { 0, 0, 0 };
    private static double[][] P = new double[3][4];

    private static boolean drawPoints = true;
    private static boolean drawOctants = true;
    private static boolean drawEmptyOctants = true;

    private static OctTree tree = new OctTree(new Box(-W / 2, -H / 2, -D / 2, W, H, D), N_POINTS);

    public static void randomWalk() {
        Random prng = new Random();
        double[] pos = {
            prng.nextDouble() * W - W / 2,
            prng.nextDouble() * H - H / 2,
            prng.nextDouble() * D - D / 2
        };

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < 3; j++) {
                pos[j] += (prng.nextDouble() * (j == 0 ? DX : (j == 1 ? DY : DZ)) + 1) * (prng.nextBoolean() ? 1 : -1);
                pos[j] = Math.max(pos[j], -((j == 0 ? W : (j == 1 ? H : D)) / 2));
                pos[j] = Math.min(pos[j], (j == 0 ? W : (j == 1 ? H : D)) / 2);
            }
            tree.insert(new Sphere(pos[0], pos[1], pos[2], RADIUS));
        }
    }

    public static String matrixToString(double[][] matrix, CharSequence delimiter) {
        return Arrays.stream(matrix)
                     .map(Arrays::toString)
                     .collect(Collectors.joining(delimiter));
    }

    public static void clearMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            Arrays.fill(row, 0);
        }
    }

    public static double[][] transpose(double[] vector) {
        double[][] result = new double[vector.length][1];
        for (int i = 0; i < vector.length; i++) {
            result[i][0] = vector[i];
        }
        return result;
    }

    public static double[][] multiplyMatrices(double[][] A, double[][] B) {
        int m = A.length, n = A[0].length, p = B[0].length;
        double[][] result = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }

    public static void calculateRotation(double theta, double psi, double phi, double[][] R) {
        double sinTheta = Math.sin(theta), cosTheta = Math.cos(theta);
        double sinPsi = Math.sin(psi), cosPsi = Math.cos(psi);
        double sinPhi = Math.sin(phi), cosPhi = Math.cos(phi);

        R[0][0] = cosTheta * cosPsi;
        R[0][1] = cosTheta * sinPsi * sinPhi - sinTheta * cosPhi;
        R[0][2] = cosTheta * sinPsi * cosPhi + sinTheta * sinPhi;
        R[1][0] = sinTheta * cosPsi;
        R[1][1] = sinTheta * sinPsi * sinPhi + cosTheta * cosPhi;
        R[1][2] = sinTheta * sinPsi * cosPhi - cosTheta * sinPhi;
        R[2][0] = -sinPsi;
        R[2][1] = cosPsi * sinPhi;
        R[2][2] = cosPsi * cosPhi;
    }

    public static void calculateProjection(double[][] K, double[][] R, double[] C, double[][] P) {
        double[][] KR = multiplyMatrices(K, R);
        double[][] IC = { { 1, 0, 0, -C[0] }, { 0, 1, 0, -C[1] }, { 0, 0, 1, -C[2] } };
        double[][] result = multiplyMatrices(KR, IC);
        System.arraycopy(result, 0, P, 0, P.length);
    }

    public static void update() {
        calculateRotation(theta, psi, phi, R);
        clearMatrix(P);
        calculateProjection(K, R, C, P);
    }

    public static void main(String[] args) {
        randomWalk();
        update();

        Canvas canvas = new Canvas() {
            private static final long serialVersionUID = 2285823752059566895L;

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                if (drawPoints) {
                    tree.traverse().flatMap(octant -> octant.elements().stream())
                        .filter(e -> e instanceof Sphere)
                        .forEach(e -> {
                            Sphere s = (Sphere) e;
                            double[][] pos = multiplyMatrices(P, s.getPoint().homogenize());
                            g2d.draw(new Ellipse2D.Double(pos[0][0] + W / 2, pos[1][0] + H / 2, s.getR(), s.getR()));
                        });
                }
                if (drawOctants) {
                    tree.traverse().forEach(octant -> {
                        if (drawEmptyOctants || !octant.elements().isEmpty()) {
                            Arrays.stream(octant.bounds().lines()).forEach(line -> {
                                double[][] start = multiplyMatrices(P, line[0].homogenize());
                                double[][] end = multiplyMatrices(P, line[1].homogenize());
                                g2d.draw(new Line2D.Double(start[0][0] + W / 2, start[1][0] + H / 2, end[0][0] + W / 2, end[1][0] + H / 2));
                            });
                        }
                    });
                }
            }
        };
    }
}
