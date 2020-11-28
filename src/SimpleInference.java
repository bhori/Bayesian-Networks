import java.text.DecimalFormat;
import java.util.*;

public class SimpleInference {
    private static int multi_count;
//    private int multi_count = 0;

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

    private static <T> List<List<T>> cartesianProduct(List<T>... lists) {

        List<List<T>> product = new ArrayList<List<T>>();

        for (List<T> list : lists) {

            List<List<T>> newProduct = new ArrayList<List<T>>();

            for (T listElement : list) {

                if (product.isEmpty()) {

                    List<T> newProductList = new ArrayList<T>();
                    newProductList.add(listElement);
                    newProduct.add(newProductList);
                } else {

                    for (List<T> productList : product) {

                        List<T> newProductList = new ArrayList<T>(productList);
                        newProductList.add(listElement);
                        newProduct.add(newProductList);
                    }
                }
            }

            product = newProduct;
        }

        return product;
    }

    private static String getQueryVarName(HashMap<String, String> query_var){
        Set<String> s = query_var.keySet();
        Iterator<String> it = s.iterator();
        return it.next();
    }

    private static double getVarProb(Variable var, HashMap<String, String> combination){
        StringBuilder parents_key = new StringBuilder();
        for (String parent : var.getParents()) {
            parents_key.append(parent).append("=").append(combination.get(parent)).append(",");
        }
        if ((parents_key.length() > 0) && (parents_key.charAt(parents_key.length() - 1) == ','))
            parents_key = new StringBuilder(parents_key.substring(0, parents_key.length() - 1));
        String self_key = "";
        self_key = combination.get(var.getName());
//        return var.getCpt().get(parents_key.toString()).get(self_key);
        return var.getCpt().getEntry(parents_key.toString(),self_key);
    }

    private static double getLocalProb(String query_var_name, String query_var_value, BayesianNetwork network, List<String> combination, HashMap<String, String> evidence, ArrayList<Variable> hidden){
        double local_prob = 1;
        HashMap<String, String> current_values;
        current_values = (HashMap<String, String>) evidence.clone();
        current_values.put(query_var_name, query_var_value);
        for (int i = 0; i < combination.size(); i++) {
//                        hidden_comb.put(hidden.get(i).getName(), combination.get(i));
            current_values.put(hidden.get(i).getName(), combination.get(i));
        }
        for (Variable v : network.getVariables()) {
            local_prob *= getVarProb(v, current_values);
            multi_count++;
        }
        if (multi_count > 0)
            multi_count--;
        return local_prob;
    }

    private static double getLocalProb(String query_var_name, String query_var_value, BayesianNetwork network, HashMap<String, String> evidence){
        double local_prob = 1;
        HashMap<String, String> current_values;
        current_values = (HashMap<String, String>) evidence.clone();
        current_values.put(query_var_name, query_var_value);
        for (Variable v : network.getVariables()) {
            local_prob *= getVarProb(v, current_values);
            multi_count++;
        }
        if (multi_count > 0)
            multi_count--;
        return local_prob;
    }


    public static String simpleInference(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence){
        double sum_of_prob = 0;
        double required_value_prob = 0;
//        int multi_count = 0;
        multi_count=0;
        int add_count = 0;
        String result = "";
        String query_var_name = getQueryVarName(query_var);
        DecimalFormat df = new DecimalFormat("#.#####");
        if(chekIfCPT(network, query_var_name, evidence.keySet())){
            result = df.format(getResultFromCPT(network.getVariable(query_var_name), query_var, evidence));
        }else {
            ArrayList<Variable> hidden = new ArrayList<>();
            ArrayList<ArrayList<String>> hidden_values = new ArrayList<>();
            for (Variable var : network.getNetwork().values()) {
                if (!evidence.containsKey(var.getName()) && !query_var.containsKey(var.getName())) {
                    hidden.add(var);
                    hidden_values.add(var.getValues());
                }
            }
            for (String value : network.getVariable(query_var_name).getValues()) { // Compute the probability for every value of the query var
                double prob = 0;
                if (hidden.size() > 0) {
                    List<List<String>> comb = cartesianProduct(hidden_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
                    for (List<String> combination : comb) {
                        prob+= getLocalProb(query_var_name, value, network, combination, evidence, hidden);
                        add_count++;
                    }
                } else {
                    prob+= getLocalProb(query_var_name, value, network, evidence);
                    add_count++;
                }
                if (value.equals(query_var.get(query_var_name)))
                    required_value_prob = prob;
                if (add_count > 0)
                    add_count--;
                sum_of_prob += prob;
                add_count++;
            }
            add_count--;
            result = df.format(required_value_prob/sum_of_prob);
        }
        int fraction_length = result.substring(result.indexOf('.')+1).length();
        if(fraction_length<5){
            result+="0".repeat(5-fraction_length);
        }
        result += ","+add_count+","+multi_count;
        return result;
    }
}
