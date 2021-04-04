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
package org.redfx.strange.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.redfx.strange.Block;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Add;
import org.redfx.strange.gate.AddInteger;
import org.redfx.strange.gate.AddModulus;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.MulModulus;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class SingleTest extends BaseGateTests {
         
    @Test
    public void multiplyModGate3p5x1mod6() { // 5 x 3 mod 6 = 3
        Program p = new Program(9);
        int mul = 5;
        int N = 6;
        Step prep = new Step();
        prep.addGates(new X(4), new X(5)); // 3 in high register
        Step s = new Step(new MulModulus(0,3,mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        for (int i = 0; i < q.length; i++) {
            System.err.println("q["+i+"] = "+q[i].measure());
        }

        assertEquals(9, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
   // @Test
    public void minus1() {
        // 1 - 3 = -2
        // -2 + 8 = 6
        int N = 3;
        int dim = 3;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(dim, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
        
    @Test
    public void inverseinverse() {
        int x1 = 2;
        int x0 = 0;
        int y0 = 3;
        int y1 = 5;
        int n = x1-x0;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program answer = new Program(7);

        answer.addStep(new Step (new X(0), new X(1), new X(2), new X(4)));
        answer.addStep(new Step(Step.Type.PSEUDO));
        
        Add add1 = new Add(x0,x1,y0,y1);
        answer.addStep (new Step(add1.inverse()));
        
        Add add2 = new Add(x0,x1,y0,y1);
        answer.addStep (new Step(add2.inverse().inverse()));
        
        
        Result result = runProgram(answer);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
        
    }
         
    @Test
    public void minmod1() {
        int mod = 3;
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0), new X(4));
        p.addStep(prep);
        AddModulus min = new AddModulus(0,2,3,5, mod).inverse();

        p.addStep(new Step(min));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }
    
    @Test
    public void testaddmodblock() {
        int x1 = 2;
        int x0 = 0;
        int y0 = 3;
        int y1 = 5;
        int n = x1-x0;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program answer = new Program(7);
        answer.addStep(new Step (new X(1), new X(4)));
        Add add = new Add(x0, x1, y0, y1);
        answer.addStep(new Step(add));

        AddInteger min = new AddInteger(x0,x1,N).inverse();
        answer.addStep(new Step(min));
        answer.addStep(new Step(new Cnot(x1,dim-1)));
        AddInteger addN = new AddInteger(x0,x1,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, x0,dim-1);
        answer.addStep(new Step(cbg));

        Add add2 = new Add(x0,x1,y0,y1).inverse();
        answer.addStep(new Step(add2));
        answer.addStep(new Step(new X(dim-1)));

        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        ControlledBlockGate cbg2 = new ControlledBlockGate(block, dim-1, x1);
        answer.addStep(new Step(cbg2));

        Add add3 = new Add(x0,x1,y0,y1);
        answer.addStep (new Step(add3));
        
        Result result = runProgram(answer);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }
    
    @Test
    public void minmod3() {
        int mod = 3;
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0), new X(4));
        p.addStep(prep);
        AddModulus add = new AddModulus(0,2,3,5, mod);
        AddModulus min = new AddModulus(0,2,3,5, mod).inverse();

        p.addStep(new Step(add));
        p.addStep(new Step(min));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }
    
    @Test
    public void minmod4() {
        int mod = 3;
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0), new X(4));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
         AddModulus min = new AddModulus(0,2,3,5, mod).inverse();

        p.addStep(new Step(min));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }
    @Test
    public void test3x1mod5is3() { // 3 x 1 %5 = 3
        Program p = new Program(9);
        int mul = 1;
        int n = 5;
        Step prep = new Step();
        prep.addGates(new X(4), new X(5)); // 3 in high register
        Step s = new Step(new MulModulus(0,3,mul, n));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
    @Test
    public void test3x2mod5is1() { // 3 x 2 %5 = 1
        Program p = new Program(9);
        int mul = 2;
        int n = 5;
        Step prep = new Step();
        prep.addGates(new X(4), new X(5)); // 3 in high register
        Step s = new Step(new MulModulus(0,3,mul, n));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
    
    @Test
    public void test3x5mod6is3() { // 3 x 5 %6 = 3
        Program p = new Program(9);
        int mul = 5;
        int n = 6;
        Step prep = new Step();
        prep.addGates(new X(4), new X(5)); // 3 in high register
        Step s = new Step(new MulModulus(0,3,mul, n));

        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
    @Test
    public void multiplyModGate5x3mod6() { // 5 x 3 mod 6 = 3
        Program p = new Program(9);
        int mul = 5;
        int N = 6;
        Step prep = new Step();
        prep.addGates(new X(4), new X(5)); // 3 in high register
        Step s = new Step(new MulModulus(0,3,mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();

        assertEquals(9, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
    
   // @Test
    public void findPeriod7_15() { // 5 x 3 mod 6 = 3
        Program p = new Program(9);
        int mul = 7;
        int n = 3;
        Step prep = new Step();
        prep.addGates(new X(4), new X(5)); // 3 in high register
        Step s = new Step(new MulModulus(0,3,mul, n));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
    
  public static void main(String[] args) {
      SingleTest st = new SingleTest();
  }
}
