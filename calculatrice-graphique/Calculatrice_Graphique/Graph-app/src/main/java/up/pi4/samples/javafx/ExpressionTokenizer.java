package up.pi4.samples.javafx;

public class ExpressionTokenizer {
    private String equation;
    private int start; // Début de token
    private int end; // Position de la fin du token

    /**
     * recupere la fonction entrée par l'utilisateur
     * 
     * @param textfield
     */
    public ExpressionTokenizer(String textfield) {
        equation = textfield;
        start = 0;
        end = 0;
        nextToken(); // Avoir le premier token
    }

    public String peekToken() {
        if (start >= equation.length()) {
            return null;
        } else {
            return equation.substring(start, end);
        }
    }

    /**
     * Obtient le token suivant et déplace le token vers le token suivant. On
     * retourne le prochain token ou null s'il n'y a plus de tokens.
     * 
     * @return r le token au sommet
     */
    public String nextToken() {
        String r = peekToken();
        start = end;
        if (start >= equation.length()) {
            return r;
        }
        if (Character.isDigit(equation.charAt(start))) {
            end = start + 1;
            while (end < equation.length()
                    && (Character.isDigit(equation.charAt(end)) || Character.compare(equation.charAt(end), '.') == 0)) {
                end++;
            }
        } // Added this
        else if (Character.isAlphabetic(equation.charAt(start))) {
            end = start + 1;
            while (end < equation.length() && Character.isAlphabetic(equation.charAt(end))) {
                end++;
            }
        } else {
            end = start + 1;
        }
        return r;
    }

}
