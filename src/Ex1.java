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

    private static BayesianNetwork createNetwork(Scanner in) throws FileNotFoundException {
//        Scanner in = new Scanner(new FileReader("input.txt"));
        in.nextLine(); // "Network"
        in.skip("Variables: ");
        ArrayList<String> variables = new ArrayList<String>(Arrays.asList(in.nextLine().split(",")));
        System.out.println(variables);
        in.nextLine(); // Skip the empty line
        BayesianNetwork network = new BayesianNetwork();
        // Read each variable
        for (int i = 0; i<variables.size(); i++){
            in.skip("Var ");
            String name = in.nextLine();
            in.skip("Values: ");
            ArrayList<String> values = new ArrayList<String>(Arrays.asList(in.nextLine().split(",")));
            System.out.println(values);
            in.skip("Parents: ");
            ArrayList<String> parents;
            String parent = in.nextLine();
            if(parent.equals("none")){
                parents = new ArrayList<String>();
            }else{
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
                if(parents.size()!=0)
                    parent_key = entries.substring(0, entries.indexOf('=') - 1);
                while(entries.contains("=")){
                    entries = entries.substring(entries.indexOf('=')+1);
                    if(entries.contains("=")) {
                        entry = entries.substring(0, entries.indexOf('=') - 1);
                    }else{
                        entry = entries;
                    }
                    self_key = entry.substring(0, entry.indexOf(','));
                    probability = Double.parseDouble(entry.substring(entry.indexOf(',')+1));
                    variable.addEntry(parent_key,self_key,probability);
                    complementary-=probability;
                }
                for (String value : values) {
                    HashMap<String, HashMap<String, Double>> cpt = variable.getCpt();
                    if(!cpt.get(parent_key).containsKey(value))
                        variable.addEntry(parent_key,value,complementary);
                }
            }
            variable.printCPT();
            network.addVariable(variable);
        }
//        System.out.println(network);
        return network;
    }

    private static void saveToFile(String summary) throws IOException {

    }


    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(new FileReader("input.txt"));
        BayesianNetwork network = createNetwork(in);
//        System.out.println("\n\n\n"+network);
        in.nextLine(); // "Queries"
        String query = "";
        int algo;
        while(in.hasNextLine()){
            query = in.nextLine();
            algo = query.charAt(query.length()-1)-'0';
            System.out.println(query+"\n"+algo);
            query = query.substring(query.indexOf('(')+1,query.indexOf(')'));
            System.out.println(query);
            String query_var = query.substring(0, query.indexOf('|'));
            String evidence = query.substring(query.indexOf('|')+1);
        }
    }
}
