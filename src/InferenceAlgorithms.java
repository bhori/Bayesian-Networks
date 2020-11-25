import java.text.DecimalFormat;
import java.util.*;

public class InferenceAlgorithms {

    private static boolean chekIfCPT(BayesianNetwork network, String query_var_name, Set<String> evidence_var_names){
        Variable query_var = network.getVariable(query_var_name);
        for (String parent : query_var.getParents()) {
            if(!evidence_var_names.contains(parent))
                return false;
        }
        return !query_var.getParents().isEmpty();
//        return evidence_var_names.containsAll(query_var.getParents());
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

    public static String simpleInference(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence){
        double sum_of_prob = 0;
        double required_value_prob = 0;
        int multi_count = 0;
        int add_count = 0;
        String result = "";
        String query_var_name = "";
        DecimalFormat df = new DecimalFormat("#.#####");
        Set<String> s = query_var.keySet();
        Iterator<String> it = s.iterator();
        query_var_name = it.next();
        if(chekIfCPT(network, query_var_name, evidence.keySet())){
            StringBuilder parents_key = new StringBuilder();
            Variable var = network.getVariable(query_var_name);
            for (String parent : var.getParents()) {
                parents_key.append(evidence.get(parent)).append(",");
            }
            parents_key = new StringBuilder(parents_key.substring(0, parents_key.length() - 1));
            String self_key = query_var.get(query_var_name);
            required_value_prob = var.getCpt().get(parents_key.toString()).get(self_key);
            result = df.format(required_value_prob);
        }else {
            ArrayList<Variable> free = new ArrayList<>();
            ArrayList<ArrayList<String>> free_values = new ArrayList<>();
            for (Variable var : network.getNetwork().values()) {
                if (!evidence.containsKey(var.getName()) && !query_var.containsKey(var.getName())) {
                    free.add(var);
                    free_values.add(var.getValues());
                }
            }
            for (String value : network.getVariable(query_var_name).getValues()) { // Compute the probability for every value of the query var
                double prob = 0;
                if (free.size() > 0) {
                    List<List<String>> comb = cartesianProduct(free_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
                    for (List<String> combination : comb) {
                        double local_prob = 1;
//                        HashMap<String, String> free_comb = new HashMap<>();
                        HashMap<String, String> current_values;
                        current_values = (HashMap<String, String>) evidence.clone();
                        current_values.put(query_var_name, value);
                        for (int i = 0; i < combination.size(); i++) {
//                        free_comb.put(free.get(i).getName(), combination.get(i));
                            current_values.put(free.get(i).getName(), combination.get(i));
                        }
                        for (Variable v : network.getVariables()) {
                            StringBuilder parents_key = new StringBuilder();
                            for (String parent : v.getParents()) {
                                parents_key.append(current_values.get(parent)).append(",");
                            }
                            if ((parents_key.length() > 0) && (parents_key.charAt(parents_key.length() - 1) == ','))
                                parents_key = new StringBuilder(parents_key.substring(0, parents_key.length() - 1));
                            String self_key = "";
                            self_key = current_values.get(v.getName());
                            local_prob *= v.getCpt().get(parents_key.toString()).get(self_key);
                            multi_count++;
                        }
                        if (multi_count > 0)
                            multi_count--;
                        prob += local_prob;
                        add_count++;
                    }
                } else {
                    double local_prob = 1;
                    HashMap<String, String> current_values;
                    current_values = (HashMap<String, String>) evidence.clone();
                    current_values.put(query_var_name, value);
                    for (Variable v : network.getVariables()) {
                        StringBuilder parents_key = new StringBuilder();
                        for (String parent : v.getParents()) {
                            parents_key.append(current_values.get(parent)).append(",");
                        }
                        if ((parents_key.length() > 0) && (parents_key.charAt(parents_key.length() - 1) == ','))
                            parents_key = new StringBuilder(parents_key.substring(0, parents_key.length() - 1));
                        String self_key = "";
                        self_key = current_values.get(v.getName());
                        local_prob *= v.getCpt().get(parents_key.toString()).get(self_key);
                        multi_count++;
                    }
                    if (multi_count > 0)
                        multi_count--;
                    prob += local_prob;
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
