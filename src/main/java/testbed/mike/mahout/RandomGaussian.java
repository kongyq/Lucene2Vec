package testbed.mike.mahout;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.mahout.math.Matrices;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.Vector;

public class RandomGaussian {
    private final int row;
    private final int column;
    private final Matrix rgMatrix;

    private Int2ObjectMap<IntArrayList> randomGaussianMatrix;

    public RandomGaussian(int row, int column, long seed) {
        this.column = column;
        this.row = row;
        this.rgMatrix = Matrices.functionalMatrixView(row, column, Matrices.gaussianGenerator(seed));
    }

    public Matrix getMatrix() {
        return this.rgMatrix;
    }

    public Vector mvMultiply(Vector vector){
        return this.rgMatrix.times(vector);
    }
}
