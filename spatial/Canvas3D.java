package spatial;

import java.awt.Canvas;
import java.util.LinkedList;
import java.util.List;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

public class Canvas3D extends Canvas {

    private static final long serialVersionUID = 5907342914317114159L;

    private double width;
    private double height;
    private double depth;
    private double xrot;
    private double yrot;
    private double zrot;

    private double[][] axes = new double[3][6];
    private double[][] ang = new double[2][3];
    private double[][] rot = new double[3][3];

    public Canvas3D() {

    }

    public Canvas3D(double width, double height, double depth, double xrot, double yrot, double zrot) {
        this.setDimensions(width, height, depth);
        this.setPerspective(xrot, yrot, zrot);
    }

    public void setDimensions(double width, double height, double depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.calc_axes();
    }

    public void setPerspective(double xrot, double yrot, double zrot) {
        this.xrot = xrot;
        this.yrot = yrot;
        this.zrot = zrot;
        this.calc_rotation_matrix();
    }

    public double[] getPerspective() {
        return new double[] { this.xrot, this.yrot, this.zrot };
    }

    public double[][] getAxes() {
        return this.axes;
    }

    public void calc_axes() {
        axes[0][0] = 0;
        axes[0][1] = this.height / 2;
        axes[0][2] = this.depth / 2;
        axes[0][3] = this.width;
        axes[0][4] = this.height / 2;
        axes[0][5] = this.depth / 2;
        axes[1][0] = this.width / 2;
        axes[1][1] = 0;
        axes[1][2] = this.depth / 2;
        axes[1][3] = this.width / 2;
        axes[1][4] = this.height;
        axes[1][5] = this.depth / 2;
        axes[2][0] = this.width / 2;
        axes[2][1] = this.height / 2;
        axes[2][2] = 0;
        axes[2][3] = this.width / 2;
        axes[2][4] = this.height / 2;
        axes[2][5] = this.depth;
    }

    public void calc_rotation_matrix() {
        this.ang[0][0] = Math.sin(this.xrot);
        this.ang[0][1] = Math.sin(this.yrot);
        this.ang[0][2] = Math.sin(this.zrot);
        this.ang[1][0] = Math.cos(this.xrot);
        this.ang[1][1] = Math.cos(this.yrot);
        this.ang[1][2] = Math.cos(this.zrot);

        this.rot[0][0] = this.ang[1][1] * this.ang[1][2];
        this.rot[0][1] = -this.ang[0][2] * this.ang[1][1];
        this.rot[0][2] = this.ang[0][1];
        this.rot[1][0] = this.ang[1][2] * -this.ang[0][1] * -this.ang[0][0] + this.ang[0][2] * this.ang[1][0];
        this.rot[1][1] = -this.ang[0][2] * -this.ang[0][1] * -this.ang[0][0] + this.ang[1][2] * this.ang[1][0];
        this.rot[1][2] = this.ang[1][1] * -this.ang[0][0];
        this.rot[2][0] = this.ang[1][2] * -this.ang[0][1] * this.ang[1][0] + this.ang[0][2] * this.ang[0][0];
        this.rot[2][1] = -this.ang[0][2] * -this.ang[0][1] * this.ang[1][0] + this.ang[1][2] * this.ang[0][0];
        this.rot[2][2] = this.ang[1][1] * this.ang[1][0];
    }

    public LinkedList<Point2D.Double> project_points(List<Point3D> pts) {
        LinkedList<Point2D.Double> prj = new LinkedList<Point2D.Double>();
        for (Point3D p : pts)
            prj.add(new Point2D.Double(p.x * this.rot[0][0] + p.y * this.rot[0][1] + p.z * this.rot[0][2],
                    p.x * this.rot[1][0] + p.y * this.rot[1][1] + p.z * this.rot[1][2]));

        return prj;
    }

    public LinkedList<Line2D.Double> project_lines(double[][] lines) {
        LinkedList<Line2D.Double> prj = new LinkedList<Line2D.Double>();
        for (double[] line : lines)
            prj.add(new Line2D.Double(line[0] * this.rot[0][0] + line[1] * this.rot[0][1] + line[2] * this.rot[0][2],
                    line[0] * this.rot[1][0] + line[1] * this.rot[1][1] + line[2] * this.rot[1][2],
                    line[3] * this.rot[0][0] + line[4] * this.rot[0][1] + line[5] * this.rot[0][2],
                    line[3] * this.rot[1][0] + line[4] * this.rot[1][1] + line[5] * this.rot[1][2]));

        return prj;
    }
}