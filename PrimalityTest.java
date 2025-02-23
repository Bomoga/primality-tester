import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

/**
 * This program tests the primality of numbers starting from 10000000000000819 for one hour.
 * It uses both a deterministic primality test (trial division) and a randomized primality test (Fermat’s Little Theorem).
 * The results are written to a CSV file.
 * 
 * @author Adrian Morton
 * PID: 6420259
 * COP 4534 - Algorithm Techniques
 */
public class PrimalityTest {
    private static final BigInteger FIRST = new BigInteger("10000000000000819");
    private static final int ITERATIONS = 10;
    private static final long ONE_HOUR = 3600000;

    public static void main(String[] args) {
        String outputFilename = "output.csv";
        PrintWriter output = null;
        
        //Open the output file for writing
        try {
            output = new PrintWriter(new FileWriter(outputFilename));
            output.println("Value,Time of DA,Prime? (DA),Time of RA,Prime? (RA)");
            
            BigInteger current = FIRST;
            long startTime = System.currentTimeMillis();
            
            //Loop for one hour
            while (System.currentTimeMillis() - startTime < ONE_HOUR) {
                long daStart = System.nanoTime();
                boolean isPrimeDA = deterministicPrimalityTest(current);
                long daTime = System.nanoTime() - daStart;
                
                long raStart = System.nanoTime();
                boolean isPrimeRA = randomizedPrimalityTest(current);
                long raTime = System.nanoTime() - raStart;
                
                output.println(current.toString() + "," + String.format("%.6f", daTime / 1e6) + "," + (isPrimeDA ? "YES" : "NO") + "," + 
                String.format("%.6f", raTime / 1e6) + "," + (isPrimeRA ? "YES" : "NO"));
                
                current = current.add(BigInteger.ONE);
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            if (output != null) {
                output.close();
            }
        }
    }

    //Deterministic primality test (trial division)
    private static boolean deterministicPrimalityTest(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) < 0){ 
            return false;
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))){
            return true;
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)){ 
            return false;
        }
        BigInteger sqrtN = n.sqrt().add(BigInteger.ONE);
        for (BigInteger i = BigInteger.valueOf(3); i.compareTo(sqrtN) <= 0; i = i.add(BigInteger.TWO)) {
            if (n.mod(i).equals(BigInteger.ZERO)){
                return false;
            }
        }
        return true;
    }

    //Randomized primality test (Fermat’s Little Theorem)
    private static boolean randomizedPrimalityTest(BigInteger n) {
        if (n.compareTo(BigInteger.TWO) < 0){
            return false;
        }
        if (n.equals(BigInteger.TWO)){
            return true;
        }
        Random rand = new Random();
        for (int i = 0; i < ITERATIONS; i++) {
            BigInteger a = new BigInteger(n.bitLength(), rand).mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
            if (!a.modPow(n.subtract(BigInteger.ONE), n).equals(BigInteger.ONE)) {
                return false; //Composite
            }
        }
        return true;
    }
}

