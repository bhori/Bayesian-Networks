import java.text.DecimalFormat;
import java.util.*;

public class VariableElimination {
    private static int multi_count; // Counts the number of multiplication operations
    private static int add_count; // Counts the number of addition operations

    /**
     * Checks if the query can be resolved directly from the CPT of the query variable
     * @param network            - The Bayesian network
     * @param query_var_name     - The query variable name
     * @param evidence_var_names - Set of the names of evidence variables
     * @return true if the query can be resolved directly from the CPT of the query variable, Otherwise - return false
     */
    private static boolean chekIfCPT(BayesianNetwork network, String query_var_name, Set<String> evidence_var_names) {
        Variable query_var = network.getVariable(query_var_name);
        if (query_var.getParents().size() != evidence_var_names.size())
            return false;
        for (String parent : query_var.getParents()) {
            if (!evidence_var_names.contains(parent))
                return false;
        }
        return !query_var.getParents().isEmpty();
    }

    /**
     * Resolves the query directly from the CPT of the query variable
     * @param var       - The query variable
     * @param query_var - The name of the query variable and it's requested value // <name, requested value>
     * @param evidence  - A collection of names and values of all given variables (evidence) // <name, given value>
     * @return the probability that requested value of the query variable will happen given that given values of the evidence variables happened
     */
    private static double getResultFromCPT(Variable var, HashMap<String, String> query_var, HashMap<String, String> evidence) {
        StringBuilder parents_key = new StringBuilder();
        for (String parent : var.getParents()) {
            parents_key.append(parent).append("=").append(evidence.get(parent)).append(",");
        }
        parents_key = new StringBuilder(parents_key.substring(0, parents_key.length() - 1)); // remove the last "," form "parents_key"
        String self_key = query_var.get(var.getName());
        return var.getCpt().getEntry(parents_key.toString(), self_key);
    }

    /**
     * Returns the name of the query variable from the HasMap.
     * @param query_var - The query variable
     * @return the name of the query variable from the HasMap.
     */
    private static String getQueryVarName(HashMap<String, String> query_var) {
        Set<String> s = query_var.keySet();
        Iterator<String> it = s.iterator();
        return it.next();
    }

    /**
     * Returns all the ancestors of a variable.
     * @param network - The Bayesian network
     * @param var_name - the variable which is ancestors are required
     * @return List of all the ancestors of 'var_name'
     */
    private static Set<String> getAncestors(BayesianNetwork network, String var_name) {
        Set<String> ancestors = new HashSet<>();
        ancestors.addAll(network.getVariable(var_name).getParents());
        for (String parent : network.getVariable(var_name).getParents()) {
            ancestors.addAll(getAncestors(network, parent));
        }
        return ancestors;
    }

    /**
     * Checks if variable is query variable, evidence variable or ancestor of one of them.
     * @param network - The Bayesian network
     * @param query_var - The query variable
     * @param evidence - the evidence variables
     * @param var - the variable which is examined
     * @return true iff 'var' is query variable, evidence variable or ancestor of one of them, else - return false.
     */
    private static boolean check_relation(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence, Variable var) {
        if (query_var.containsKey(var.getName()) || evidence.containsKey(var.getName()) || getAncestors(network, getQueryVarName(query_var)).contains(var.getName()))
            return true;
        for (String evidence_var : evidence.keySet()) {
            if (getAncestors(network, evidence_var).contains(var.getName()))
                return true;
        }
        return false;
    }

    /**
     * Creates the factors from all the variables in the network and returns all the factors with more then one entry.
     * @param network - The Bayesian network
     * @param query_var - The query variable
     * @param evidence - the evidence variables
     * @return all the factors with more then one entry.
     */
    private static ArrayList<Factor> initialFactors(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence) {
        ArrayList<Factor> factors = new ArrayList<>();
        for (Variable var : network.getVariables()) {
            if (check_relation(network, query_var, evidence, var)) {
                Factor f = new Factor(var, evidence);
                if (f.getTable().size() > 1)
                    factors.add(f);
            }
        }
        return factors;
    }

    /**
     * Returns all the factors which contains the hidden variable which is name is 'hidden_var_name'.
     * @param factors - List of all the existing factors.
     * @param hidden_var_name - The hidden variable.
     * @return a list of all the factors which contains the hidden variable which is name is 'hidden_var_name'
     */
    private static ArrayList<Factor> hiddenVarFactors(ArrayList<Factor> factors, String hidden_var_name) {
        ArrayList<Factor> hidden_var_factors = new ArrayList<>();
        for (Factor factor : factors) {
            if (factor.getName().contains(hidden_var_name))
                hidden_var_factors.add(factor);
        }
        return hidden_var_factors;
    }

    /**
     * Returns the names of all the common variables of 'f1' and 'f2'.
     * @param f1 - The first factor.
     * @param f2 - The second factor.
     * @return the names of all the common variables of 'f1' and 'f2'.
     */
    private static ArrayList<String> getCommonVariables(Factor f1, Factor f2) {
        ArrayList<String> common_vars = new ArrayList<>();
        for (String var_name : f1.getName()) {
            if (f2.getName().contains(var_name))
                common_vars.add(var_name);
        }
        return common_vars;
    }

    /**
     * Returns the values of all the common variables.
     * @param network - The Bayesian network
     * @param common_vars - the common variables
     * @return the values of all the common variables
     */
    private static ArrayList<ArrayList<String>> getCommonVariablesValues(BayesianNetwork network, ArrayList<String> common_vars) {
        ArrayList<ArrayList<String>> common_variables_values = new ArrayList<>();
        for (String var_name : common_vars) {
            ArrayList<String> tmp = new ArrayList<>();
            for (String value : network.getVariable(var_name).getValues()) {
                tmp.add(new String(value));
            }
            for (int i = 0; i < tmp.size(); i++) {
                if (!tmp.get(i).contains("="))
                    tmp.set(i, var_name + "=" + tmp.get(i));
            }
            common_variables_values.add(tmp);
        }
        return common_variables_values;
    }

    /**
     * Returns the values of all the different variables.
     * @param network - The Bayesian network
     * @param different_vars - the common variables
     * @return the values of all the different variables
     */
    private static ArrayList<ArrayList<String>> getDifferentVariablesValues(BayesianNetwork network, ArrayList<String> different_vars) {
        ArrayList<ArrayList<String>> different_variables_values = new ArrayList<>();
        for (String var_name : different_vars) {
            ArrayList<String> tmp = new ArrayList<>();
            for (String value : network.getVariable(var_name).getValues()) {
                tmp.add(new String(value));
            }
            for (int i = 0; i < tmp.size(); i++) {
                tmp.set(i, var_name + "=" + tmp.get(i));
            }
            different_variables_values.add(tmp);
        }
        return different_variables_values;
    }

    /**
     * Returns the names of all the different variables of 'f1' and 'f2'.
     * @param f1 - The first factor.
     * @param f2 - The second factor.
     * @return the names of all the different variables of 'f1' and 'f2'.
     */
    private static ArrayList<String> getDifferentVariables(Factor f1, Factor f2) {
        ArrayList<String> different_vars = new ArrayList<>();
        for (String var_name : f1.getName()) {
            if (!f2.getName().contains(var_name))
                different_vars.add(var_name);
        }
        for (String var_name : f2.getName()) {
            if (!f1.getName().contains(var_name))
                different_vars.add(var_name);
        }
        return different_vars;
    }

    // function to get all the combinations of possible values of the hidden variables values (cartesian product), taken from: https://codereview.stackexchange.com/questions/67804/generate-cartesian-product-of-list-in-java
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
     * Computes and returns the multiplication of two factors
     * @param network - The Bayesian network
     * @param f1 - The first factor
     * @param f2 - The second factor
     * @return new factor which is the result of the multiplication of 'f1' and 'f2'
     */
    private static Factor join(BayesianNetwork network, Factor f1, Factor f2) {
        ArrayList<String> name = new ArrayList<>();
        HashMap<String, Double> table = new HashMap<>();
        ArrayList<String> common_vars = getCommonVariables(f1, f2);
        ArrayList<String> different_vars = getDifferentVariables(f1, f2);
        name.addAll(common_vars);
        name.addAll(different_vars);
        ArrayList<ArrayList<String>> common_vars_values = getCommonVariablesValues(network, common_vars);
        List<List<String>> all_common_vars_combinations = cartesianProduct(common_vars_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
        if (different_vars.size() > 0) {
            ArrayList<ArrayList<String>> different_vars_values = getDifferentVariablesValues(network, different_vars);
            List<List<String>> all_different_vars_combinations = cartesianProduct(different_vars_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
            for (List<String> different_vars_combination : all_different_vars_combinations) {
                ArrayList<String> key_list = new ArrayList<>(different_vars_combination);
                for (List<String> common_vars_combination : all_common_vars_combinations) {
                    ArrayList<String> f1_key_list = new ArrayList<>(common_vars_combination);
                    ArrayList<String> f2_key_list = new ArrayList<>(common_vars_combination);
                    for (String var : different_vars_combination) {
                        String[] str;
                        if (var.contains(",")) {
                            str = var.split(",");
                        } else {
                            str = new String[1];
                            str[0] = var;
                        }
                        for (String s : str) {
                            if (f1.getName().contains(s.substring(0, s.indexOf('=')))) {
                                f1_key_list.add(s);
                            } else {
                                f2_key_list.add(s);
                            }
                        }
                    }
                    Collections.sort(f1_key_list);
                    Collections.sort(f2_key_list);
                    String f1_key = String.join(",", f1_key_list.toArray(new String[f1_key_list.size()]));
                    String f2_key = String.join(",", f2_key_list.toArray(new String[f2_key_list.size()]));
                    double f1_value = f1.getEntry(f1_key);
                    double f2_value = f2.getEntry(f2_key);
                    key_list.addAll(common_vars_combination);
                    Collections.sort(key_list);
                    String key = String.join(",", key_list.toArray(new String[key_list.size()]));
                    table.put(key, f1_value * f2_value);
                    multi_count++;
                    key_list.removeAll(common_vars_combination);
                }
            }
        } else {
            for (List<String> common_vars_combination : all_common_vars_combinations) {
                ArrayList<String> key_list = new ArrayList<>(common_vars_combination);
                Collections.sort(key_list);
                String key = String.join(",", key_list.toArray(new String[key_list.size()]));
                double f1_value = f1.getEntry(key);
                double f2_value = f2.getEntry(key);
                table.put(key, f1_value * f2_value);
                multi_count++;
                key_list.removeAll(common_vars_combination);
            }
        }
        return new Factor(name, table);
    }

    /**
     * Computes and returns new factor after elimination of the hidden variable which is is 'hidden_var_name'.
     * @param network - The bayesian network.
     * @param f - The factor that needs to eliminate 'hidden_var_name'.
     * @param hidden_var_name - The name of the variable that need to be eliminated.
     * @return new factor after elimination of the hidden variable which is is 'hidden_var_name'
     */
    private static Factor eliminate(BayesianNetwork network, Factor f, String hidden_var_name) {
        HashMap<String, Double> table = new HashMap<>();
        Set<String> new_keys = new HashSet<>();
        for (String f_key : f.getTable().keySet()) {
            String key = "";
            for (String hidden_var_value : network.getVariable(hidden_var_name).getValues()) {
                if (f_key.contains(hidden_var_name + "=" + hidden_var_value)) {
                    key = new String(f_key);
                    key = key.replace(hidden_var_name + "=" + hidden_var_value, "");
                    if (key.contains(",,"))
                        key = key.replace(",,", ",");
                    if (key.startsWith(","))
                        key = key.substring(1);
                    if (key.endsWith(","))
                        key = key.substring(0, key.length() - 1);
                }
            }
            new_keys.add(key);
        }
        for (String new_key : new_keys) {
            String key = "";
            double value = 0;
            for (String hidden_var_value : network.getVariable(hidden_var_name).getValues()) {
                ArrayList<String> key_list;
                if (new_key.contains(",")) {
                    key_list = new ArrayList<>(Arrays.asList(new_key.split(",")));
                } else {
                    key_list = new ArrayList<>();
                    key_list.add(new_key);
                }
                key_list.add(hidden_var_name + "=" + hidden_var_value);
                Collections.sort(key_list);
                if (new_key.length() > 0) {
                    key = String.join(",", key_list);
                } else {
                    key = String.join("", key_list);
                }
                value += f.getEntry(key);
                add_count++;
            }
            add_count--;
            table.put(new_key, value);
        }
        ArrayList<String> name = f.getName();
        name.remove(hidden_var_name);
        return new Factor(name, table);
    }

    /**
     * Computes and returns new factor after normalization.
     * @param f - The factor that needs to be normalized
     * @return new factor after normalization
     */
    private static Factor normalize(Factor f) {
        HashMap<String, Double> table = new HashMap<>();
        double sum_of_values = 0;
        for (double value : f.getTable().values()) {
            sum_of_values += value;
            add_count++;
        }
        double value;
        for (String key : f.getTable().keySet()) {
            value = f.getEntry(key) / sum_of_values;
            table.put(key, value);
        }
        add_count--;
        return new Factor(f.getName(), table);
    }

    /**
     * Computes and returns which two factors from the hidden variable factors list are most profitable to multiply
     * @param network            - The Bayesian network
     * @param hidden_var_factors - List of factors whose name contains the hidden variable name
     * @return which two factors from the hidden variable factors list are most profitable to multiply
     */
    private static String multiply_order(BayesianNetwork network, ArrayList<Factor> hidden_var_factors) {
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
        for (int i = 0; i < hidden_var_factors.size(); i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < hidden_var_factors.size(); j++) {
                if (i == j) {
                    row.add(Integer.MAX_VALUE); // Multiplying a factor by itself is not an option
                } else {
                    Factor f1 = hidden_var_factors.get(i);
                    Factor f2 = hidden_var_factors.get(j);
                    Set<String> name = new HashSet<>(f1.getName());
                    name.addAll(f2.getName());
                    int values_product = 1;
                    for (String var_name : name) {
                        values_product *= network.getVariable(var_name).getValues().size();
                    }
                    row.add(values_product - Math.max(f1.getTable().size(), f2.getTable().size()));
                }
            }
            matrix.add(i, row);
        }
        ArrayList<Integer> minimum_values = new ArrayList<>();
        for (ArrayList<Integer> arr : matrix) { // Takes the minimum value in each row in the matrix
            minimum_values.add(Collections.min(arr));
        }
        int minimum_value = Collections.min(minimum_values); // finds the minimum value in the 'minimum_values' list
        ArrayList<String> name = new ArrayList<>();
        HashMap<ArrayList<String>, Integer> ascii_values = new HashMap<>(); // Holds the ascii value of all new factors names obtained after multiplication whose value is equal to the 'minimum_value'
        HashMap<ArrayList<String>, String> minimum = new HashMap<>(); // Holds all factor pairs whose multiplication is equal to the 'minimum_value'
        for (int i = 0; i < hidden_var_factors.size(); i++) {
            for (int j = 0; j < hidden_var_factors.size(); j++) {
                if (matrix.get(i).get(j) == minimum_value) {
                    Factor f1 = hidden_var_factors.get(i);
                    Factor f2 = hidden_var_factors.get(j);
                    Set<String> name_set = new HashSet<>(f1.getName());
                    name_set.addAll(f2.getName());
                    name = new ArrayList<>(name_set);
                    Collections.sort(name);
                    int ascii_value = 0;
                    for (String var : name) {
                        for (int k = 0; k < var.length(); k++) {
                            ascii_value += var.charAt(k);
                        }
                    }
                    ascii_values.put(name, ascii_value);
                    minimum.put(name, i + "," + j);
                }
            }
        }
        int min_ascii_value = Integer.MAX_VALUE;
        ArrayList<String> key_name = new ArrayList<>();
        for (ArrayList<String> var_name : ascii_values.keySet()) { // Finds the name of the new factor with the minimum ascii value out of all those whose value is equal to the 'minimum_value'
            if (ascii_values.get(var_name) < min_ascii_value) {
                min_ascii_value = ascii_values.get(var_name);
                key_name = var_name;
            }
        }
        return minimum.get(key_name); // Returns a string that represents the two factors that need to be selected (from 'hidden_var_factors' list) to get the desired factor
    }

    // function to sort hashmap by values, taken from GeeksforGeeks: https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     * Computes and returns the weight of the variable which 'neighbors' list belongs to.
     * @param neighbors - The neighbors list
     * @param evidence - hashmap of the evidence variables and their values
     * @return the weight of the variable which 'neighbors' list belongs to
     */
    private static int weightCalculation(ArrayList<Variable> neighbors, HashMap<String, String> evidence) {
        int weight;
        if (neighbors.size() > 0) {
            weight = 1;
            for (Variable var : neighbors) {
                if(!evidence.containsKey(var.getName())) // if a neighbor is evidence variable, ignore it (because its domain is 1).
                    weight *= var.getValues().size();
            }
        } else {
            weight = 0;
        }
        return weight;
    }

    /**
     * Sorts the hidden variables list according to heuristic function, uses the 'min-weight' heuristic function
     * @param network - The Bayesian network
     * @param hidden_vars - List of all hidden variables names
     * @param evidence - hashmap of the evidence variables and their values
     * @return sorted list of the hidden variables names according to the heuristic function
     */
    private static ArrayList<String> heuristicOrder(BayesianNetwork network, ArrayList<String> hidden_vars, HashMap<String, String> evidence) {
        ArrayList<String> sorted = new ArrayList<>();
        HashMap<String, Integer> weights = new HashMap<>();
        HashMap<String, ArrayList<Variable>> neighbors_lists = new HashMap<>();
        // Builds the initial neighbors lists of the hidden variables and their weights
        for (String hidden_var : hidden_vars) {
            ArrayList<Variable> neighbors = new ArrayList<>();
            for (String parent : network.getVariable(hidden_var).getParents()) // Adds all the parents of 'hidden_var' to it's neighbors list
                neighbors.add(network.getVariable(parent));
            for (Variable var : network.getVariables()) {
                if (var.getParents().contains(hidden_var)) {
                    neighbors.add(var); // Adds all the variables which 'hidden_var' is their parent to it's neighbors list
                    for (String parent : var.getParents()) {
                        if (!parent.equals(hidden_var)) // Adds all variables that are also parents of 'var' to the neighbors list of 'hidden_var'
                            neighbors.add(network.getVariable(parent));
                    }
                }
            }
            weights.put(hidden_var, weightCalculation(neighbors, evidence));
            neighbors_lists.put(hidden_var, neighbors);
        }
        weights = sortByValue(weights);

        Iterator<Map.Entry<String, Integer>> itr = weights.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Integer> entry = itr.next();
            sorted.add(entry.getKey()); // Inserts the minimum-weighted hidden variable to the sorted list
            for (Variable neighbor1 : neighbors_lists.get(entry.getKey())) { // Updates all the neighbor's lists of the hidden variables neighbors of the chosen variable
                if (!hidden_vars.contains(neighbor1.getName()))
                    continue;
                for (Variable neighbor2 : neighbors_lists.get(entry.getKey())) {
                    if (!neighbor1.equals(neighbor2) && neighbors_lists.get(neighbor1.getName()).contains(neighbor2.getName()))
                        neighbors_lists.get(neighbor1.getName()).add(neighbor2); // Adds each variable of the neighbors list of the chosen variable to it's hidden variables neighbors lists
                }
            }
            for (List<Variable> neighbors : neighbors_lists.values()) { // Deletes the chosen variable from all it's hidden variables' neighbors lists
                if (neighbors.contains(network.getVariable(entry.getKey())))
                    neighbors.remove(network.getVariable(entry.getKey()));
            }
            itr.remove(); // Delete the chosen variable entry from the 'weights' map
            for (String key : weights.keySet()) {  // Updates the weights on the map after rebuilding the lists of neighbors
                if (!key.equals(entry.getKey())) {
                    weights.put(key, weightCalculation(neighbors_lists.get(key), evidence));
                }
            }
            weights = sortByValue(weights);
        }
        return sorted;
    }

    /**
     * Solves the query (using Variable Elimination algorithm (with alphabet/heuristic order)) and returns the result with the number of addition operations and multiplication operations used in the calculation
     * @param network - the bayesian network
     * @param query_var - the query variable name and it's requested value
     * @param evidence - hashmap of the evidence variables and their values
     * @return the result of the query with the number of addition operations and multiplication operations used in the calculation
     */
    public static String variableElimination(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence, boolean with_heuristic) {
        multi_count = 0;
        add_count = 0;
        String result = "";
        String query_var_name = getQueryVarName(query_var);
        DecimalFormat df = new DecimalFormat("#.#####");
        if (chekIfCPT(network, query_var_name, evidence.keySet())) {
            result = df.format(getResultFromCPT(network.getVariable(query_var_name), query_var, evidence));
        } else {
            ArrayList<Factor> factors = initialFactors(network, query_var, evidence);
            ArrayList<String> hidden_vars = new ArrayList<>();
            for (Variable var : network.getNetwork().values()) {
                if (!evidence.containsKey(var.getName()) && !query_var.containsKey(var.getName())) {
                    hidden_vars.add(var.getName());
                }
            }
            if (with_heuristic) { // for algorithm 3
                hidden_vars = heuristicOrder(network, hidden_vars, evidence);
            } else { // for algorithm 2
                Collections.sort(hidden_vars);
            }
            for (String hidden_var_name : hidden_vars) {
                if (!check_relation(network, query_var, evidence, network.getVariable(hidden_var_name))) // variable which is not ancestor of query or evidence variable is irrelevant
                    continue;
                ArrayList<Factor> hidden_var_factors = hiddenVarFactors(factors, hidden_var_name);
                if(!(hidden_var_factors.size() > 1))
                    factors.remove(hidden_var_factors.get(0));
                while (hidden_var_factors.size() > 1) {
                    String chosen_factors = multiply_order(network, hidden_var_factors);
                    Factor f1 = hidden_var_factors.get(Integer.parseInt(chosen_factors.substring(0, chosen_factors.indexOf(','))));
                    Factor f2 = hidden_var_factors.get(Integer.parseInt(chosen_factors.substring(chosen_factors.indexOf(',') + 1)));
                    Factor multi_factor = join(network, f1, f2);
                    factors.remove(f1);
                    factors.remove(f2);
                    hidden_var_factors.remove(f1);
                    hidden_var_factors.remove(f2);
                    hidden_var_factors.add(multi_factor);
                }
                Factor eliminate_factor = eliminate(network, hidden_var_factors.get(0), hidden_var_name);
                if (eliminate_factor.getTable().size() > 1) {
                    factors.add(eliminate_factor);
                }
            }
            Factor final_factor = factors.get(0);
            for (int i = 1; i < factors.size(); i++) {
                final_factor = join(network, final_factor, factors.get(i));
            }
            final_factor = normalize(final_factor);
            String key = query_var_name + "=" + query_var.get(query_var_name);
            result = df.format(final_factor.getEntry(key));
        }
        if (result.startsWith("1")) {
            result = "1.00000";
        } else if (!result.contains(".")) {
            result = "0.00000";
        } else {
            int fraction_length = result.substring(result.indexOf('.') + 1).length();
            if (fraction_length < 5) { // If the number of digits after the decimal point is less than five, padding with zeros
                String padding = "0";
                for (int i = 1; i < 5 - fraction_length; i++) {
                    padding += "0";
                }
                result += padding;
            }
        }
        result += "," + add_count + "," + multi_count;
        return result;
    }

}
