package br.com.lucas.alves.encurtador_url.utils;

public class CodificadorUtil {

    private CodificadorUtil() {
        // Private constructor to prevent instantiation
    }

    public static final String toBase62String(long id) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder shortCode = new StringBuilder();
        
        while (id > 0) {
            int remainder = (int) (id % 62);
            shortCode.append(characters.charAt(remainder));
            id /= 62;
        }
        
        return shortCode.reverse().toString(); 
    }
}
