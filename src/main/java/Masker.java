import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;


public class Masker {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar DataMasker.jar <path_to_data> <path_to_rules> \n" +
                    "Example: java -jar DataMasker.jar C:\\Masker\\people.json C:\\Masker\\a.rules.json");
            System.exit(1);
        }

        String dataPathFile = args[0];
        String rulesPathFile = args[1];


//        String rulesPathFile = "C:\\Users\\aryan\\Desktop\\Redgate Intern Assessment\\TestData\\a.rules.json";
//        String dataPathFile = "C:\\Users\\aryan\\Desktop\\Redgate Intern Assessment\\TestData\\people.json";

//        String rulesPathFile = "C:\\Users\\aryan\\Desktop\\Redgate Intern Assessment\\TestData\\b.rules.json";
//        String dataPathFile = "C:\\Users\\aryan\\Desktop\\Redgate Intern Assessment\\TestData\\people.json";

//        String rulesPathFile = "C:\\Users\\aryan\\Desktop\\Redgate Intern Assessment\\TestData\\c.rules.json";
//        String dataPathFile = "C:\\Users\\aryan\\Desktop\\Redgate Intern Assessment\\TestData\\nuts.json";


        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dataDataJson = mapper.readTree(new File(dataPathFile));
            JsonNode rulesDataJson = mapper.readTree(new File(rulesPathFile));

            //convert rules into a list of (category(key, value), pattern(regex))
            List<Rule> rules = helpers.parseRules(rulesDataJson);
            //apply the masking process
            JsonNode maskedData = helpers.maskJson(dataDataJson, rules);

            System.out.println(mapper.writeValueAsString(maskedData));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
