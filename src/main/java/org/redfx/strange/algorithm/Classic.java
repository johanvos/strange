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
package org.redfx.strange.algorithm;

import org.redfx.strange.*;
import org.redfx.strange.gate.*;
import org.redfx.strange.local.Computations;
import org.redfx.strange.local.SimpleQuantumExecutionEnvironment;

import java.util.List;
import java.util.function.Function;

/**
 * <p>Classic class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class Classic {
    
    private static QuantumExecutionEnvironment qee = new SimpleQuantumExecutionEnvironment();
            
    /**
     * <p>setQuantumExecutionEnvironment.</p>
     *
     * @param val a {@link org.redfx.strange.QuantumExecutionEnvironment} object
     */
    public static void setQuantumExecutionEnvironment(QuantumExecutionEnvironment val) {
        qee = val;
    }
    
    /**
     * <p>randomBit.</p>
     *
     * @return a int
     */
    public static int randomBit() {
        Program program = new Program(1, new Step(new Hadamard(0)));
        Result result = qee.runProgram(program);
        Qubit[] qubits = result.getQubits();
        int answer = qubits[0].measure();
        return answer;
    }
    
    /**
     * Use a quantum addition algorithm to compute the sum of a and b
     *
     * @param a first term
     * @param b second term
     * @return the sum of a and b
     */
    public static int qsum(int a, int b) {
        int y = a > b ? a : b;
        int x = a > b ? b : a;
        int m = y < 2? 1 : 1 + (int) Math.ceil(Math.log(y)/Math.log(2));
        int n = x < 2? 1 : 1 + (int) Math.ceil(Math.log(x)/Math.log(2));
        Program program = new Program(m + n);
        Step prep = new Step();
        int y0 = y;
        for (int i = 0; i < m; i++) {
            int p = 1 << (m-i-1);
            if (y0 >= p) {
                prep.addGate(new X(m-i-1));
                y0 = y0-p;
            }
        }
        int x0 = x;
        for (int i = 0; i < n; i++) {
            int p = 1 << (n-i-1);
            if (x0 >= p) {
                prep.addGate(new X(m+n-i-1));
                x0 = x0-p;
            }   
        }
        program.addStep(prep);
        program.addStep(new Step(new Fourier(m, 0)));
        for (int i = 0; i < m; i++) {
           for (int j = 0; j < m-i ; j++) {
                int cr0 = 2 *m-j-i-1;
                if (cr0 < m + n) {
                    Step s = new Step(new Cr(i, cr0,2,1+j));
                    program.addStep(s);
                }
            }
        }
        program.addStep(new Step(new InvFourier(m, 0)));
        Result res = qee.runProgram(program);
        Qubit[] qubits = res.getQubits();
        int answer = 0;
        for (int i = 0; i < m; i++) {
            if (qubits[i].measure() == 1) {
                answer = answer + (1<<i);
            }
        }
        return answer;
    }
    
    /**
     * Apply Grover's search algorithm to find the element from the supplied
     * list that would evaluate the provided function to 1
     *
     * @param <T> the type of the element
     * @param list the list of all elements that need to be searched into
     * @param function the function that, when evaluated, returns 0 for all
     * elements except for the element that this method returns, which evaluation
     * leads to 1.
     * @return the single element from the provided list that, when evaluated
     * by the function, returns 1.
     */
    public static<T> T search(List<T> list, Function<T, Integer> function) {
        double[] probability = searchProbabilities(list, function);
        int winner = 0;
        double wv = 0;
        for (int i = 0 ; i < probability.length; i++) {
            double a = probability[i];
            if (a > wv) {
                wv = a;
                winner = i;
            }
        }
        System.err.println("winner = "+winner+" with prob "+wv);
        return list.get(winner);
    }
    
      
    /**
     * Apply Grover's search algorithm to find the element from the supplied
     * list that would evaluate the provided function to 1
     *
     * @param <T> the type of the element
     * @param list the list of all elements that need to be searched into
     * @param function the function that, when evaluated, returns 0 for all
     * elements except for the element that this method returns, which evaluation
     * leads to 1.
     * @return the single element from the provided list that, when evaluated
     * by the function, returns 1.
     */
    public static<T> double[] searchProbabilities(List<T> list, Function<T, Integer> function) {
        int size = list.size();
        int n = (int) Math.ceil((Math.log(size)/Math.log(2)));
        int N = 1 << n;
        double cnt = Math.PI*Math.sqrt(N)/4;

        Oracle oracle = createGroverOracle(n, list, function);
        Program p = new Program(n);
        Step s0 = new Step();
        for (int i = 0; i < n; i++) {
            s0.addGate(new Hadamard(i));
        }
        p.addStep(s0);
        oracle.setCaption("O");
        Complex[][] dif = createDiffMatrix(n);
        Oracle difOracle = new Oracle(dif);
        difOracle.setCaption("D");
        for (int i = 1; i < cnt; i++) {
            Step s1 = new Step("Oracle "+i);
            s1.addGate(oracle);
            Step s2 = new Step("Diffusion "+i);
            s2.addGate(difOracle);
            Step s3 = new Step("Prob "+i);
            s3.addGate(new ProbabilitiesGate(0));
            p.addStep(s1);
            p.addStep(s2);
            p.addStep(s3);
        }
        System.out.println(" n = "+n+", steps = "+cnt);

        Result res = qee.runProgram(p);
        Complex[] probability = res.getProbability();
        double[] answer = new double[probability.length];
        for (int i = 0; i < probability.length; i++) {
            answer[i] = probability[i].abssqr();
        }
        return answer;
      
    }

    /**
     * Find the periodicity of a^x mod N
     *
     * @param a a int
     * @param mod N
     * @return period r or -1 if no period is found
     */
    public static int findPeriod(int a, int mod) {
        int maxtries = 2;
        int tries = 0;
        int p = 0;
        while ((p == 0) && tries < maxtries) {
            p = measurePeriod(a, mod);
            System.err.println("p = "+p);
            if (p ==0) {
                System.err.println("We measured a periodicity of 0, and have to start over.");
            }
        }
        if (p ==0) return -1;
        int period = Computations.fraction(p, mod);
        return period;
    }
    
    /**
     * <p>qfactor.</p>
     *
     * @param N a int
     * @return a int
     */
    public static int qfactor (int N) {
        System.out.println("We need to factor "+N);
        int a = 1+ (int)((N-1) * Math.random());
        System.out.println("Pick a random number a, a < N: "+a);
        int gcdan = Computations.gcd(N,a);
        System.out.println("calculate gcd(a, N):"+ gcdan); 
        if (gcdan != 1) return gcdan;
        int p = findPeriod (a, N); 
        if (p == -1) {
            System.err.println("After too many tries with " + a+", we need to pick a new random number.");
            return qfactor(N);

        }
        System.out.println("period of f = "+p);
        if (p%2 == 1) { 
            System.out.println("bummer, odd period, restart.");
            return qfactor(N);
        }
        int md = (int)(Math.pow(a, p/2) +1);
        int m2 = md%N; 
        if (m2 == 0) { 
            System.out.println("bummer, m^p/2 + 1 = 0 mod N, restart");
            return qfactor(N);
        }
        int f2 = (int)Math.pow(a, p/2) -1;
        int factor = Computations.gcd(N, f2);
        return factor;
    }
    
    private static int measurePeriod(int a, int mod) {         
        int length = (int) Math.ceil(Math.log(mod) / Math.log(2));
        int offset = 2 * length;
        System.err.println("create with #qubits = "+length+" or "+ (2*length +2+offset));
        Program p = new Program(2 * length + 2 + offset);
        Step prep = new Step();
        for (int i = 0; i < offset; i++) {
            prep.addGate(new Hadamard(i));
        }
        Step prepAnc = new Step(new X(offset));
        p.addStep(prep);
        p.addStep(prepAnc);
        for (int i = length - 1; i > length - 1 - offset; i--) {
            int m = 1;
            for (int j = 0; j < 1 << i; j++) {
                m = m * a % mod;
            }
            MulModulus mul = new MulModulus(length, 2 * length-1, m, mod);
            ControlledBlockGate cbg = new ControlledBlockGate(mul, offset, i);
            p.addStep(new Step(cbg));
        }
        p.addStep(new Step(new InvFourier(offset, 0)));
        Result result = qee.runProgram(p);
        Qubit[] q = result.getQubits();
        Complex[] probs = result.getProbability();
        Complex.printArray(probs);
        int answer = 0;
        for (int i = 0; i < offset; i++) {
            answer = answer + q[i].measure()*(1<< i);
        }
        return answer;
    }
          
    private static<T> Oracle createGroverOracle(int n, List<T> list, Function<T, Integer> function) {
        int N = 1 << n;
        int listSize = list.size();
        Complex[][] matrix = new Complex[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrix[i][j] = (i!=j) ? Complex.ZERO : 
                        ((i >= listSize) || function.apply(list.get(i)) == 0 )? 
                        Complex.ONE : Complex.ONE.mul(-1);
            }
        }
        return new Oracle(matrix);
    }

    private static Complex[][] createDiffMatrix(int n) {
        int N = 1<<n;
        Gate g = new Hadamard(0);
        Complex[][] matrix = g.getMatrix();
        Complex[][] h2 = matrix;
        for (int i = 1; i < n; i++) {
            h2 = Complex.tensor(h2, matrix);
        }
        Complex[][] I2 = new Complex[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j <N;j++) {
                if (i!=j) {
                    I2[i][j] = Complex.ZERO;
                } else {
                    I2[i][j] = Complex.ONE;
                }
            }
        }
        I2[0][0] = Complex.ONE.mul(-1);
        int nd = n<<1;

        Complex[][] inter1 = Complex.mmul(h2,I2);
        Complex[][] dif = Complex.mmul(inter1, h2);
        return dif;
    }

}
