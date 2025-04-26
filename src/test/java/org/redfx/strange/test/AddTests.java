package org.redfx.strange.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.redfx.strange.Complex;
import org.redfx.strange.Program;
import org.redfx.strange.Qubit;
import org.redfx.strange.Result;
import org.redfx.strange.Step;
import org.redfx.strange.gate.Add;
import org.redfx.strange.gate.AddInteger;
import org.redfx.strange.gate.AddIntegerModulus;
import org.redfx.strange.gate.X;

/**
 *
 * @author johan
 */
public class AddTests extends BaseGateTests {
        
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
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[3].abssqr(), DELTA);
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
    public void add21() {
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(1), new X(2));
        p.addStep(prep);
        Add add = new Add(0,1,2,3);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        for (Qubit qq : q) {
            System.err.println("QQ = "+qq.getProbability());
        }
        assertEquals(4, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());  
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure()); 
    }
                       
    @Test
    public void admin2part1() {
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(1), new X(2));
        p.addStep(prep);
        Add add = new Add(0,1,2,3);
        p.addStep(new Step(add));
//        Add min = new Add(0,1,2,3).inverse();
//        p.addStep(new Step(min));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(1, q[1].measure());  
        assertEquals(1, q[2].measure());
        assertEquals(0, q[3].measure());  
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
        assertEquals(0, q[0].measure());
    }
         
    @Test
    public void add01num1() {
        Program p = new Program(2);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        AddInteger add = new AddInteger(0,1,1);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
    }
 
    @Test
    public void min01num1() { // 2 -1 = 1
        Program p = new Program(2);
        Step prep = new Step();
        prep.addGates(new X(1));
        p.addStep(prep);
        AddInteger min = new AddInteger(0,1,1).inverse();
        p.addStep(new Step(min));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(2, q.length);
        assertEquals(1, q[0].measure());
        assertEquals(0, q[1].measure());
        Complex[] probs = result.getProbability();
        assertEquals(0, probs[0].abssqr(), DELTA);
        assertEquals(1, probs[1].abssqr(), DELTA);
        assertEquals(0, probs[2].abssqr(), DELTA);
        assertEquals(0, probs[3].abssqr(), DELTA);
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
        Complex[] probs = result.getProbability();
        assertEquals(1, probs[7].abssqr(), DELTA);
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
    public void addmod11num4() {
        Program p = new Program(4);
        Step prep = new Step();
        prep.addGates(new X(0));
        p.addStep(prep);
        AddIntegerModulus add = new AddIntegerModulus(0,1,1,3);
        p.addStep(new Step(add));
        Result result = runProgram(p);
        Qubit[] q = result.getQubits();
        assertEquals(4, q.length);
        assertEquals(0, q[0].measure());
        assertEquals(1, q[1].measure());
        assertEquals(0, q[2].measure());
        assertEquals(0, q[3].measure());
    }  
     
}
