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

import org.redfx.strange.gate.*;

import java.util.List;

/**
 *
 * A Gate describes an operation on one or more qubits. Gates are combined in
 * {@link Step}s, and {@link Step}s are added to a quantum {@link Program}
 *
 * @author johan
 * @version $Id: $Id
 */
public interface Gate {

    /**
     * <p>cnot.</p>
     *
     * @param a a int
     * @param b a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate cnot(int a, int b) { return new Cnot(a, b); }
    /**
     * <p>cz.</p>
     *
     * @param a a int
     * @param b a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate cz(int a, int b)   { return new Cz(a, b); }
    /**
     * <p>hadamard.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate hadamard(int idx)  { return new Hadamard(idx); }
    /**
     * <p>identity.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate identity(int idx) { return new Identity(idx); }
    /**
     * <p>measurement.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate measurement(int idx) { return new Measurement(idx); }
    /**
     * <p>oracle.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate oracle(int idx) { return new Oracle(idx); }
    /**
     * <p>oracle.</p>
     *
     * @param matrix an array of {@link org.redfx.strange.Complex} objects
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate oracle(Complex[][] matrix) { return new Oracle(matrix); }
    /**
     * <p>permutation.</p>
     *
     * @param a a int
     * @param b a int
     * @param n a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate permutation(int a, int b, int n) { return new PermutationGate(a, b, n); }
    /**
     * <p>probability.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate probability(int idx) { return new ProbabilitiesGate(idx); }
    /**
     * <p>swap.</p>
     *
     * @param a a int
     * @param b a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate swap(int a, int b) { return new Swap(a, b); }
    /**
     * <p>toffoli.</p>
     *
     * @param a a int
     * @param b a int
     * @param c a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate toffoli(int a, int b, int c) { return new Toffoli(a, b, c); }
    /**
     * <p>x.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate x(int idx)  { return new X(idx); }
    /**
     * <p>y.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate y(int idx)  { return new Y(idx); }
    /**
     * <p>z.</p>
     *
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate z(int idx)  { return new Z(idx); }
    /**
     * <p>rotation.</p>
     *
     * @param theta a double
     * @param axis a {@link org.redfx.strange.gate.Rotation.Axes} object
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate rotation(double theta, Rotation.Axes axis, int idx) {return new Rotation(theta, axis, idx);}
    /**
     * <p>rotationX.</p>
     *
     * @param theta a double
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate rotationX(double theta, int idx) {return new RotationX(theta, idx);}
    /**
     * <p>rotationY.</p>
     *
     * @param theta a double
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate rotationY(double theta, int idx) {return new RotationY(theta, idx);}
    /**
     * <p>rotationZ.</p>
     *
     * @param theta a double
     * @param idx a int
     * @return a {@link org.redfx.strange.Gate} object
     */
    static Gate rotationZ(double theta, int idx) {return new RotationZ(theta, idx);}

    /**
     * Set the main qubit where this gate operates on.
     *
     * @param idx the index of the main qubit
     */
    public void setMainQubitIndex(int idx);
    
    /**
     * Return the index of the main qubit this gate acts upon
     *
     * @return the index of the main qubit
     */
    public int getMainQubitIndex();
    
    default Qubit getQubit() {return null;}

    /**
     * <p>setAdditionalQubit.</p>
     *
     * @param idx a int
     * @param cnt a int
     */
    public void setAdditionalQubit(int idx, int cnt);
    
    /**
     * <p>getAffectedQubitIndexes.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<Integer> getAffectedQubitIndexes();

    /**
     * Get the highest index of the qubit that is affected by this gate
     *
     * @return the index of the highest affected qubit
     */
    public int getHighestAffectedQubitIndex();

    /**
     * <p>getCaption.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCaption();
    
    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName();
    
    /**
     * <p>getGroup.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getGroup();
    
    /**
     * <p>getMatrix.</p>
     *
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public Complex[][] getMatrix();
    
    /**
     * Return the number of qubits that are affected by this gate
     *
     * @return the number of qubits that are affected by this gate
     */
    public int getSize();
    
    /**
     * <p>getMatrix.</p>
     *
     * @param qee a {@link org.redfx.strange.QuantumExecutionEnvironment} object
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    default public Complex[][] getMatrix(QuantumExecutionEnvironment qee) {
        return getMatrix();
    }
    
    /**
     * <p>hasOptimization.</p>
     *
     * @return a boolean
     */
    default public boolean hasOptimization() {
        return false;
    }
    
    /**
     * <p>applyOptimize.</p>
     *
     * @param v an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    default public Complex[] applyOptimize(Complex[] v) {
        return null;
    }
    
    /**
     * Defines whether or not this gate should be inversed
     *
     * @param inv the boolean to check if this gate should be inversed or not
     */
    void setInverse(boolean inv);

}
