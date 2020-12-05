import java.text.DecimalFormat;
import java.util.*;

public class SimpleInference {
    private static int multi_count;

    /**
     * Checks if the query can be resolved directly from the CPT of the query variable
     * @param network - The Bayesian network
     * @param query_var_name - The query variable name
     * @param evidence_var_names - Set of the names of evidence variables
     * @return true if the query can be resolved directly from the CPT of the query variable, Otherwise - return false
     */
    private static boolean chekIfCPT(BayesianNetwork network, String query_var_name, Set<String> evidence_var_names){
        Variable query_var = network.getVariable(query_var_name);
        for (String parent : query_var.getParents()) {
            if(!evidence_var_names.contains(parent))
                return false;
        }
        /* Waiting for answer about that..
        for (Variable var : network.getVariables()) {
            if(var.getParents().contains(query_var_name))
                return false;
        }
         */
        return !query_var.getParents().isEmpty();
    }

    /**
     * Resolves the query directly from the CPT of the query variable
     * @param var - The query variable
     * @param query_var - The name of the query variable and it's requested value // <name, requested value>
     * @param evidence - A collection of names and values of all given variables (evidence) // <name, given value>
     * @return the probability that requested value of the query variable will happen given that given values of the evidence variables happened
     */
    private static double getResultFromCPT(Variable var, HashMap<String, String> query_var, HashMap<String, String> evidence){
        StringBuilder parents_key = new StringBuilder();
        for (String parent : var.getParents()) {
            parents_key.append(parent).append("=").append(evidence.get(parent)).append(",");
        }
//        if(var.getParents().size()>0)
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

    /**
     * Returns the name of the query variable
     * @param query_var - The name of the query variable and it's requested value // <name, requested value>
     * @return the name of the query variable
     */
    private static String getQueryVarName(HashMap<String, String> query_var){
        Set<String> s = query_var.keySet();
        Iterator<String> it = s.iterator();
        return it.next();
    }

    /**
     *
     * @param var
     * @param combination
     * @return
     */
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
            //TODO: Ignore cases in which the probability is zero!
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
            multi_count--; // because the first multiplication is "1*...", this is not part of the multiplication operations of the query calculation
        return local_prob;
    }


    public static String simpleInference(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence){
        double sum_of_prob = 0; // sum of probabilities of the all values of the query variable given values of the evidence variable, used for for the normalization
        double required_value_prob = 0;
        multi_count=0; // Counter for multiplication operation
        int add_count = 0; // Counter for the addition operation
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
                    List<List<String>> all_combinations = cartesianProduct(hidden_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
                    for (List<String> combination : all_combinations) {
                        prob += getLocalProb(query_var_name, value, network, combination, evidence, hidden);
                        add_count++;
                    }
                } else {
                    prob+= getLocalProb(query_var_name, value, network, evidence);
                    add_count++;
                }
                if (value.equals(query_var.get(query_var_name)))
                    required_value_prob = prob;
                if (add_count > 0)
                    add_count--; // because the first addition is "0+...", this is not part of the addition operations of the query calculation
                sum_of_prob += prob;
                add_count++;
            }
            add_count--; // because the first addition is "0+...", this is not part of the addition operations of the query calculation
            result = df.format(required_value_prob/sum_of_prob); // "required_value_prob/sum_of_prob" is for the normalization
        }
        if(result.startsWith("1")){
            result = "1.00000";
        }else {
            int fraction_length = result.substring(result.indexOf('.') + 1).length();
            if (fraction_length < 5) {
                String padding = "0";
                for (int i = 1; i < 5 - fraction_length; i++) {
                    padding += "0";
                }
                result += padding;
            }
//            if (fraction_length < 5) { // if the result of the fraction is shorter than five digits we padding it with zeros
//                result += "0".repeat(5 - fraction_length);
//            }
        }
        result += ","+add_count+","+multi_count;
        return result;
    }
}
