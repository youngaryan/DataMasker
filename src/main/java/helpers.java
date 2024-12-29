import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * series of helper functions which will be used inside main class (Masker.java)
 */
public class helpers {

    /**
     * Parse the rules JSON array into a list of Rule objects.
     */
    public static List<Rule> parseRules(JsonNode rulesDataJson) {

        List<Rule> rules = new ArrayList<>();

        if (!rulesDataJson.isArray()) {
            System.err.println("Rules file must contain a JSON array of strings.\n" +
                    "Example: [\n" +
                    "\t\"k:^nut$\",\n" +
                    "\t\"v:nut\"\n" +
                    "]");
            return rules;
        }

        for (JsonNode ruleNode : rulesDataJson) {
            String ruleStr = ruleNode.asText();
            if (ruleStr.startsWith("k:")) {
                String pattern = ruleStr.substring(2);  // exclude "k:"
                rules.add(new Rule(RuleType.KEY, Pattern.compile(pattern)));
            } else if (ruleStr.startsWith("v:")) {
                String pattern = ruleStr.substring(2);  // exclude "v:"
                rules.add(new Rule(RuleType.VALUE, Pattern.compile(pattern)));
            }
        }

        return rules;
    }

    /**
     * it will recursively mask the JSON if needed.
     * However, as mentioned in the instructions, it will only deal with single hierarchy arrays
     */
    public static JsonNode maskJson(JsonNode node, List<Rule> rules) {
        //for each element in the array, mask it
        for (int i = 0; i < node.size() ; i++) {
            JsonNode child = node.get(i);
            if (child.isObject()) {
                maskObject((ObjectNode) child, rules);
            }
        }
        return node;
    }

    private static void maskObject(ObjectNode objectNode, List<Rule> rules) {
        // collect the field names in a list to avoid concurrent modification
        List<String> fieldNames = new ArrayList<>();
        for (Iterator<String> it = objectNode.fieldNames(); it.hasNext();) {
            fieldNames.add(it.next());
        }

        // for each field, check the rules
        for (String fieldName: fieldNames) {
            JsonNode valueNode = objectNode.get(fieldName);

            if (valueNode.isTextual()) {
                String originalValue = valueNode.asText();
                String maskedValue = originalValue;

                // Check each rule
                for (Rule rule : rules) {
                    switch (rule.category) {
                        case KEY:
                            // if the field name matches the rule, mask the entire value
                            if (rule.pattern.matcher(fieldName).find()) {
                                maskedValue = helpers.maskEntireString(originalValue);
                            }
                            break;
                        case VALUE:
                            // if the value (string) has substrings matching the pattern,
                            // replace those substrings with *s of the same length.
                            maskedValue = maskSubstrings(maskedValue, rule.pattern);
                            break;
                    }
                }

                // update the field with the masked value
                objectNode.put(fieldName, maskedValue);
            }
        }
    }

    private static String maskSubstrings(String original, Pattern pattern) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = pattern.matcher(original);
        while (matcher.find()) {
            String match = matcher.group();
            String maskedSegment = "*".repeat(match.length());
            matcher.appendReplacement(result, maskedSegment);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String maskEntireString(String original) {
        return "*".repeat(original.length());
    }

}
