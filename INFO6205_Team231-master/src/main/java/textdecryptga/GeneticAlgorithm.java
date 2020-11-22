package textdecryptga;

import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class GeneticAlgorithm {

    List<Chromosome> population;
    Integer currentGeneration;
    private Integer bestScore = -1;
    private Integer bestScoreChromosomeIndex = -1;
    private String encryptedText;
    private Map<String, Integer> fitnessWeightsMap;
    private int mutationProbability;
    private int crossoverProbability;

    final static Logger logger = Logger.getLogger(GeneticAlgorithm.class);

    /**
     * Constructor
     *
     * @param encryptedTextpath
     * @param fitnessWeightsFile
     * @param alphabets
     * @param populationSize
     * @param crossoverProbability
     * @param mutationProbability
     */
    public GeneticAlgorithm(String encryptedTextpath, String fitnessWeightsFile, String alphabets,
                            Integer populationSize, Integer crossoverProbability, Integer mutationProbability) {
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.fitnessWeightsMap = getFitnessWtMap(fitnessWeightsFile);
        this.encryptedText = getEncryptedText(encryptedTextpath);
        Chromosome.alphabets = alphabets;
        currentGeneration = 0;
        population = initializePopulation(populationSize);
    }

    /**
     * Function initializes population with given generation size
     *
     * @param generationSize
     * @return
     */
    public List<Chromosome> initializePopulation(Integer generationSize) {
        logger.debug("Initializd population with generation size:" + generationSize);
        List<Chromosome> firstGeneration = new ArrayList<>();
        for (int i = 0; i < generationSize; i++) {
            Chromosome chromosome = Chromosome.generateChromosome();
            firstGeneration.add(chromosome);
        }
        return firstGeneration;
    }

    /**
     * Function calculates fitness of each chromosome
     *
     * @param c
     * @return
     */
    public Integer calculateFitness(Chromosome c) {
        Integer score = 0;
        String decryptedText = Decryption.decrypt(c, encryptedText);
        for (String key : fitnessWeightsMap.keySet()) {
            int lastIndex = 0;
            int count = 0;
            while (lastIndex != -1) {

                lastIndex = decryptedText.indexOf(key, lastIndex);

                if (lastIndex != -1) {
                    count++;
                    lastIndex += key.length();
                }
            }
            score += count * fitnessWeightsMap.get(key);
        }
        c.setScore(score);
        return score;
    }

    /**
     * Function evaluates population of chromosomes based on fitness
     * Finds out best score from all
     */
    public void evaluatePopulation() {
        Integer maxScore = -1;
        for (Chromosome c : population) {
            Integer s = calculateFitness(c);
            if (maxScore <= s) {
                bestScoreChromosomeIndex = population.indexOf(c);
                maxScore = s;
            }
        }
        bestScore = maxScore;
        logger.debug("Chromosome with best score is at index:" + bestScoreChromosomeIndex + " and best score:" + bestScore);
        printResult();
    }

    /**
     * Function mutates population by generating new pair of chromosomes
     */
    public void mutatePopulation() {
        ArrayList<Chromosome> newGeneration = new ArrayList<>();
        //we are generating new pair at each interval
        for (int i = 0; i < population.size() / 2; i++) {
            Chromosome c1 = Chromosome.getNextChromosome(population);
            Chromosome c2 = Chromosome.getNextChromosome(population);
            Pair<Chromosome, Chromosome> newChromosomes = generateNewChromosomes(c1, c2);
            newGeneration.add(newChromosomes.getKey());
            newGeneration.add(newChromosomes.getValue());
        }
        population = newGeneration;
        currentGeneration++;
        logger.debug("New generation created with Generation number:" + currentGeneration);
    }

    /**
     * Function performs crossover for two chromosomes
     *
     * @param c1
     * @param c2
     * @return
     */
    public Pair<Chromosome, Chromosome> crossOver(Chromosome c1, Chromosome c2) {
        Random r = new Random();
        Integer randomIndex = r.nextInt(Chromosome.getSize());
        Chromosome c3 = new Chromosome(crossOverString(c1.getShuffledAlphabets(), c2.getShuffledAlphabets(), randomIndex));
        Chromosome c4 = new Chromosome(crossOverString(c2.getShuffledAlphabets(), c1.getShuffledAlphabets(), randomIndex));
        return new Pair<>(c3, c4);
    }

    /**
     * Function used by crossover to crossover alphabet string present in chromosome
     *
     * @param c1
     * @param c2
     * @param randomIndex
     * @return c3
     */
    private String crossOverString(String c1, String c2, Integer randomIndex) {
        StringBuilder c3Part2 = new StringBuilder();
        String c1Part2 = c1.substring(randomIndex);
        for (int i = 0; i < c2.length(); i++) {
            if (c1Part2.indexOf(c2.charAt(i)) != -1)
                c3Part2.append(c2.charAt(i));
        }
        return c1.substring(0, randomIndex) + c3Part2;
    }

    /**
     * Function generates new pair of chromosomes
     *
     * @param c1 - chromosome from which next two will be generated
     * @param c2 - chromosome from which next two will be generated
     * @return - pair of chromosomes
     */
    private Pair<Chromosome, Chromosome> generateNewChromosomes(Chromosome c1, Chromosome c2) {
        Pair<Chromosome, Chromosome> newChromosomes = new Pair<>(c1, c2);
        if (crossoverRequired(crossoverProbability))
            newChromosomes = crossOver(c1, c2);
        if (mutationRequired(mutationProbability))
            newChromosomes.getKey().mutate();
        if (mutationRequired(mutationProbability))
            newChromosomes.getValue().mutate();
        return newChromosomes;
    }

    /**
     * Function decides if mutation is required based on mutation probability given
     *
     * @param mutationProbablity
     * @return
     */
    public boolean mutationRequired(int mutationProbablity) {
        Random r = new Random();
        Integer randomNumber = r.nextInt(100);
        return randomNumber < mutationProbablity;
    }

    /**
     * Function decides if crossover is required based on probability given
     *
     * @param crossoverProbability
     * @return
     */
    public boolean crossoverRequired(int crossoverProbability) {
        Random r = new Random();
        Integer randomNumber = r.nextInt(100);
        return randomNumber < crossoverProbability;
    }

    /**
     * Decrypts text using specific chromosome
     *
     * @param c
     * @return
     */
    public String decryptUsingSpecific(Chromosome c) {
        return Decryption.decrypt(c, encryptedText);
    }

    private void printResult() {
        System.out.println("Generation:" + currentGeneration);
        System.out.println("Best score:" + bestScore);
        System.out.println("Best chromosome index:" + bestScoreChromosomeIndex);
        logger.debug("Output using best chromosome: " + decryptUsingSpecific(population.get(bestScoreChromosomeIndex)));
        System.out.println(decryptUsingSpecific(population.get(bestScoreChromosomeIndex)));
        System.out.println("------------------------------------------------------------------------------");
    }

    /**
     * Returns encrypted text from file
     *
     * @param filepath
     * @return
     */
    public String getEncryptedText(String filepath) {
        StringBuilder encrText = new StringBuilder();
        try {
            InputStream fileStream = getInputStream(filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
            while (br.ready())
                encrText.append(br.readLine()).append(" ");
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return encrText.toString();
    }

    /**
     * Creates fitness map from fitnessWeights file
     *
     * @param fitnessWeightsPath
     * @return
     */
    public Map<String, Integer> getFitnessWtMap(String fitnessWeightsPath) {
        Map<String, Integer> fitnessWeightsMap = new HashMap<>();
        try {
            InputStream fileStream = getInputStream(fitnessWeightsPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));
            while (br.ready()) {
                String[] words = br.readLine().split(" ");
                String key = words[0];
                Integer value = Integer.parseInt(words[1]);
                fitnessWeightsMap.put(key, value);
            }
        } catch (IOException e) {
            logger.error("could not read encrypted text file", e.getCause());
            throw new IllegalArgumentException(e);
        }
        return fitnessWeightsMap;
    }

    private InputStream getInputStream(String relativeFilePath) {
        InputStream fileStream = getClass().getResourceAsStream(relativeFilePath);
        if (fileStream == null)
            throw new IllegalArgumentException("Bad path :" + relativeFilePath);
        return fileStream;
    }

    public int getBestScore() {
        return bestScore;
    }

    public Integer getBestScoreChromosomeIndex() {
        return bestScoreChromosomeIndex;
    }

    public List<Chromosome> getPopulation() {
        return population;
    }

    public Chromosome getBestChromosome() {
        return population.get(bestScoreChromosomeIndex);
    }
}
