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
import org.redfx.strange.Complex;
import org.redfx.strange.ControlledBlockGate;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Add;
import org.redfx.strange.gate.AddInteger;
import org.redfx.strange.gate.AddModulus;
import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.Mul;
import org.redfx.strange.gate.MulModulus;
import org.redfx.strange.gate.Swap;
import org.redfx.strange.gate.X;
import org.redfx.strange.local.Computations;


/**
 *
 * @author johan
 */
public class ArithmeticTests extends BaseGateTests {

    static final double D = 0.000000001d;

    @Test
    public void gcd() {
        assertEquals(2, Computations.gcd(2, 6));
        assertEquals(2, Computations.gcd(6, 2));
        assertEquals(1, Computations.gcd(7, 15));
        assertEquals(1, Computations.gcd(15, 7));
        assertEquals(3, Computations.gcd(15, 21));
        assertEquals(3, Computations.gcd(21, 15));
    }
    
    @Test
    public void add00() {
        Program p = new Program(2);
        Step prep = new Step();
        p.addStep(prep);
        Add add = new Add(0,0,1,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());   
    }
       
    @Test
    public void add10() {
        Program p = new Program(2);
        Step prep = new Step();
        prep.addGate(new X(0));
        p.addStep(prep);
        Add add = new Add(0,0,1,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());   
    }
         
        @Test
    public void add010() {
        Program p = new Program(3);
        Step prep = new Step();
        prep.addGate(new X(1));
        p.addStep(prep);
        Add add = new Add(1,1,2,2);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());   
    }
    @Test
    public void add01() {
        Program p = new Program(2);
        Step prep = new Step();
        prep.addGate(new X(1));
        p.addStep(prep);
        Add add = new Add(0,0,1,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());   
    }
            
    @Test
    public void add11() {
        Program p = new Program(2);
        Step prep = new Step();
        prep.addGates(new X(0), new X(1));
        p.addStep(prep);
        Add add = new Add(0,0,1,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());   
    }
               
    @Test
    public void add61() {
        Program p = new Program(6);
        Step prep = new Step();
        prep.addGates(new X(1), new X(2), new X(3));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());  
        assertEquals(1, q[2].measure());
        assertEquals(1, q[3].measure()); 
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());  
    }
    
    @Test
    public void addmodp0() {
        int N = 1;
        int dim = 2;
        Program p = new Program(dim);
        AddInteger min = new AddInteger(0,1,N).inverse();
        p.addStep(new Step(min));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
    }
    
    @Test
    public void add0num0() {
        Program p = new Program(1);
        Step prep = new Step();
        p.addStep(prep);
        AddInteger add = new AddInteger(0,0,0);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(1, q.length);
        assertEquals(0, q[0].measure());
    }
           
    @Test
    public void add0num1() {
        Program p = new Program(1);
        Step prep = new Step();
        p.addStep(prep);
        AddInteger add = new AddInteger(0,0,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(1, q.length);
        assertEquals(1, q[0].measure());  
    }
    
    @Test
    public void add1num0() {
        Program p = new Program(1);
        Step prep = new Step();
        prep.addGate(new X(0));
        p.addStep(prep);
        AddInteger add = new AddInteger(0,0,0);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(1, q.length);
        assertEquals(1, q[0].measure());  
    }

    @Test
    public void add1num1() {
        Program p = new Program(1);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        AddInteger add = new AddInteger(0,0,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(1, q.length);
        assertEquals(0, q[0].measure());
    }
         
    @Test
    public void add5num2() {
        Program p = new Program(3);
        Step prep = new Step();
        prep.addGates(new X(0), new X(2));
        p.addStep(prep);
        AddInteger add = new AddInteger(0,2,2);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
                
           
    @Test
    public void add5num2p() {
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(1), new X(3));
        p.addStep(prep);
        AddInteger add = new AddInteger(1,3,2);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(1, q[3].measure());
    }
    
    @Test
    public void add5num4() {
        Program p = new Program(3);
        Step prep = new Step();
        prep.addGates(new X(0), new X(2));
        p.addStep(prep);
        AddInteger add = new AddInteger(0,2,4);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
    }
         
         
    @Test
    public void adjoint() {
           Program p = new Program(6);
        Step prep = new Step();
        prep.addGates(new X(1), new X(2), new X(3));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        Add min = new Add(0,2,3,5).inverse();
        p.addStep(new Step(min));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());  
        assertEquals(1, q[2].measure());
        assertEquals(1, q[3].measure()); 
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());  
    }
    
    @Test
    public void multiply5x3() { // 5 x 3 mod 8 = 7
        Program p = new Program(6);
        Step prep = new Step();
        prep.addGates(new X(3), new X(4)); // 5 in second register
        p.addStep(prep);
        for (int i = 0; i < 5; i++) {
            Add add = new Add(0, 2, 3, 5);
            p.addStep(new Step(add));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(1, q[0].measure()); // result in q2,q1,q0
        assertEquals(1, q[1].measure());  
        assertEquals(1, q[2].measure());
        assertEquals(1, q[3].measure()); 
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());  
    }
      
    @Test
    public void multiply5x7() { // 5 x 7 mod 8 = 3
        Program p = new Program(6);
        Step prep = new Step();
        prep.addGates(new X(3), new X(4), new X(5));
        p.addStep(prep);
        for (int i = 0; i < 5; i++) {
            Add add = new Add(0, 2, 3, 5);
            p.addStep(new Step(add));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(1, q[3].measure()); 
        assertEquals(1, q[4].measure());
        assertEquals(1, q[5].measure());  
    }
    
    @Test
    public void multiply5x3andswap() { // 5 x 3 mod 8 = 7
        Program p = new Program(6);
        Step prep = new Step();
        prep.addGates(new X(3), new X(4)); // 5 in second register
        p.addStep(prep);
        for (int i = 0; i < 5; i++) {
            Add add = new Add(0, 2, 3, 5);
            p.addStep(new Step(add));
        }
        p.addStep(new Step( new Swap(0,3)));
        p.addStep(new Step( new Swap(1,4)));
        p.addStep(new Step( new Swap(2,5)));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(1, q[0].measure()); // result in q2,q1,q0
        assertEquals(1, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(1, q[3].measure()); 
        assertEquals(1, q[4].measure());
        assertEquals(1, q[5].measure());  
    }
    
    @Test
    public void getInverseModulus() {
        int answer = Computations.getInverseModulus(56, 69);
        assertEquals(53, answer);
    }
         
    @Test
    public void multiply5x3andswapandclean() { // 5 x 3 mod 8 = 7
        Program p = new Program(6);
        Step prep = new Step();
        int mul = 5;
        prep.addGates(new X(3), new X(4)); // 3 in high register
        p.addStep(prep);
        for (int i = 0; i < mul; i++) {
            Add add = new Add(0, 2, 3, 5);
            p.addStep(new Step(add));
        }
        p.addStep(new Step( new Swap(0,3)));
        p.addStep(new Step( new Swap(1,4)));
        p.addStep(new Step( new Swap(2,5)));

        int invsteps = Computations.getInverseModulus(mul,8);
        for (int i = 0; i < invsteps; i++) {
            Add add = new Add(0, 2, 3, 5).inverse();
            p.addStep(new Step(add));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        assertEquals(0, q[0].measure()); // q2,q1,q0 should be clean
        assertEquals(0, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(1, q[3].measure()); // result in q3,q4,q5
        assertEquals(1, q[4].measure());
        assertEquals(1, q[5].measure());  
    }
    
    @Test 
    public void mul01() {
        Program p = new Program(2);
        Step s = new Step(new Mul(0,0,1));
        p.addStep(s);
          Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
    }
        
    @Test 
    public void mul11() {
        Program p = new Program(2);
        Step prep = new Step();
        prep.addGates(new X(1));
        Step s = new Step(new Mul(0,0,1));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
    }
            
   @Test 
    public void mul13() { // 0100 -> 1100
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(2));
        Step s = new Step(new Mul(0,1,3));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        System.err.println("results: ");
        for (int i = 0; i < 4; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(1, q[3].measure());
    }
    
                
    @Test 
    public void mul55() { // 101000 -> 001000
        Program p = new Program(6);
        Step prep = new Step();
        prep.addGates(new X(3), new X(5));
        Step s = new Step(new Mul(0,2,5));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        System.err.println("results: ");
        for (int i = 0; i < 6; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(1, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
    }
    
    @Test
    public void controlledAdd001() {
        Program p = new Program(3);
        Step s = new Step();
        Add add = new Add(0,0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(add, 1,0);
        Complex[][] m = cbg.getMatrix();
        Complex.printMatrix(m, System.err);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Step cg = new Step(cbg);
        p.addStep(cg);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
    }
          
    @Test // q_0 = q_1(0) + q_0 (0) = 0
    public void controlledAdd100() {
        Program p = new Program(3);
        Step s = new Step();
        Add add = new Add(0,0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(add, 0,2);
        Complex[][] m = cbg.getMatrix();
        Complex.printMatrix(m, System.err);
        Step prep = new Step();
        prep.addGates(new X(2));
        p.addStep(prep);
        Step cg = new Step(cbg);
        p.addStep(cg);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(1, q[2].measure());
    }
    
    @Test // q_0 = q_1(0) + q_0 (1) = 1
    public void controlledAdd101() {
        Program p = new Program(3);
        Step s = new Step();
        Add add = new Add(0,0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(add, 1,0);
        Complex[][] m = cbg.getMatrix();
        Complex.printMatrix(m, System.err);
        Step prep = new Step();
        prep.addGates(new X(0), new X(2));
        p.addStep(prep);
        Step cg = new Step(cbg);
        p.addStep(cg);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
    
    @Test // q_0 = q_1(1) + q_0 (0) = 1
    public void controlledAdd110() {
        Program p = new Program(3);
        Step s = new Step();
        Add add = new Add(0,0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(add, 0,2);
        Complex[][] m = cbg.getMatrix();
        Complex.printMatrix(m, System.err);
        Step prep = new Step();
        prep.addGates(new X(1), new X(2));
        p.addStep(prep);
        Step cg = new Step(cbg);
        p.addStep(cg);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
           
    @Test // q_0 = q_1(1) + q_0 (1) = 0
    public void controlledAdd111() {
        Program p = new Program(3);
        Step s = new Step();
        Add add = new Add(0,0,1,1);
        ControlledBlockGate cbg = new ControlledBlockGate(add, 0,2);
        Complex[][] m = cbg.getMatrix();
        Complex.printMatrix(m, System.err);
        Step prep = new Step();
        prep.addGates(new X(0), new X(1), new X(2));
        p.addStep(prep);
        Step cg = new Step(cbg);
        p.addStep(cg);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(3, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
    }
    
    @Test // 
    public void expmul() {
        int length = 2;
        int a = 3;
        Program p = new Program(3 * length);
        Step prepAnc = new Step(new X(3 * length -1));
        p.addStep(prepAnc);
        for (int i = 0; i < length; i++) {
            int m = a << (length -1 - i); // 2^(n-1-i)
            Mul mul = new Mul(length, 2 * length-1, a);
            if (i < length -1) {
                Swap swap = new Swap(i, length-1);
                p.addStep(new Step(swap));
            }
            ControlledBlockGate cbg = new ControlledBlockGate(mul, length, length -1);
            p.addStep(new Step(cbg));
            if (i < length -1) {
                Swap swap = new Swap(i, length-1);
                p.addStep(new Step(swap));
            }
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(6, q.length);
        System.err.println("results: ");
        for (int i = 0; i < 6; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(1, q[5].measure());
    }
    
    
    @Test
    public void addmod0() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }

    @Test
    public void addmod1() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(1, q[6].measure());
    }

    @Test
    public void addmod2() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(1, q[6].measure());
    }

    @Test
    public void addmod3() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }

    @Test
    public void addmod4() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));
        
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        ControlledBlockGate cbg2 = new ControlledBlockGate(block, dim-1, 2);
        p.addStep(new Step(cbg2));
        
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }

    @Test
    public void addmod5() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));
        
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        ControlledBlockGate cbg2 = new ControlledBlockGate(block, dim-1, 2);
        p.addStep(new Step(cbg2));
        
        Add add3 = new Add(0,2,3,5);
        p.addStep (new Step(add3));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }

   @Test
    public void addmod1plus1mod3() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(0), new X(3));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));
        
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        ControlledBlockGate cbg2 = new ControlledBlockGate(block, dim-1, 2);
        p.addStep(new Step(cbg2));
        
        Add add3 = new Add(0,2,3,5);
        p.addStep (new Step(add3));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(1, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(0, q[6].measure());
    }

    @Test
    public void addmod2p2mod3() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(1), new X(4));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));
        
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        ControlledBlockGate cbg2 = new ControlledBlockGate(block, dim-1, 2);
        p.addStep(new Step(cbg2));
        
        Add add3 = new Add(0,2,3,5);
        p.addStep (new Step(add3));

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
    public void addmod2p2mod3step1() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(1), new X(4));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));

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
    public void addmod2p2mod3step2() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(1), new X(4));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));
        
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(7, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure());
        assertEquals(0, q[5].measure());
        assertEquals(1, q[6].measure());
    }
    
    
    @Test
    public void addmod2p2mod3step3() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(1), new X(4));
        p.addStep(prep);
        Add add = new Add(0,2,3,5);
        p.addStep(new Step(add));
        
        AddInteger min = new AddInteger(0,2,N).inverse();
        p.addStep(new Step(min));
        p.addStep(new Step(new Cnot(2,dim-1)));
        AddInteger addN = new AddInteger(0,2,N);
        ControlledBlockGate cbg = new ControlledBlockGate(addN, 0,dim-1);
        p.addStep(new Step(cbg));
        
        
        Add add2 = new Add(0,2,3,5).inverse();
        p.addStep(new Step(add2));
        p.addStep(new Step(new X(dim-1)));
        
        Block block = new Block(1);
        block.addStep(new Step(new X(0)));
        ControlledBlockGate cbg2 = new ControlledBlockGate(block, dim-1, 2);
        p.addStep(new Step(cbg2));

        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
                System.err.println("Results: ");
        for (int i = 0; i < 7; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
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
    public void addmodgate() {
        int n = 2;
        int N = 3;
        int dim = 2 * (n+1)+1;
        Program p = new Program(dim);
        Step prep = new Step();
        prep.addGates(new X(1), new X(4));
        p.addStep(prep);
        
        Step mod = new Step(new AddModulus(0,2,3,5, N));
        p.addStep(mod);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        System.err.println("Results: ");
        for (int i = 0; i < 7; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
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
    public void multiplyMod5x3andswapandcleans1() { // 5 x 3 mod 6 = 3
        System.err.println("C1");
        Program p = new Program(9);
        Step prep = new Step();
        int mul = 5;
        int N = 6;
        prep.addGates(new X(4), new X(5)); // 3 in high register
        p.addStep(prep);
        for (int i = 0; i < mul; i++) {
            AddModulus add = new AddModulus(0, 3, 4, 7, N);
            p.addStep(new Step(add));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(9, q.length);
        assertEquals(1, q[0].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(1, q[1].measure());  
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
        assertEquals(1, q[4].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[5].measure());
        assertEquals(0, q[6].measure());  
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
    }
    
    @Test
    public void multiplyMod5x3andswapandcleans2() { // 5 x 3 mod 6 = 3
        System.err.println("C2");
        // |A00110000> -> 
        Program p = new Program(9);
        Step prep = new Step();
        int mul = 5;
        int N = 6;
        prep.addGates(new X(4), new X(5)); // 3 in high register
        p.addStep(prep);
        for (int i = 0; i < 1; i++) {
            AddModulus add = new AddModulus(0, 3, 4, 7, N);
            p.addStep(new Step(add));
        }
        p.addStep(new Step( new Swap(0,4)));
        p.addStep(new Step( new Swap(1,5)));
        p.addStep(new Step( new Swap(2,6)));
        p.addStep(new Step( new Swap(3,7)));

        int invsteps = Computations.getInverseModulus(mul,N);
        System.err.println("INVSTEPS = "+invsteps);
        for (int i = 0; i < invsteps; i++) {
            AddModulus addModulus = new AddModulus(0, 3, 4, 7, N).inverse();
            p.addStep(new Step(addModulus));
        }
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        System.err.println("Results: ");
        for (int i = 0; i < 9; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
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
    
    @Test
    public void multiplyModGate5x3mod6() { // 5 x 3 mod 6 = 3
        System.err.println("TESTMULMOD");
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
        System.err.println("results: ");
        for (int i = 0; i < 9; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
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
    
    @Test
    public void multiplyModGate5x3mod6p() { // 5 x 3 mod 6 = 3
        Program p = new Program(10);
        int mul = 5;
        int N = 6;
        Step prep = new Step();
        prep.addGates(new X(5), new X(6)); // 3 in high register
        Step s = new Step(new MulModulus(1,4,mul, N));
        p.addStep(prep);
        p.addStep(s);
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        System.err.println("results: ");
        for (int i = 0; i < 9; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
        }
        assertEquals(10, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(0, q[1].measure()); // q2,q1,q0,q3 should be clean
        assertEquals(0, q[2].measure());  
        assertEquals(0, q[3].measure());
        assertEquals(0, q[4].measure());
        assertEquals(1, q[5].measure()); // result in q4,q5,q6,q7
        assertEquals(1, q[6].measure());
        assertEquals(0, q[7].measure());  
        assertEquals(0, q[8].measure());  
        assertEquals(0, q[9].measure());  
    }
    
//    @Test
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
        System.err.println("results: ");
        for (int i = 0; i < 9; i++) {
            System.err.println("m["+i+"]: "+q[i].measure());
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
}
