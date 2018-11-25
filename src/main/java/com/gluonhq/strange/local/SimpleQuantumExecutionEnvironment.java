/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Gluon Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.strange.local;

import com.gluonhq.strange.Complex;
import com.gluonhq.strange.Gate;
import com.gluonhq.strange.Program;
import com.gluonhq.strange.QuantumExecutionEnvironment;
import com.gluonhq.strange.Qubit;
import com.gluonhq.strange.Result;
import com.gluonhq.strange.Step;
import com.gluonhq.strange.gate.Identity;
import com.gluonhq.strange.gate.PermutationGate;
import com.gluonhq.strange.gate.SingleQubitGate;
import com.gluonhq.strange.gate.Swap;
import com.gluonhq.strange.gate.ThreeQubitGate;
import com.gluonhq.strange.gate.TwoQubitGate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 *
 * @author johan
 */
public class SimpleQuantumExecutionEnvironment implements QuantumExecutionEnvironment {
    
    @Override
    public Result runProgram(Program p) {
        int nQubits = p.getNumberQubits();
        Qubit[] qubit = new Qubit[nQubits];
        for (int i = 0; i < nQubits; i++) {
            qubit[i] = new Qubit();
        }
        int dim = 1 << nQubits;
        Complex[] probs = new Complex[dim];
        probs[0] = Complex.ONE;
        for (int i = 1; i < dim; i++) {
            probs[i] = Complex.ZERO;
        }
        List<Step> steps = p.getSteps();
        List<Step> simpleSteps = new ArrayList<>();
        for (Step step: steps) {
            simpleSteps.addAll(decomposeStep(step, nQubits));
        }
   //    System.out.println("stepsize "+steps.size()+" and simplestepsize = "+simpleSteps.size());
       //printProbs(probs);
       Result result = new Result(nQubits, steps.size());
     //  int idx = 0;
        for (Step step: simpleSteps) {
            probs = applyStep(step, probs, qubit);
            int idx = step.getComplexStep();
            if (idx > -1)  {
            result.setIntermediateProbability(idx, probs);
            } else {
                System.out.println("don't set intermediates ");
            }
         //   idx++;
        }
        double[] qp = calculateQubitStatesFromVector(probs);
        for (int i = 0; i < nQubits; i++) {
            qubit[i].setProbability(qp[i]);
        }
        result.measureSystem();
    //    Result result = new Result(qubit, probs);
        return result;
    }
    
    @Override
    public void runProgram(Program p, Consumer<Result> result) {
        Thread t = new Thread() {
            @Override
            public void run() {
                result.accept(runProgram(p));
            }
        };
        t.start();
    }

    private void printProbs(Complex[] p) {
        System.out.println("\n");
        for (int i = 0; i < p.length; i++) {
            System.out.println("Probabiliy["+i+"]: "+p[i]);
        }
    }
    /**
     * decompose a Step into steps that can be processed without permutations
     * @param s
     * @return 
     */
    private List<Step> decomposeStep(Step s, int nqubit) {
        ArrayList<Step> answer = new ArrayList<>();
        answer.add(s);
        List<Gate> gates = s.getGates();
        if (gates.isEmpty()) return answer;
        boolean simple = gates.stream().allMatch(g -> g instanceof SingleQubitGate);
        if (simple) return answer;
        // at least one non-singlequbitgate
       // System.out.println("Complex gates!");
        List<Gate> firstGates = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof SingleQubitGate) {
                firstGates.add(gate);
            } else if (gate instanceof TwoQubitGate) {
                TwoQubitGate tqg = (TwoQubitGate) gate;
                int first = tqg.getMainQubit();
                int second = tqg.getSecondQubit();
                if (first == second + 1) {
                    firstGates.add(gate);
                } else {
                    if (first == second) throw new RuntimeException ("Wrong gate, first == second for "+gate);
                    if (first > second) {
                        Step prePermutation = new Step();
                        Step postPermutation = new Step();
                        PermutationGate pg = new PermutationGate(first - 1, second, nqubit);
                        prePermutation.addGate(pg);
                        postPermutation.addGate(pg);
                        answer.add(0, prePermutation);
                        answer.add(postPermutation);
                        postPermutation.setComplexStep(s.getIndex());
                        s.setComplexStep(-1);
                        tqg.setAdditionalQubit(first -1, second);

                    } else {
                        Step prePermutation = new Step();

                        Step prePermutationInv = new Step();
                        PermutationGate pg = new PermutationGate(first, second, nqubit );
                        prePermutation.addGate(pg);
                        prePermutationInv.addGate(pg);
                        int realStep = s.getIndex();
                        s.setComplexStep(-1);
                        answer.add(0, prePermutation);
                        answer.add(prePermutationInv);
                        Step postPermutation = new Step();
                        Step postPermutationInv = new Step();
                        if (first != second -1) {
                            PermutationGate pg2 = new PermutationGate(second-1, first, nqubit );
                            postPermutation.addGate(pg2);
                            postPermutationInv.addGate(pg2);
                            answer.add(1, postPermutation);
                            answer.add(3, postPermutationInv);
                        } 
                        prePermutationInv.setComplexStep(realStep);

                        tqg.setMainQubit(second);
                        tqg.setAdditionalQubit(second-1,0);
                    }
                }
            } else if (gate instanceof ThreeQubitGate) {
                ThreeQubitGate tqg = (ThreeQubitGate) gate;
                int first = tqg.getMainQubit();
                int second = tqg.getSecondQubit();
                int third = tqg.getThirdQubit();
                if ((first == second + 1) && (second == third + 1)) {
                    firstGates.add(gate);
                } else {
                    throw new RuntimeException("ThreeQubit Gate should have qubits in order 1-2-3");
                }
            }
            else {
                throw new RuntimeException("Gate must be SingleQubit or TwoQubit");
            }
        }
        return answer;
    }
    
       private List<Step> olddecomposeStep (Step s, int nqubit) {
        ArrayList<Step> answer = new ArrayList<>();
        answer.add(s);
        List<Gate> gates = s.getGates();
        if (gates.isEmpty()) return answer;
        boolean notsimple = gates.stream().anyMatch(g -> (!(g instanceof SingleQubitGate)));
        if (!notsimple) return answer;
        // at least one non-singlequbitgate
       // System.out.println("Complex gates!");
        List<Gate> firstGates = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof SingleQubitGate) {
                firstGates.add(gate);
            } else if (gate instanceof TwoQubitGate) {
                TwoQubitGate tqg = (TwoQubitGate) gate;
                int first = tqg.getMainQubit();
                int second = tqg.getSecondQubit();
                if (first == second - 1) {
                    firstGates.add(gate);
                } else {
                    if (first == second) throw new RuntimeException ("Wrong gate, first == second for "+gate);
                    if (second > first) {
                        Step prePermutation = new Step();

                        PermutationGate pg = new PermutationGate(first + 1, second, nqubit);
                        prePermutation.addGate(pg);
                        answer.add(0, prePermutation);
                        answer.add(prePermutation);

                    } else {
                        System.out.println("FIRST > SECOND");
                        Step prePermutation = new Step();

                        PermutationGate pg = new PermutationGate(first, second, nqubit );
                        prePermutation.addGate(pg);

                        answer.add(0, prePermutation);
                        answer.add(prePermutation);
                        Step postPermutation = new Step();
                        if (first != second +1) {
                            System.out.println("FIRST NOT SECOND + 1, first = "+first+", second = "+second);
                            PermutationGate pg2 = new PermutationGate(second+1, first, nqubit );
                            postPermutation.addGate(pg2);
                            answer.add(0, postPermutation);
                            answer.add(postPermutation);
                        }
//                        for (int i = second; i < first; i++) {
//                            Swap swap = new Swap(i, i+1);
//                            prePermutation.addGate(swap);
//                        }
                        tqg.setMainQubit(second);
                        tqg.setAdditionalQubit(second+1,0);
                    }
                }
            } else {
                throw new RuntimeException("Gate must be SingleQubit or TwoQubit");
            }
        }
        return answer;
    }
    
    private List<Step> decomposeSteps(List<Step> steps) {
        return steps;
    }
    
    private Complex[]  applyStep (Step step, Complex[] vector, Qubit[] qubits) {
        List<Gate> gates = step.getGates();
        Complex[][] a = calculateStepMatrix(gates, qubits.length);
       // System.out.println("applystep, gates = "+gates);
  // printMatrix(a);
        Complex[] result = new Complex[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = Complex.ZERO;
            for (int j = 0; j < vector.length; j++) {
                result[i] = result[i].add(a[i][j].mul(vector[j]));
            }
        }
        return result;
    }
    
    private Complex[][] calculateStepMatrix(List<Gate> gates, int nQubits) {
        Complex[][] a = new Complex[1][1];
        a[0][0] = Complex.ONE;
        int idx = nQubits-1;
        while (idx >= 0) {
            Gate myGate = new Identity(idx);
            final int cnt = idx;
            Optional<Gate> myGateOpt = gates.stream().filter(gate -> gate.getAffectedQubitIndex().contains(cnt))
                    .findFirst();
            if (myGateOpt.isPresent()) {
                myGate = myGateOpt.get();
            }
         //   System.out.println("idx = "+idx+", gate = "+myGate);
            if (myGate instanceof SingleQubitGate) {
                SingleQubitGate sqg = (SingleQubitGate)myGate;
                a = tensor(a, sqg.getMatrix());
            }
            if (myGate instanceof TwoQubitGate) {
                TwoQubitGate tqg = (TwoQubitGate)myGate;
                a = tensor(a, tqg.getMatrix());
                idx--;
            }
            if (myGate instanceof ThreeQubitGate) {
                ThreeQubitGate tqg = (ThreeQubitGate)myGate;
                a = tensor(a, tqg.getMatrix());
                idx = idx-2;
            }
            if (myGate instanceof PermutationGate) {
                a = tensor(a, myGate.getMatrix());
                idx = 0;
            }
            idx--;
        }
       // System.out.println("done calcstepmatrix, nq = "+nQubits+", dim = "+a.length);
        return a;
    }
    
    private Complex[][] oldcalculateStepMatrix(List<Gate> gates, int nQubits) {
     //   System.out.println("calcstepmatrix, nq = "+nQubits+", gates = "+gates);
        Complex[][] a = new Complex[1][1];
        a[0][0] = Complex.ONE;
        int idx = 0;
        while (idx < nQubits) {
            Gate myGate = new Identity(idx);
            final int cnt = idx;
            Optional<Gate> myGateOpt = gates.stream().filter(gate -> gate.getAffectedQubitIndex().contains(cnt))
                    .findFirst();
            if (myGateOpt.isPresent()) {
                myGate = myGateOpt.get();
            }
            if (myGate instanceof SingleQubitGate) {
                SingleQubitGate sqg = (SingleQubitGate)myGate;
                a = tensor(a,sqg.getMatrix());
            }
            if (myGate instanceof TwoQubitGate) {
                TwoQubitGate tqg = (TwoQubitGate)myGate;
                a = tensor (a, tqg.getMatrix());
                idx++;
            }
            if (myGate instanceof PermutationGate) {
                a = tensor (a, myGate.getMatrix());
                idx = nQubits;
            }
            idx++;
        }
       // System.out.println("done calcstepmatrix, nq = "+nQubits+", dim = "+a.length);
        return a;
    }
    
    private Complex[][] tensor(Complex[][] a, Complex[][] b) {
        int d1 = a.length;
        int d2 = b.length;
        Complex[][] result = new Complex[d1 * d2][d1 * d2];
        for (int rowa = 0; rowa < d1; rowa++) {
            for (int cola = 0; cola < d1; cola++) {
                for (int rowb = 0; rowb < d2; rowb++) {
                    for (int colb = 0; colb < d2; colb++) {
                        result[d2 * rowa + rowb][d2 * cola + colb] = a[rowa][cola].mul( b[rowb][colb]);
                    }
                }
            }
        }
        return result;
    }
        
        
    private double[] calculateQubitStatesFromVector(Complex[] vectorresult) {
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
    
    public Complex[][] createPermutationMatrix(int first, int second, int n) {
        Complex[][] swapMatrix = new Swap().getMatrix();
        Complex[][] iMatrix = new Identity().getMatrix();
        Complex[][] answer = iMatrix;
        int i = 1;
        if (first == 0) {
            answer = swapMatrix;
            i++;
        }
        while (i < n) {
            if (i == first) {
                i++;
                answer = tensor(answer, swapMatrix);
            } else {
                answer = tensor(answer, iMatrix);
            }
            i++;
        }
        return answer;
    }

    private void printMatrix(Complex[][] a) {
        for (int i = 0; i < a.length; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < a[i].length; j++) {
                sb.append(a[i][j]).append("    ");
            }
            System.out.println("m["+i+"]: "+sb);
        }
    }
    
}
