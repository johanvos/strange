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
package org.redfx.strange.local;

import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;
import static org.redfx.strange.Complex.tensor;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Gate;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Identity;
import org.redfx.strange.gate.Oracle;
import org.redfx.strange.gate.PermutationGate;
import org.redfx.strange.gate.ProbabilitiesGate;
import org.redfx.strange.gate.SingleQubitGate;
import org.redfx.strange.gate.ThreeQubitGate;
import org.redfx.strange.gate.TwoQubitGate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.redfx.strange.QuantumExecutionEnvironment;

/**
 *
 * @author johan
 */
public class Computations {

    private static final boolean debug = false;

    static void dbg(String s) {
        if (debug) {
            System.err.println("[DBG] " + System.currentTimeMillis() % 100000 + ": " + s);
        }
    }

    public static Complex[][] calculateStepMatrix(List<Gate> gates, int nQubits, QuantumExecutionEnvironment qee) {
        long l0 = System.currentTimeMillis();
        Complex[][] a = new Complex[1][1];
        a[0][0] = Complex.ONE;
        int idx = nQubits - 1;
        while (idx >= 0) {
            final int cnt = idx;
            Gate myGate = gates.stream()
                    .filter(
                            // gate -> gate.getAffectedQubitIndex().contains(cnt)
                            gate -> gate.getHighestAffectedQubitIndex() == cnt)
                    .findFirst()
                    .orElse(new Identity(idx));
            dbg("stepmatrix, cnt = " + cnt + ", idx = " + idx + ", myGate = " + myGate);
            if (myGate instanceof BlockGate) {
                dbg("calculateStepMatrix for blockgate " + myGate + " of class " + myGate.getClass());
                BlockGate sqg = (BlockGate) myGate;
                a = tensor(a, sqg.getMatrix(qee));
                dbg("calculateStepMatrix for blockgate calculated " + myGate);

                idx = idx - sqg.getSize() + 1;
            }
            if (myGate instanceof SingleQubitGate) {
                SingleQubitGate sqg = (SingleQubitGate) myGate;
                a = tensor(a, sqg.getMatrix());
            }
            if (myGate instanceof TwoQubitGate) {
                TwoQubitGate tqg = (TwoQubitGate) myGate;
                a = tensor(a, tqg.getMatrix());
                idx--;
            }
            if (myGate instanceof ThreeQubitGate) {
                ThreeQubitGate tqg = (ThreeQubitGate) myGate;
                a = tensor(a, tqg.getMatrix());
                idx = idx - 2;
            }
            if (myGate instanceof PermutationGate) {
                throw new RuntimeException("No perm allowed ");
            }
            if (myGate instanceof Oracle) {
                a = myGate.getMatrix();
                idx = 0;
            }
            idx--;
        }
        long l1 = System.currentTimeMillis();
        return a;
    }

    /**
     * decompose a Step into steps that can be processed without permutations
     *
     * @param s
     * @param nqubit
     * @return
     */
    public static List<Step> decomposeStep(Step s, int nqubit) {
        ArrayList<Step> answer = new ArrayList<>();
        answer.add(s);
        if (s.getType() == Step.Type.PSEUDO) {
            s.setComplexStep(s.getIndex());
            return answer;
        }

        List<Gate> gates = s.getGates();

        if (gates.isEmpty()) {
            return answer;
        }
        boolean simple = gates.stream().allMatch(g -> g instanceof SingleQubitGate);
        if (simple) {
            return answer;
        }
        // if only 1 gate, which is an oracle, return as well
        if ((gates.size() == 1) && (gates.get(0) instanceof Oracle)) {
            return answer;
        }
        // at least one non-singlequbitgate
        List<Gate> firstGates = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof ProbabilitiesGate) {
                s.setInformalStep(true);
                return answer;
            }
            if (gate instanceof BlockGate) {
                if (gate instanceof ControlledBlockGate) {
                    processBlockGate((ControlledBlockGate) gate, answer);
                }
                firstGates.add(gate);
            } else if (gate instanceof SingleQubitGate) {
                firstGates.add(gate);
            } else if (gate instanceof TwoQubitGate) {
                TwoQubitGate tqg = (TwoQubitGate) gate;
                int first = tqg.getMainQubitIndex();
                int second = tqg.getSecondQubitIndex();
                if ((first >= nqubit) || (second >= nqubit)) {
                    throw new IllegalArgumentException("Step " + s + " uses a gate with invalid index " + first + " or " + second);
                }
                if (first == second + 1) {
                    firstGates.add(gate);
                } else {
                    if (first == second) {
                        throw new RuntimeException("Wrong gate, first == second for " + gate);
                    }
                    if (first > second) {
                        PermutationGate pg = new PermutationGate(first - 1, second, nqubit);
                        Step prePermutation = new Step(pg);
                        Step postPermutation = new Step(pg);
                        answer.add(0, prePermutation);
                        answer.add(postPermutation);
                        postPermutation.setComplexStep(s.getIndex());
                        s.setComplexStep(-1);
                    } else {
                        PermutationGate pg = new PermutationGate(first, second, nqubit);
                        Step prePermutation = new Step(pg);
                        Step prePermutationInv = new Step(pg);
                        int realStep = s.getIndex();
                        s.setComplexStep(-1);
                        answer.add(0, prePermutation);
                        answer.add(prePermutationInv);
                        Step postPermutation = new Step();
                        Step postPermutationInv = new Step();
                        if (first != second - 1) {
                            PermutationGate pg2 = new PermutationGate(second - 1, first, nqubit);
                            postPermutation.addGate(pg2);
                            postPermutationInv.addGate(pg2);
                            answer.add(1, postPermutation);
                            answer.add(3, postPermutationInv);
                        }
                        prePermutationInv.setComplexStep(realStep);
                    }
                }
            } else if (gate instanceof ThreeQubitGate) {
                ThreeQubitGate tqg = (ThreeQubitGate) gate;
                int first = tqg.getMainQubit();
                int second = tqg.getSecondQubit();
                int third = tqg.getThirdQubit();
                int sFirst = first;
                int sSecond = second;
                int sThird = third;
                if ((first == second + 1) && (second == third + 1)) {
                    firstGates.add(gate);
                } else {
                    int p0idx = 0;
                    int maxs = Math.max(second, third);
                    if (first < maxs) {
                        PermutationGate pg = new PermutationGate(first, maxs, nqubit);
                        Step prePermutation = new Step(pg);
                        Step postPermutation = new Step(pg);
                        answer.add(p0idx, prePermutation);
                        answer.add(answer.size() - p0idx, postPermutation);
                        p0idx++;
                        postPermutation.setComplexStep(s.getIndex());
                        s.setComplexStep(-1);
                        sFirst = maxs;
                        if (second > third) {
                            sSecond = first;
                        } else {
                            sThird = first;
                        }
                    }
                    if (sSecond != sFirst - 1) {
                        PermutationGate pg = new PermutationGate(sFirst - 1, sSecond, nqubit);
                        Step prePermutation = new Step(pg);
                        Step postPermutation = new Step(pg);
                        answer.add(p0idx, prePermutation);
                        answer.add(answer.size() - p0idx, postPermutation);
                        p0idx++;
                        postPermutation.setComplexStep(s.getIndex());
                        s.setComplexStep(-1);
                        sSecond = sFirst - 1;
                    }
                    if (sThird != sFirst - 2) {
                        PermutationGate pg = new PermutationGate(sFirst - 2, sThird, nqubit);
                        Step prePermutation = new Step(pg);
                        Step postPermutation = new Step(pg);
                        answer.add(p0idx, prePermutation);
                        answer.add(answer.size() - p0idx, postPermutation);
                        p0idx++;
                        postPermutation.setComplexStep(s.getIndex());
                        s.setComplexStep(-1);
                        sThird = sFirst - 2;
                    }
                }
            } else {
                throw new RuntimeException("Gate must be SingleQubit or TwoQubit");
            }
        }
        return answer;
    }

    public static void printMatrix(Complex[][] a) {
        for (int i = 0; i < a.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < a[i].length; j++) {
                sb.append(a[i][j]).append("    ");
            }
            System.out.println("m[" + i + "]: " + sb);
        }
    }

    public static int getInverseModulus(int a, int b) {
        int r0 = a;
        int r1 = b;
        int r2 = 0;
        int s0 = 1;
        int s1 = 0;
        int s2 = 0;
        while (r1 != 1) {
            int q = r0 / r1;
            r2 = r0 % r1;
            s2 = s0 - q * s1;
            r0 = r1;
            r1 = r2;
            s0 = s1;
            s1 = s2;
        }
        return s1 > 0 ? s1 : s1 + b;
    }

    public static int gcd(int a, int b) {
        int x = a > b ? a : b;
        int y = x == a ? b : a;
        int z = 0;
        while (y != 0) {
            z = x % y;
            x = y;
            y = z;
        }
        return x;
    }

    public static int fraction(int p, int max) {
        int length = (int) Math.ceil(Math.log(max) / Math.log(2));
        int offset = length;
        int dim = 1 << offset;
        double r = (double) p / dim + .000001;
        int period = Computations.fraction(r, max);
        return period;
    }

    public static int fraction(double d, int max) {
        double EPS = 1e-15;
        int answer = -1;
        int h = 0;
        int k = -1;
        int a = (int) d;
        double r = d - a;
        int h_2 = 0;
        int h_1 = 1;
        int k_2 = 1;
        int k_1 = 0;
        while ((k < max) && (r > EPS)) {
            h = a * h_1 + h_2;
            k = a * k_1 + k_2;
            h_2 = h_1;
            h_1 = h;
            k_2 = k_1;
            k_1 = k;
            double rec = 1 / r;
            a = (int) rec;
            r = rec - a;
        }
        return k_2;
    }

    public static Complex[][] createIdentity(int dim) {
        Complex[][] matrix = new Complex[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                matrix[i][j] = (i == j) ? Complex.ONE : Complex.ZERO;
            }
        }
        return matrix;
    }

    public static void printMemory() {
        if (!debug) {
            return;
        }
        Runtime rt = Runtime.getRuntime();
        long fm = rt.freeMemory() / 1024;
        long mm = rt.maxMemory() / 1024;
        long tm = rt.totalMemory() / 1024;
        long um = tm - fm;
        System.err.println("free = " + fm + ", mm = " + mm + ", tm = " + tm + ", used = " + um);
        /*
        System.err.println("now gc...");
        System.gc();
        fm = rt.freeMemory()/1024;
        mm = rt.maxMemory()/1024;
        tm = rt.totalMemory()/1024;
        um = tm - fm;
        System.err.println("free = "+fm+", mm = "+mm+", tm = "+tm+", used = "+um);
         */
    }

    public static Complex[] permutateVector(Complex[] vector, int a, int b) {
        int amask = 1 << a;
        int bmask = 1 << b;
        int dim = vector.length;
        Complex[] answer = new Complex[dim];
        for (int i = 0; i < dim; i++) {
            int j = i;
            int x = (amask & i) / amask;
            int y = (bmask & i) / bmask;
            if (x != y) {
                j ^= amask;
                j ^= bmask;
            }
            answer[i] = vector[j];
        }
        return answer;
    }

    static int nested = 0; // allows us to e.g. show only 2 nested steps

    public static Complex[] calculateNewState(List<Gate> gates, Complex[] vector, int length) {
        nested++;
        Complex[] answer = getNextProbability(getAllGates(gates, length), vector);
        nested--;
        return answer;
    }
    
    private static Complex[] getNextProbability(List<Gate> gates, Complex[] v) {
        Gate gate = gates.get(0);
        int nqubits = gate.getSize();
        int gatedim = 1 << nqubits;
        int size = v.length;
     //   System.err.println("GETNEXTPROBABILITY asked for size = " + size + " and gates = " + gates);
        if (gates.size() > 1) {

            int partdim = size / gatedim;
            Complex[] answer = new Complex[size];
            List<Gate> nextGates = gates.subList(1, gates.size());
            boolean id = true;
            for (Gate g : nextGates) {
                id = id && (g instanceof Identity);
            }
            if (id) {
                dbg("ONLY IDENTITY!! partdim = "+partdim);
                long s0 = System.currentTimeMillis();
                long s1 = s0;
                for (int j = 0; j < partdim; j++) {
                    dbg("do part "+j+" from "+partdim);
                    Complex[] oldv = new Complex[gatedim];
                    Complex[] newv = new Complex[gatedim];
                    for (int i = 0; i < gatedim; i++) {
                        oldv[i] = v[i * partdim + j];
                        newv[i] = Complex.ZERO;
                    }

                    if (gate.hasOptimization()) {
                        dbg("OPTPART!");
                        newv = gate.applyOptimize(oldv);
                    } else {
                        dbg("GET MATRIX for  "+gate);
                        Complex[][] matrix = gate.getMatrix();
                        s1 = System.currentTimeMillis();
                        for (int i = 0; i < gatedim; i++) {
                            for (int k = 0; k < gatedim; k++) {
                                newv[i] = newv[i].add(matrix[i][k].mul(oldv[k]));
                            }
                        }
                    }
                    for (int i = 0; i < gatedim; i++) {
                        answer[i * partdim + j] = newv[i];
                    }
                    dbg("done part");
                }
                long s2 = System.currentTimeMillis();
                return answer;
            }
            long sm0 = System.currentTimeMillis();
            Complex[][] vsub = new Complex[gatedim][partdim];
            for (int i = 0; i < gatedim; i++) {
                Complex[] vorig = new Complex[partdim];
                for (int j = 0; j < partdim; j++) {
                    vorig[j] = v[j + i * partdim];
                }
                vsub[i] = getNextProbability(nextGates, vorig);
            }
            long s0 = System.currentTimeMillis();
            Complex[][] matrix = gate.getMatrix();
            long s1 = System.currentTimeMillis();
            for (int i = 0; i < gatedim; i++) {
                for (int j = 0; j < partdim; j++) {
                    answer[j + i * partdim] = Complex.ZERO;
                    for (int k = 0; k < gatedim; k++) {
                        answer[j + i * partdim] = answer[j + i * partdim].add(matrix[i][k].mul(vsub[k][j]));
                    }
                }
            }
            long s2 = System.currentTimeMillis();
            return answer;
        } else {
            if (gatedim != size) {
                System.err.println("problem with matrix for gate " + gate);
                throw new IllegalArgumentException("wrong matrix size " + gatedim + " vs vector size " + v.length);
            }
            if (gate.hasOptimization()) {
                return gate.applyOptimize(v);
            } else {
                Complex[][] matrix = gate.getMatrix();
                Complex[] answer = new Complex[size];
                for (int i = 0; i < size; i++) {
                    answer[i] = Complex.ZERO;
                    for (int j = 0; j < size; j++) {
                        answer[i] = answer[i].add(matrix[i][j].mul(v[j]));
                    }
                }
                return answer;
            }
        }
    }

    private static List<Gate> getAllGates(List<Gate> gates, int nQubits) {
        dbg("getAllGates, orig = " + gates);
        List<Gate> answer = new ArrayList<>();
        int idx = nQubits - 1;
        while (idx >= 0) {
            final int cnt = idx;
            Gate myGate = gates.stream()
                    .filter(
                            gate -> gate.getHighestAffectedQubitIndex() == cnt)
                    .findFirst()
                    .orElse(new Identity(idx));
            dbg("stepmatrix, cnt = " + cnt + ", idx = " + idx + ", myGate = " + myGate);
            answer.add(myGate);
            if (myGate instanceof BlockGate) {
                BlockGate sqg = (BlockGate) myGate;
                idx = idx - sqg.getSize() + 1;
                dbg("processed blockgate, size = " + sqg.getSize() + ", idx = " + idx);
            }
            if (myGate instanceof TwoQubitGate) {
                idx--;
            }
            if (myGate instanceof ThreeQubitGate) {
                idx = idx - 2;
            }
            if (myGate instanceof PermutationGate) {
                throw new RuntimeException("No perm allowed ");
            }
            if (myGate instanceof Oracle) {
                idx = 0;
            }
            idx--;
        }
        return answer;
    }

    private static void processBlockGate(ControlledBlockGate gate, ArrayList<Step> answer) {
        Step master = answer.get(answer.size() -1);
        gate.calculateHighLow();
        int low = gate.getLow();
        int control = gate.getControlQubit();
        int idx = gate.getMainQubitIndex();
        int high = control;
        int size = gate.getSize();
        int gap = control - idx;
        List<PermutationGate> perm = new LinkedList<>();
        Block block = gate.getBlock();
        int bs = block.getNQubits();

        if (control > idx) {
            if (gap < bs) {
                throw new IllegalArgumentException("Can't have control at " + control + " for gate with size " + bs + " starting at " + idx);
            }
            low = idx;
            if (gap > bs) {
                high = control;
                size = high - low + 1;
                PermutationGate pg = new PermutationGate(control, control - gap + bs, low + size);

                perm.add(pg);
            }
        } else {
            low = control;
            high = idx + bs - 1;
            size = high - low + 1;
            //   gate.correctHigh(low+bs);
            for (int i = low; i < low + size - 1; i++) {
                PermutationGate pg = new PermutationGate(i, i + 1, low + size);
                perm.add(0, pg);
            }
        }

        for (int i = 0; i < perm.size(); i++) {
            PermutationGate pg = perm.get(i);
            Step lpg = new Step(pg);
            if (i < perm.size()-1) {
                lpg.setComplexStep(-1);
            } else {
                // the complex step should be the last part of the step
                lpg.setComplexStep(master.getComplexStep());
                master.setComplexStep(-1);
            }
            answer.add(lpg);
            answer.add(0, new Step(pg));
        }

    }
    
    // TODO: make this a utility method
    public static double[] calculateQubitStatesFromVector(Complex[] vectorresult) {
        int nq = (int) Math.round(Math.log(vectorresult.length) / Math.log(2));
        double[] answer = new double[nq];
        int ressize = 1 << nq;
        for (int i = 0; i < nq; i++) {
            int pw = i;//nq - i - 1;
            int div = 1 << pw;
            for (int j = 0; j < ressize; j++) {
                int p1 = j / div;
                if (p1 % 2 == 1) {
                    answer[i] = answer[i] + vectorresult[j].abssqr();
                }
            }
        }
        return answer;
    }

}
