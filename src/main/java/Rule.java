import java.util.regex.Pattern;

// could be done using a static class within the main class(Masker.java)
/**
 * Simple class to represent the rules.
 * category -> KEY or VALUE
 * pattern -> a regex pattern
 */
public class Rule {
    // Value or Key
    RuleType category;
    Pattern pattern;

    public Rule(RuleType category, Pattern pattern){
        this.category= category;
        this.pattern =pattern;
    }
}