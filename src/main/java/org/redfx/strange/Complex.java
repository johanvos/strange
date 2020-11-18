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
import org.redfx.strange.local.Computations;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
//import org.nd4j.linalg.api.buffer.DataType;
//import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.linalg.cpu.nativecpu.NDArray;
//import org.nd4j.linalg.factory.Nd4j;

public final class Complex {

    public static final Complex ZERO = new Complex(0.d, 0.d);
    public static final Complex ONE = new Complex(1.d, 0.d);
    public static final Complex I = new Complex(0.d, 1.d);

    private static final double HV = 1. / Math.sqrt(2.);

    public static final Complex HC = new Complex(HV, 0.d);
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

    public Complex add(Complex b) {
        double nr = this.r + b.r;
        double ni = this.i + b.i;
        return new Complex(nr, ni);
    }

    /**
     * Add and replace
     *
     * @param b
     * @return
     */
    public Complex addr(Complex b) {
        this.r = this.r + b.r;
        this.i = this.i + b.i;
        return this;
    }

    /**
     * AddMul and replace
     *
     * @param b
     * @return
     */
    public Complex addmulr(Complex a, Complex b) {
        double nr = (a.r * b.r) - (a.i * b.i);
        double ni = (a.r * b.i) + (a.i * b.r);
        this.r = (float) (this.r + nr);
        this.i = (float) (this.i + ni);
        return this;
    }

    public Complex min(Complex b) {
        double nr = this.r - b.r;
        double ni = this.i - b.i;
        return new Complex(nr, ni);
    }

    public Complex mul(Complex b) {
        double nr = (this.r * b.r) - (this.i * b.i);
        double ni = (this.r * b.i) + (this.i * b.r);
        return new Complex(nr, ni);
    }

    public Complex mul(double b) {
        return new Complex(this.r * b, this.i * b);
    }

    public double abssqr() {
        return (this.r * this.r + this.i * this.i);
    }

    /**
     * return an identity matrix 
     * @param dim 
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
     * @param a
     * @param b
     * @return
     */
    public static Complex[][] tensor(Complex[][] a, Complex[][] b) {
        Complex.printMatrix(a);
        Complex.printMatrix(b);
        int d1 = a.length;
        int d2 = b.length;
       // System.err.println("tensor for "+d1+" and "+d2+", new dim will be "+(d1*d2));
        //Computations.printMemory();
        Complex[][] result = new Complex[d1 * d2][d1 * d2];
        //System.err.println("allocated memory");
// Computations.printMemory();
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
        //System.err.println("tensor created new matrix");
        //Computations.printMemory();

        return result;
    }
    static int zCount = 0;
    static int nzCount = 0;

    public static Complex[][] mmul(Complex[][] a, Complex[][] b) {
        return slowmmul (a, b);
    }
    
    public static Complex[][] slowmmul(Complex[][] a, Complex[][] b) {
     //   System.err.println("slowmul");
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
        
    private static Complex[][] fastmmul(Complex[][] a, Complex[][] b) {
        /*
        long l0 = System.currentTimeMillis();
        int arow = a.length;
        int acol = a[0].length;
        int brow = b.length;
        int bcol = b[0].length;
        int am = 0;
        if (acol != brow) {
            throw new RuntimeException("#cols a " + acol + " != #rows b " + brow);
        }
        Complex[][] answer = new Complex[arow][bcol];
        double[][] ar = new double[arow][acol];
        double[][] ai = new double[arow][acol];
        double[][] br = new double[brow][bcol];
        double[][] bi = new double[brow][bcol];
        dbg("start for loop");
        for (int i = 0; i < arow; i++) {
            for (int j = 0; j < bcol; j++) {
                Complex el = new Complex(0., 0.);
                double newr = 0;
                double newi = 0;
                boolean zero = true;
                for (int k = 0; k < acol; k++) {
                    if (j == 0) {
                        ar[i][k] = a[i][k].r;
                        ai[i][k] = a[i][k].i;
                    }
                    if (i == 0) {
                        br[k][j] = b[k][j].r;
                        bi[k][j] = b[k][j].i;
                    }
                }
                if (zero) {
                    answer[i][j] = Complex.ZERO;
                    zCount++;
                } else {
                    answer[i][j] = Complex.ZERO;
                    nzCount++;
                }

                //      System.err.println("ANSWER["+i+"]["+j+"] = "+answer[i][j]);

            }
        }
        //     if (1 < 2) System.exit(0);
        long l1 = System.currentTimeMillis();
        dbg("mulitply matrix " + arow + ", " + acol + ", " + bcol + " took " + (l1 - l0) + " zc = " + zCount + " and nzc = " + nzCount + " and am = " + am);
        INDArray n_ar = Nd4j.create(ar);
        INDArray n_ai = Nd4j.create(ai);
        INDArray n_br = Nd4j.create(br);
        INDArray n_bi = Nd4j.create(bi);
        INDArray n_r = n_ar.mmul(n_br).sub(n_ai.mmul(n_bi));
        INDArray n_i = n_ai.mmul(n_br).add(n_ar.mmul(n_bi));
//        System.err.println("ar = "+n_ar);
//        System.err.println("br = "+n_br);
//        System.err.println("nr = "+n_r);
//        System.err.println("ni = "+n_i);
        for (int i = 0; i < acol; i++) {
            for (int j = 0; j < brow; j++) {
                answer[i][j] = new Complex(n_r.getDouble(i, j), n_i.getDouble(i, j));
//                                System.err.println("NDANSWER["+i+"]["+j+"] = "+answer[i][j]);

            }
        }
        return answer;
*/
        throw new RuntimeException ("Fast multiplication not available");
    }

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

    public static void printArray(Complex[] ca) {
        if (debug) {
            printArray(ca, System.err);
        }
    }

    public static void printArray(Complex[] ca, PrintStream ps) {
        ps.println("complex[" + ca.length + "]: ");
        for (Complex c : ca) {
            ps.println("-> " + c);
        }
    }

    public static void printMatrix(Complex[][] cm) {
        printMatrix(cm, System.err);
    }

    static final boolean debug = false;
    
    public static void dbg (String s) {
        if (debug) {
        System.err.println("[DBG] " + System.currentTimeMillis()%1000000+": "+s);
        }
    }


    public static void printMatrix(Complex[][] cm, PrintStream ps) {
        if (!debug) {
            return;
        }
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

 
    @Override
    public String toString() {
        double mr = this.r;
        double mi = this.i;
        if (Math.abs(mr) < 1e-7) mr = 0;
        if (Math.abs(mi) < 1e-7) mi = 0;
        if (Math.abs(mr) > .999999) mr = 1;
        if (Math.abs(mi) > .999999) mi = 1;
        return "(" + mr + ", " + mi + ")";
    }

}
