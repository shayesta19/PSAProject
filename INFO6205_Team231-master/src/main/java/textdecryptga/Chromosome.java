package textdecryptga;

import lombok.EqualsAndHashCode;

import java.util.*;

@EqualsAndHashCode
public class Chromosome {

    private static final int NUMBER_OF_MUTATIONS = 1;
    static String alphabets = "abcdefghijklmnopqrstuvwxyz";
    private String shuffledAlphabets;
    private Integer score;

    public Chromosome(String shuffledAlphabets) {
        score = 0;
        if (shuffledAlphabets.length() == getSize())
            this.shuffledAlphabets = shuffledAlphabets;
        else
            throw new IllegalArgumentException("Error while initializing Chromosome, please rerun algorithm: " + shuffledAlphabets);
    }

    /**
     * This function gives map of actual alphabets mapped to shuffled alphabets
     *
     * @return
     */
    Map<Character, Character> getMap() {
        Map<Character, Character> map = new HashMap<Character, Character>();
        for (int i = 0; i < shuffledAlphabets.length(); i++)
            map.put(shuffledAlphabets.charAt(i), alphabets.charAt(i));
        return map;
    }

    public static Chromosome getNextChromosome(List<Chromosome> population) {
        List<Integer> allScores = new ArrayList<>();
        Integer fitnessValueSum = 0;
        for (Chromosome c : population) {
            allScores.add(c.getScore());
            fitnessValueSum += c.getScore();
        }
        Integer min = Collections.min(allScores) - 1;

        Random r = new Random();
        Integer randomNumber = r.nextInt(fitnessValueSum - population.size() * min + 1);
        fitnessValueSum = 0;
        for (Chromosome c : population) {
            fitnessValueSum += c.getScore() - min;
            if (randomNumber <= fitnessValueSum)
                return c;
        }
        return null;
    }

    public void mutate() {
        Random r = new Random();
        for (int i = 0; i < NUMBER_OF_MUTATIONS; i++) {
            Integer index1 = r.nextInt(getSize());
            Integer index2 = r.nextInt(getSize());
            this.mutate(index1, index2);
        }
    }

    private void mutate(Integer index1, Integer index2) {
        StringBuilder mutatedString = new StringBuilder(shuffledAlphabets);
        mutatedString.setCharAt(index1, shuffledAlphabets.charAt(index2));
        mutatedString.setCharAt(index2, shuffledAlphabets.charAt(index1));
        shuffledAlphabets = mutatedString.toString();
    }


    public static Chromosome generateChromosome() {
        ArrayList<Character> stringCharacters = new ArrayList<Character>();
        for (Character c : Chromosome.alphabets.toCharArray())
            stringCharacters.add(c);
        Collections.shuffle(stringCharacters);
        StringBuilder alphabetPermutation = new StringBuilder();
        for (Character c : stringCharacters)
            alphabetPermutation.append(c);
        return new Chromosome(alphabetPermutation.toString());
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    Integer getScore() {
        return score;
    }

    public String getShuffledAlphabets() {
        return shuffledAlphabets;
    }

    static Integer getSize() {
        return alphabets.length();
    }
}
