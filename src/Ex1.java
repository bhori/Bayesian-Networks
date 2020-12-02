import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Ex1 {

    private static void readVariable(){

    }

    private static BayesianNetwork createNetwork(Scanner in) {
//        Scanner in = new Scanner(new FileReader("input.txt"));
        in.nextLine(); // "Network"
        in.skip("Variables: ");
        ArrayList<String> variables = new ArrayList<>(Arrays.asList(in.nextLine().split(",")));
//        System.out.println(variables);
        in.nextLine(); // Skip the empty line
        BayesianNetwork network = new BayesianNetwork();
        // Read each variable
        for (int i = 0; i<variables.size(); i++){
            in.skip("Var ");
            String name = in.nextLine();
            in.skip("Values: ");
            ArrayList<String> values = new ArrayList<>(Arrays.asList(in.nextLine().split(",")));
//            System.out.println(values);
            in.skip("Parents: ");
            ArrayList<String> parents;
            String parent = in.nextLine();
            if(parent.equals("none")){
                parents = new ArrayList<>();
            }else{
                parents = new ArrayList<>(Arrays.asList(parent.split(",")));
//                System.out.println(parents);
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
                if(parents.size()!=0) {
                    parent_key = entries.substring(0, entries.indexOf('=') - 1);
                    String[] parent_arr = parent_key.split(",");
                    for (int j = 0; j < parent_arr.length; j++) {
                        parent_arr[j] = parents.get(j) + "=" + parent_arr[j];
                    }
                    parent_key = String.join(",", parent_arr);
                }
//                System.out.println(parent_key);
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
//            variable.printCPT();
            network.addVariable(variable);
        }
//        System.out.println(network);
        return network;
    }

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
        BayesianNetwork network = createNetwork(in);
        in.nextLine(); // "Queries"
        StringBuilder summary = new StringBuilder();
//        int i = 1;
        boolean with_heuristic = false;
        while(in.hasNextLine()){
            if(summary.length()>0)
                summary.append("\n");
            Query query = readQuery(in);
////            System.out.println(new Factor(network.getVariable(query.getQueryVariable().keySet().toString().substring(1,2)), query.getEvidenceVariables()));
////            System.out.println(new Factor(network.getVariable("A"), query.getEvidenceVariables()));
//            System.out.println("\n"+new Factor(network.getVariable("B"), query.getEvidenceVariables()));
//            System.out.println("\n"+new Factor(network.getVariable("E"), query.getEvidenceVariables()));
//            Factor r = new Factor(network.getVariable("A"), query.getEvidenceVariables());
//            System.out.println("\n"+r);
//            System.out.println("\n"+new Factor(network.getVariable("J"), query.getEvidenceVariables()));
//            System.out.println("\n"+new Factor(network.getVariable("M"), query.getEvidenceVariables()));
//            if(i==1) {
//                Factor f = VariableElimination.join(network, new Factor(network.getVariable("J"), query.getEvidenceVariables()), new Factor(network.getVariable("M"), query.getEvidenceVariables()));
//                System.out.println("***************\n" + VariableElimination.join(network, f, r));
//            }
            switch (query.getAlgo()){
                case 1:
                    summary.append(SimpleInference.simpleInference(network, query.getQueryVariable(), query.getEvidenceVariables()));
                    break;
                case 2:
                    with_heuristic = false;
                    summary.append(VariableElimination.variableElimination(network, query.getQueryVariable(), query.getEvidenceVariables(), with_heuristic));
                    break;
                case 3:
                    with_heuristic = true;
                    summary.append(VariableElimination.variableElimination(network, query.getQueryVariable(), query.getEvidenceVariables(), with_heuristic));
                    break;
            }

//            if(i==2 || i==5) {
//                summary.append(VariableElimination.variableElimination(network, query.getQueryVariable(), query.getEvidenceVariables(), with_heuristic));
//            }else{
//                summary.append(SimpleInference.simpleInference(network, query.getQueryVariable(), query.getEvidenceVariables()));
//            }
//            summary.append(SimpleInference.simpleInference(network, query.getQueryVariable(), query.getEvidenceVariables()));
//            i++;
        }
        System.out.println(summary);
        saveToFile(summary.toString());
    }
}
