package testbed.mike.mahout;

import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.junit.Test;

import static org.junit.Assert.*;

public class RandomGaussianTest {

    @Test
    public void getMatrix() {
    }

    @Test
    public void mvMultiply() {
        RandomGaussian rg = new RandomGaussian(3,4,4327495392L);
        Vector result = new RandomAccessSparseVector(4);
        result.setQuick(0, 2);
        System.out.println(rg.getMatrix());
        System.out.println(rg.mvMultiply(result));
    }
}