import java.text.DecimalFormat;
import java.util.*;

public class VariableElimination {

    private static boolean chekIfCPT(BayesianNetwork network, String query_var_name, Set<String> evidence_var_names){
        Variable query_var = network.getVariable(query_var_name);
        for (String parent : query_var.getParents()) {
            if(!evidence_var_names.contains(parent))
                return false;
        }
        return !query_var.getParents().isEmpty();
//        return evidence_var_names.containsAll(query_var.getParents());
    }

    private static double getResultFromCPT(Variable var, HashMap<String, String> query_var, HashMap<String, String> evidence){
        StringBuilder parents_key = new StringBuilder();
        for (String parent : var.getParents()) {
            parents_key.append(parent).append("=").append(evidence.get(parent)).append(",");
        }
        parents_key = new StringBuilder(parents_key.substring(0, parents_key.length() - 1));
        String self_key = query_var.get(var.getName());
//        return var.getCpt().get(parents_key.toString()).get(self_key);
        return var.getCpt().getEntry(parents_key.toString(),self_key);
    }

    private static String getQueryVarName(HashMap<String, String> query_var){
        Set<String> s = query_var.keySet();
        Iterator<String> it = s.iterator();
        return it.next();
    }

    private static ArrayList<Factor> initialFactors(BayesianNetwork network, HashMap<String, String> evidence){
        ArrayList<Factor> factors = new ArrayList<>();
        for (Variable var : network.getVariables()) {
            Factor f = new Factor(var, evidence);
            if(f.getTable().size()>1)
                factors.add(f);
        }
        return factors;
    }

    private static ArrayList<Factor> hiddenVarFactors(ArrayList<Factor> factors, String hidden_var_name){
        ArrayList<Factor> hidden_var_factors = new ArrayList<>();
        for (Factor factor : factors) {
            if(factor.getName().contains(hidden_var_name))
                hidden_var_factors.add(factor);
        }
        return hidden_var_factors;
    }

    private static Factor join(Factor f1, Factor f2){

        return null;
    }

    private static void eliminate(Factor f, String hidden_var_name){

    }

    private static void normalize(Factor f){

    }

    public static String variableElimination(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence){
        int multi_count = 0;
        int add_count = 0;
        String result = "";
        String query_var_name = getQueryVarName(query_var);
        DecimalFormat df = new DecimalFormat("#.#####");
        if(chekIfCPT(network, query_var_name, evidence.keySet())){
            result = df.format(getResultFromCPT(network.getVariable(query_var_name), query_var, evidence));
        }else {
            ArrayList<Factor> factors = initialFactors(network, evidence);
            ArrayList<String> hidden = new ArrayList<>();
//            ArrayList<ArrayList<String>> hidden_values = new ArrayList<>();
            for (Variable var : network.getNetwork().values()) {
                if (!evidence.containsKey(var.getName()) && !query_var.containsKey(var.getName())) {
                    hidden.add(var.getName());
//                    hidden_values.add(var.getValues());
                }
            }
            Collections.sort(hidden);
            for (String hidden_var_name : hidden) {
                ArrayList<Factor> hidden_var_factors = hiddenVarFactors(factors, hidden_var_name);
                //TODO: What if there is no elements in the ArrayList? Can this happen at all??
                Factor multi_factor = hidden_var_factors.get(0);
                factors.remove(multi_factor);
                for (int i = 1; i < hidden_var_factors.size(); i++) {
                    multi_factor = join(multi_factor, hidden_var_factors.get(i));
                    factors.remove(hidden_var_factors.get(i));
                }
                eliminate(multi_factor, hidden_var_name);
                factors.add(multi_factor);
            }
            Factor final_factor = factors.get(0);
            for (int i = 1; i < factors.size(); i++) {
                final_factor = join(final_factor, factors.get(i));
            }
            normalize(final_factor);
            String key = query_var_name+"="+query_var.get(query_var_name);
            result = df.format(final_factor.getEntry(key));
        }
        int fraction_length = result.substring(result.indexOf('.')+1).length();
        if(fraction_length<5){
            result+="0".repeat(5-fraction_length);
        }
        result += ","+add_count+","+multi_count;
        return result;
    }

}
