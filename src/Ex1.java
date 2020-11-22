import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Ex1 {

    private static void readVariable(){

    }

    private static void readInput() throws FileNotFoundException {
        Scanner in = new Scanner(new FileReader("input.txt"));
//        in.skip("Network");
        in.nextLine(); // "Network"
        in.skip("Variables: ");
//        in.next(); // "Variables:"
//        in.next();
//        in.useDelimiter(",");
        ArrayList<String> variables = new ArrayList<String>(Arrays.asList(in.nextLine().split(",")));
        System.out.println(variables);
        in.nextLine(); // Skip the empty line
        // Read each variable
        for (int i = 0; i<variables.size(); i++){
            in.skip("Var ");
            String name = in.nextLine();
            in.skip("Values: ");
            ArrayList<String> values = new ArrayList<String>(Arrays.asList(in.nextLine().split(",")));
            System.out.println(values);
            in.skip("Parents: ");
//            boolean hasParents;
            ArrayList<String> parents;
            String parent = in.nextLine();
            if(parent.equals("none")){
//                hasParents = false;
                parents = new ArrayList<String>();
            }else{
//                hasParents = true;
                parents = new ArrayList<String>(Arrays.asList(parent.split(",")));
                System.out.println(parents);
            }
            Variable variable = new Variable(name, values, parents);
            in.nextLine(); // "CPT:"
            String entries = "";
            String entry = "";
            double probability = 0;
            // TODO: if there is no parents then the '=' is the first character! I need to take care of that!!
            while((entries = in.nextLine()).contains("=")){
                double complementary = 1;
                String parent_key = "";
                String self_key = "";
                if(parents.size()!=0){
                    parent_key = entries.substring(0, entries.indexOf('=') - 1);
                }
                while(entries.contains("=")){
                    entries = entries.substring(entries.indexOf('=')+1);
                    if(entries.contains("=")) {
                        entry = entries.substring(0, entries.indexOf('=') - 1);
                    }else{
                        entry = entries;
                    }
                    self_key = entry.substring(0, entry.indexOf(','));
                    probability = Double.parseDouble(entry.substring(entry.indexOf(',')+1));
                    System.out.println(self_key+","+probability);
                    variable.addEntry(parent_key,self_key,probability);
                    complementary-=probability;
                }
                for (String value : values) {
                    HashMap<String, HashMap<String, Double>> cpt = variable.getCpt();
                    if(!cpt.get(parent_key).containsKey(value))
                        variable.addEntry(parent_key,value,complementary);
                }
//                variable.addEntry(parent_key,"false",complementary);
//                variable.addEntry(parent_key,self_key,complementary);
            }
            variable.printCPT();
//            break;
        }
    }

    private static void saveToFile(String summary) throws IOException {

    }


    public static void main(String[] args) throws IOException {
        readInput();
    }
}
