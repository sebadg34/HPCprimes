package cl.ucn.disc.hpc.primes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



@Slf4j
public class Parallel {
    /**
     * The Main
     */

    private static  AtomicInteger counter = new AtomicInteger(0);


    public static void main(String[] args) throws InterruptedException {


        
        final int ini = 2;
        final int end = 5 * 1000 * 1000;

        // Finding the numbers of cores
        final int maxCores = Runtime.getRuntime().availableProcessors();
        log.debug ("The max cores: {}.",maxCores);
        log.info ("AMOUNT TO EVALUATE: {}",end);
        
        // number of iterations per core amount
        final int maxIterations = 20;
         float average = 0;
        float[] averages = new float[maxCores + 1];

        // Iterate per core
        for(int usedCores = 1; usedCores <= maxCores + 1; usedCores ++){

            //Iterate 10 times per core, getting the average
            for(int iteration = 0; iteration < maxIterations; iteration ++){
                // The Executor of Threads
                final ExecutorService executor = Executors.newFixedThreadPool(usedCores);
                final StopWatch sw = StopWatch.createStarted();

                // Find if it is prime, from 2 to end
                for(int i = ini; i <=  end; i++){

                    final int n = i;

                    executor.submit(() -> {
                        if (isPrime(n)) {
                            counter.incrementAndGet();
                        }
                    });
                }



                executor.shutdown();

                int maxTime = 5;
                if(executor.awaitTermination(maxTime, TimeUnit.MINUTES)){
                    log.info("Executor ok. {} numbers in {} milliseconds with {} cores", counter, sw.getTime(TimeUnit.MILLISECONDS),usedCores);
                } else {
                    log.warn("The Executor didn't finish in {} minutes", maxTime);
                }
                average+=sw.getTime(TimeUnit.MILLISECONDS);
                counter.set(0);
            }
            // display average time per full iteration
            log.info("FINAL RESULT WITH {} CORES, AVERAGE OF {} MILLISECONDS",usedCores,average/maxIterations);
            log.info("========================================================");
            averages[usedCores-1] = average/maxIterations;
            average = 0;
        }
        log.info("========================================================");
        log.info("AVERAGE TIMES PER CORE: ");
        for (int i = 0; i < maxCores +1; i++){
            log.info("FOR {} cores, Average time of {} milliseconds",i+1,averages[i]);
        }
        log.info("=============END OF PROGRAM EXECUTION===================");
    }


    // Prime calculator
    public static boolean isPrime(final long n){
        // zero isn't prime
        if(n<= 0){
            throw new IllegalArgumentException("Error in n: can't process negative numbers");
        }
        // One isn't prime
        if(n == 1){
            return false;
        }
        if(n== 2){
            return true;
        }
        //any number % 2 -> false
        if(n % 2 == 0) {
            return false;
        }



        // Testing the primalty
        for (long i = 3; (i * i) <= n; i+=2) {
            //n is divisible by i
            if (n % i == 0){
                return false;

            }
        }
        return true;
    }
}
