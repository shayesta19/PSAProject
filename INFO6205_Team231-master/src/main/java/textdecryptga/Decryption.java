package textdecryptga;

import java.util.Map;

public class Decryption {

    public static String decrypt(Chromosome c, String encryptedText){
        StringBuilder decryptedText = new StringBuilder();
        Map<Character,Character> keyMap = c.getMap();
        for (int i = 0; i < encryptedText.length(); i++) {
            Character ch  = keyMap.get(encryptedText.charAt(i));
            if(ch == null)
                ch = encryptedText.charAt(i);
            decryptedText.append(ch);
        }
        return decryptedText.toString();
    }
}
