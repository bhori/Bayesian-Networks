import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Ex1 {

    /**
     * Reads the content of the network and creates the Bayesian network
     * @param in - Scanner object for reading from the text file
     * @return Bayesian network which created from the content in the text file // "input.txt"
     */
    private static BayesianNetwork createNetwork(Scanner in) {
        in.nextLine(); // Skip the "Network" line
        in.skip("Variables: ");
        ArrayList<String> variables = new ArrayList<>(Arrays.asList(in.nextLine().split(",")));
        in.nextLine(); // Skip the empty line
        BayesianNetwork network = new BayesianNetwork();
        // Read each variable
        for (int i = 0; i<variables.size(); i++){
            in.skip("Var ");
            String name = in.nextLine();
            in.skip("Values: ");
            ArrayList<String> values = new ArrayList<>(Arrays.asList(in.nextLine().split(",")));
            in.skip("Parents: ");
            ArrayList<String> parents;
            String parent = in.nextLine();
            if(parent.equals("none")){
                parents = new ArrayList<>();
            }else{
                parents = new ArrayList<>(Arrays.asList(parent.split(",")));
            }
            Variable variable = new Variable(name, values, parents);
            in.nextLine(); // Skip the "CPT:" line
            String entries = "";
            String entry = "";
            double probability = 0;
            while((entries = in.nextLine()).contains("=")){
                double complementary = 1; // For the last value that is not represented in the file
                String parent_key = "";
                String self_key = "";
                if(parents.size()!=0) {
                    parent_key = entries.substring(0, entries.indexOf('=') - 1);
                    String[] parent_arr = parent_key.split(",");
                    for (int j = 0; j < parent_arr.length; j++) {
                        parent_arr[j] = parents.get(j) + "=" + parent_arr[j];
                    }
                    ArrayList<String> s = new ArrayList<>(Arrays.asList(parent_arr));
                    Collections.sort(s);
                    parent_key = String.join(",", s.toArray(new String[s.size()]));
                    Collections.sort(variable.getParents());
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
                    variable.addEntry(parent_key,self_key,probability);
                    complementary-=probability;
                }
                for (String value : values) {
                    HashMap<String, HashMap<String, Double>> cpt = variable.getCpt().getCpt();
                    if(!cpt.get(parent_key).containsKey(value))
                        variable.addEntry(parent_key,value,complementary);
                }
            }
            network.addVariable(variable);
        }
        return network;
    }

    /**
     * Read query from the text file
     * @param in - Scanner object for reading from the text file
     * @return Query which created from the content in the text file // "input.txt"
     */
    private static Query readQuery(Scanner in){
        String str_query;
        str_query = in.nextLine();
        int algo = str_query.charAt(str_query.length()-1)-'0';
        str_query = str_query.substring(str_query.indexOf('(')+1,str_query.indexOf(')'));
        String query_var = str_query.substring(0, str_query.indexOf('|'));
        String evidence = str_query.substring(str_query.indexOf('|')+1);
        String[] q = query_var.split("=");
        HashMap<String, String> query_variable = new HashMap<>();
        query_variable.put(q[0], q[1]);
        String[] e = evidence.split(",");
        HashMap<String, String> evidence_variables = new HashMap<>();
        for (String s : e) {
            evidence_variables.put(s.substring(0, s.indexOf('=')), s.substring(s.indexOf('=')+1));
        }
        return new Query(query_variable, evidence_variables, algo);
    }

    /**
     * Writes the results of the all queries to file // "output.txt"
     * @param summary - String which represents the results of the all queries
     */
    private static void saveToFile(String summary) {
        try {
            PrintWriter pw = new PrintWriter("output.txt");
            pw.write(summary);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(new FileReader("input.txt"));
        BayesianNetwork network = createNetwork(in); // Create the bayesian network
        in.nextLine(); // Skip the "Queries" line
        StringBuilder summary = new StringBuilder();
        boolean with_heuristic;
        while(in.hasNextLine()){
            if(summary.length()>0)
                summary.append("\n");
            Query query = readQuery(in);
            switch (query.getAlgo()) {
                case 1:
                    summary.append(SimpleInference.simpleInference(network, query.getQueryVariable(), query.getEvidenceVariables()));
                    break;
                case 2 :
                    with_heuristic = false;
                    summary.append(VariableElimination.variableElimination(network, query.getQueryVariable(), query.getEvidenceVariables(), with_heuristic));
                    break;
                case 3 :
                    with_heuristic = true;
                    summary.append(VariableElimination.variableElimination(network, query.getQueryVariable(), query.getEvidenceVariables(), with_heuristic));
                    break;
            }
        }
        // TODO: Delete this line before submission!
        System.out.println(summary);
        saveToFile(summary.toString());
    }
}
