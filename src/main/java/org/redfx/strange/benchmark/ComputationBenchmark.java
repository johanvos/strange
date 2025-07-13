package org.redfx.strange.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput) // How to measure (ops/sec)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Result unit
@State(Scope.Thread) // Benchmark state is per thread
/**
 *
 * @author johan
 */
public class ComputationBenchmark {
  
    private String someString;

    @Setup
    public void setup() {
        someString = "Hello, JMH!";
    }

    @Benchmark
    public String testStringConcat() {
        return someString + " test";
    }

    @Benchmark
    public String testStringBuilder() {
        return new StringBuilder().append(someString).append(" test").toString();
    }  
}
