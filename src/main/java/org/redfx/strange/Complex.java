/*-
 * #%L
 * Strange
 * %%
 * Copyright (C) 2020 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strange;

import org.redfx.strange.gate.PermutationGate;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
//import org.nd4j.linalg.api.buffer.DataType;
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.cpu.nativecpu.NDArray;
//import org.nd4j.linalg.factory.Nd4j;

/**
 * <p>Complex class.</p>
 *
 * @author alain
 * @version $Id: $Id
 */
public final class Complex {

    /** Constant <code>ZERO</code> */
    public static final Complex ZERO = new Complex(0.d, 0.d);
    /** Constant <code>ONE</code> */
    public static final Complex ONE = new Complex(1.d, 0.d);
    /** Constant <code>I</code> */
    public static final Complex I = new Complex(0.d, 1.d);
    
    static final boolean DEBUG = false;

    private static final double HV = 1. / Math.sqrt(2.);

    /** Constant <code>HC</code> */
    public static final Complex HC = new Complex(HV, 0.d);
    /** Constant <code>HCN</code> */
    public static final Complex HCN = new Complex(-HV, 0.d);

    public float r;
    public float i;

    /**
     * Create a complex number with a real component only
     *
     * @param r the real component
     */
    public Complex(double r) {
        this(r, 0.d);
    }

    /**
     * Create a complex number with a real and imaginary component
     *
     * @param r the real component
     * @param i the imaginary component
     */
    public Complex(double r, double i) {
        this.r = (float) r;
        this.i = (float) i;
    }

    /**
     * <p>add.</p>
     *
     * @param b a {@link org.redfx.strange.Complex} object
     * @return a {@link org.redfx.strange.Complex} object
     */
    public Complex add(Complex b) {
        double nr = this.r + b.r;
        double ni = this.i + b.i;
        return new Complex(nr, ni);
    }

    /**
     * Add and replace
     *
     * @param b complex number to add
     * @return the modified complex number
     */
    public Complex addr(Complex b) {
        this.r = this.r + b.r;
        this.i = this.i + b.i;
        return this;
    }

    /**
     * AddMul and replace
     *
     * @param a a {@link org.redfx.strange.Complex} object
     * @param b a {@link org.redfx.strange.Complex} object
     * @return a {@link org.redfx.strange.Complex} object
     */
    public Complex addmulr(Complex a, Complex b) {
        double nr = (a.r * b.r) - (a.i * b.i);
        double ni = (a.r * b.i) + (a.i * b.r);
        this.r = (float) (this.r + nr);
        this.i = (float) (this.i + ni);
        return this;
    }

    /**
     * <p>min.</p>
     *
     * @param b a {@link org.redfx.strange.Complex} object
     * @return a {@link org.redfx.strange.Complex} object
     */
    public Complex min(Complex b) {
        double nr = this.r - b.r;
        double ni = this.i - b.i;
        return new Complex(nr, ni);
    }

    /**
     * <p>mul.</p>
     *
     * @param b a {@link org.redfx.strange.Complex} object
     * @return a {@link org.redfx.strange.Complex} object
     */
    public Complex mul(Complex b) {
        double nr = (this.r * b.r) - (this.i * b.i);
        double ni = (this.r * b.i) + (this.i * b.r);
        return new Complex(nr, ni);
    }

    /**
     * <p>mul.</p>
     *
     * @param b a double
     * @return a {@link org.redfx.strange.Complex} object
     */
    public Complex mul(double b) {
        return new Complex(this.r * b, this.i * b);
    }

    /**
     * <p>abssqr.</p>
     *
     * @return a double
     */
    public double abssqr() {
        return (this.r * this.r + this.i * this.i);
    }

    /**
     * return an identity matrix
     *
     * @param dim a int
     * @return  identity matrix
     */
    public static Complex[][] identityMatrix (int dim) {
        Complex[][] answer = new Complex[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i == j) {
                    answer[i][j] = Complex.ONE;
                } else {
                    answer[i][j] = Complex.ZERO;
                }
            }
                
        }
        return answer;
    }
    /**
     * Calculate the tensor product of matrix a and matrix b
     *
     * @param a an array of {@link org.redfx.strange.Complex} objects
     * @param b an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] tensor(Complex[][] a, Complex[][] b) {
        int d1 = a.length;
        int d2 = b.length;
        Complex[][] result = new Complex[d1 * d2][d1 * d2];
        for (int rowa = 0; rowa < d1; rowa++) {
            for (int cola = 0; cola < d1; cola++) {
                for (int rowb = 0; rowb < d2; rowb++) {
                    for (int colb = 0; colb < d2; colb++) {
                        if ((a[rowa][cola] == Complex.ZERO) || (b[rowb][colb] == Complex.ZERO)) {
                            result[d2 * rowa + rowb][d2 * cola + colb] = Complex.ZERO;
                        } else {
                            result[d2 * rowa + rowb][d2 * cola + colb] = a[rowa][cola].mul(b[rowb][colb]);
                        }
                    }
                }
            }
        }
        return result;
    }
    static int zCount = 0;
    static int nzCount = 0;

    /**
     * <p>mmul.</p>
     *
     * @param a an array of {@link org.redfx.strange.Complex} objects
     * @param b an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] mmul(Complex[][] a, Complex[][] b) {
        return slowmmul (a, b);
    }
    
    /**
     * <p>slowmmul.</p>
     *
     * @param a an array of {@link org.redfx.strange.Complex} objects
     * @param b an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] slowmmul(Complex[][] a, Complex[][] b) {
        int arow = a.length;
        int acol = a[0].length;
        int brow = b.length;
        int bcol = b[0].length;
        if (acol != brow) {
            throw new RuntimeException("#cols a " + acol + " != #rows b " + brow);
        }
        Complex[][] answer = new Complex[arow][bcol];
        for (int i = 0; i < arow; i++) {
            for (int j = 0; j < bcol; j++) {
                Complex el = Complex.ZERO;
                for (int k = 0; k < acol;k++) {
                    el = el.add(a[i][k].mul(b[k][j]));
                }
                answer[i][j] = el;
            }
        }

        return answer;
    }

    /**
     * <p>conjugateTranspose.</p>
     *
     * @param src an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] conjugateTranspose(Complex[][] src) {
        int d0 = src.length;
        int d1 = src[0].length;
        Complex[][] answer = new Complex[d1][d0];
        for (int i = 0; i < d0; i++) {
            for (int j = 0; j < d1; j++) {
                Complex c = src[i][j];
                answer[j][i] = new Complex(c.r, -1 * c.i);
            }
        }
        return answer;
    }

    /**
     * <p>permutate0.</p>
     *
     * @param matrix an array of {@link org.redfx.strange.Complex} objects
     * @param pg a {@link org.redfx.strange.gate.PermutationGate} object
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] permutate0(Complex[][] matrix, PermutationGate pg) {
        Complex[][] p = pg.getMatrix();
        int dim = p.length;
        Complex[][] answer = new Complex[dim][dim];
        for (int i = 0; i < dim; i++) {
            int idx = 0;
            while (p[i][idx].equals(Complex.ZERO)) {
                idx++;
            }
            for (int j = 0; j < dim; j++) {
                answer[i][j] = matrix[idx][j];
            }
        }
        return answer;
    }

    /**
     * <p>permutate.</p>
     *
     * @param pg a {@link org.redfx.strange.gate.PermutationGate} object
     * @param matrix an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] permutate(PermutationGate pg, Complex[][] matrix) {
        int a = pg.getIndex1();
        int b = pg.getIndex2();
        int amask = 1 << a;
        int bmask = 1 << b;
        int dim = matrix.length;
        Complex cp;
        List<Integer> swapped = new LinkedList<>();
        for (int i = 0; i < dim; i++) {
            int j = i;
            int x = (amask & i) / amask;
            int y = (bmask & i) / bmask;
            if (x != y) {
                j ^= amask;
                j ^= bmask;
                if (!swapped.contains(j)) {
                    swapped.add(j);
                    swapped.add(i);
                    for (int k = 0; k < dim; k++) {
                        cp = matrix[k][i];
                        matrix[k][i] = matrix[k][j];
                        matrix[k][j] = cp;
                    }
                }
            }
        }
        return matrix;
        
    }

    /**
     * <p>permutate.</p>
     *
     * @param matrix an array of {@link org.redfx.strange.Complex} objects
     * @param pg a {@link org.redfx.strange.gate.PermutationGate} object
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public static Complex[][] permutate(Complex[][] matrix, PermutationGate pg) {
        int a = pg.getIndex1();
        int b = pg.getIndex2();
        int amask = 1 << a;
        int bmask = 1 << b;
        int dim = matrix.length;
   //            System.err.println("permutate "+a+" and "+b+" on matrix sized "+dim);

        List<Integer> swapped = new LinkedList<>();
        for (int i = 0; i < dim; i++) {
            int j = i;
            int x = (amask & i) / amask;
            int y = (bmask & i) / bmask;
       //     System.err.println("x = " + x + ", y = " + y);
            if (x != y) {
                j ^= amask;
                j ^= bmask;

             //   System.err.println("need to swap rows " + i + " and " + j);
                if (swapped.contains(j)) {
             //       System.err.println("Already swapped those ");
                } else {
                    swapped.add(j);
                    swapped.add(i);
                    Complex[] rowa = matrix[i];
                    matrix[i] = matrix[j];
                    matrix[j] = rowa;
                }
            }
        }

        return matrix;
    }

    /**
     * <p>printArray.</p>
     *
     * @param ca an array of {@link org.redfx.strange.Complex} objects
     */
    public static void printArray(Complex[] ca) {
//        if (DEBUG) {
            printArray(ca, System.err);
//        }
    }

    /**
     * <p>printArray.</p>
     *
     * @param ca an array of {@link org.redfx.strange.Complex} objects
     * @param ps a {@link java.io.PrintStream} object
     */
    public static void printArray(Complex[] ca, PrintStream ps) {
        ps.println("complex[" + ca.length + "]: ");
        for (Complex c : ca) {
            ps.println("-> " + c);
        }
    }

    /**
     * <p>printMatrix.</p>
     *
     * @param cm an array of {@link org.redfx.strange.Complex} objects
     */
    public static void printMatrix(Complex[][] cm) {
        printMatrix(cm, System.err);
    }
    
    /**
     * <p>dbg.</p>
     *
     * @param s a {@link java.lang.String} object
     */
    public static void dbg (String s) {
        if (DEBUG) {
        System.err.println("[DBG] " + System.currentTimeMillis()%1000000+": "+s);
        }
    }


    /**
     * <p>printMatrix.</p>
     *
     * @param cm an array of {@link org.redfx.strange.Complex} objects
     * @param ps a {@link java.io.PrintStream} object
     */
    public static void printMatrix(Complex[][] cm, PrintStream ps) {
//        if (!DEBUG) {
//            return;
//        }
       // Thread.dumpStack();
        ps.println("complex[" + cm.length + "]: ");
        for (int idx = 0; idx < cm.length; idx++) {
            String row = "row " + idx;
            for (int jdx = 0; jdx < cm.length; jdx++) {
                Complex c = cm[idx][jdx];
                row = row + ":" + (c == null ? "NULL!!!!!!" : c.toString());
            }
            ps.println("-> " + row);
            //   idx++;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        float mr = this.r;
        float mi = this.i;
        if (Math.abs(mr) < 1e-7) mr = 0;
        if (Math.abs(mi) < 1e-7) mi = 0;
        if (Math.abs(mr) > .999999) mr = (mr > 0) ?1: -1;
        if (Math.abs(mi) > .999999) mi = (mi > 0) ?1: -1;
        return "(" + mr + ", " + mi + ")";
    }

}
