package tests;

import javafx.util.Pair;
import lombok.ToString;
import org.junit.Test;
import textdecryptga.Chromosome;
import textdecryptga.Decryption;
import textdecryptga.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestCases {

    GeneticAlgorithm enviroment = new GeneticAlgorithm("/EncryptedText",
            "/FitnessWeights"
            , "abcdefghijklmnopqrstuvwxyz", 500, 100, 50);

    /**
     * Generates a perfect chromosome with expected key and
     * compares it with max fitness value
     */
    @Test
    public void testFitness() {
        assertEquals(Integer.valueOf(43), enviroment.calculateFitness(new Chromosome("phqgiumeaylnofdxjkrcvstzwb")));
    }

    /**
     * The average best score for ten generation
     * is nearly equal to 10 where max score is 26
     */
    @Test
    public void testBestScore() {
        int sumofBest = 0;
        for (int generation = 0; generation < 10; generation++) {
            enviroment.evaluatePopulation();
            sumofBest = sumofBest + enviroment.getBestScore();
        }
        assertEquals(10, sumofBest / 10, 5);
    }

    /**
     * Function tests if decryption function is working
     */
    @Test
    public void testDecrypt() {
        String encrText = enviroment.getEncryptedText("/EncryptedText");
        String decrText = Decryption.decrypt(Chromosome.generateChromosome(), encrText);
        assertNotEquals(encrText, decrText);
    }

    /**
     * Function tests if decryption function is working
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDecryptFailsForBadPath() {
        enviroment.getEncryptedText("dummy");
    }

    /**
     * Test to check mutation is changing chromosomes
     */
    @Test
    public void testMutate() {
        List<Chromosome> population1 = enviroment.getPopulation();
        enviroment.mutatePopulation();
        List<Chromosome> population2 = enviroment.getPopulation();
        assertNotEquals(population1, population2);
    }

    /**
     * Test to find crossover function is changing chromosomes
     */
    @Test
    public void testCrossover() {
        Chromosome c1 = Chromosome.generateChromosome();
        Chromosome c2 = Chromosome.generateChromosome();
        Pair<Chromosome, Chromosome> newChromosomes = enviroment.crossOver(c1, c2);
        assertNotEquals(newChromosomes.getKey(), c1);
        assertNotEquals(newChromosomes.getValue(), c2);
    }

    /**
     * Test to check initialization function
     */
    @Test
    public void testInitialization() {
        List<Chromosome> population = enviroment.initializePopulation(20);
        assertEquals(population.size(), 20);
        assertNotNull(population);
    }

    /**
     * Test to check file path
     */
    @Test(expected = IllegalArgumentException.class)
    public void failForBadFilePathForFitnessWt() {
        enviroment.getFitnessWtMap("dummy");

    }

    /**
     * Test to check constructor
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotCreateNewChromosomeForWrongSize() {
        new Chromosome("abcedefghijklmnopqrstuvwxyzabcde");
    }

    /**
     * Test to check constructor
     */
    @Test
    public void mutationChangesStrings() {
        String input = "abcd";
        Chromosome chromosome = new Chromosome("abcdefghijklmnopqrstuvwxyz");
        chromosome.mutate();
        assertNotEquals("mutation did not happen", chromosome.getShuffledAlphabets(), input);
    }


}
