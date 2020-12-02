import java.text.DecimalFormat;
import java.util.*;

public class VariableElimination {
    private static int multi_count;
    private static int add_count;

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

    private static Set<String> getAncestors(BayesianNetwork network, String var_name){
        Set<String> ancestors = new HashSet<>();
        ancestors.addAll(network.getVariable(var_name).getParents());
        for (String parent : network.getVariable(var_name).getParents()) {
            ancestors.addAll(getAncestors(network, parent));
        }
        return ancestors;
    }

    private static boolean check_relation(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence, Variable var) {
        if (query_var.containsKey(var.getName()) || evidence.containsKey(var.getName()) || getAncestors(network, getQueryVarName(query_var)).contains(var.getName()))
            return true;
        for (String evidence_var : evidence.keySet()) {
            if(getAncestors(network, evidence_var).contains(var.getName()))
                return true;
        }
        return false;
    }

    private static ArrayList<Factor> initialFactors(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence){
        ArrayList<Factor> factors = new ArrayList<>();
        for (Variable var : network.getVariables()) {
            if(check_relation(network, query_var, evidence, var)) {
                Factor f = new Factor(var, evidence);
                if (f.getTable().size() > 1)
                    factors.add(f);
            }
        }
        // Can 'factors' be empty??
        return factors;
    }

    private static ArrayList<Factor> hiddenVarFactors(ArrayList<Factor> factors, String hidden_var_name){
        ArrayList<Factor> hidden_var_factors = new ArrayList<>();
        for (Factor factor : factors) {
            if(factor.getName().contains(hidden_var_name))
                hidden_var_factors.add(factor);
        }
        // Can 'hidden_var_factors' be empty??
        return hidden_var_factors;
    }

    private static ArrayList<String> getCommonVariables(Factor f1, Factor f2){
        ArrayList<String> common_vars = new ArrayList<>();
        for (String var_name : f1.getName()) {
            if(f2.getName().contains(var_name))
                common_vars.add(var_name);
        }
        return common_vars;
    }

    private static ArrayList<ArrayList<String>> getCommonVariablesValues(BayesianNetwork network, ArrayList<String> common_vars) {
        ArrayList<ArrayList<String>> common_variables_values = new ArrayList<>();
        for (String var_name : common_vars) {
            ArrayList<String> tmp = new ArrayList<>();
            for (String value : network.getVariable(var_name).getValues()) {
                tmp.add(new String(value));
            }            for (int i = 0; i < tmp.size(); i++) {
                if(!tmp.get(i).contains("="))
                    tmp.set(i, var_name+"="+tmp.get(i));
            }
            common_variables_values.add(tmp);
        }
        return common_variables_values;
    }

    private static ArrayList<ArrayList<String>> getDifferentVariablesValues(BayesianNetwork network, ArrayList<String> different_vars) {
        ArrayList<ArrayList<String>> different_variables_values = new ArrayList<>();
        for (String var_name : different_vars) {
            ArrayList<String> tmp = new ArrayList<>();
            for (String value : network.getVariable(var_name).getValues()) {
                tmp.add(new String(value));
            }
            for (int i = 0; i < tmp.size(); i++) {
                tmp.set(i, var_name+"="+tmp.get(i));
            }
            different_variables_values.add(tmp);
        }
        return different_variables_values;
    }

    private static ArrayList<String> getDifferentVariables(Factor f1, Factor f2){
        ArrayList<String> different_vars = new ArrayList<>();
        for (String var_name : f1.getName()) {
            if(!f2.getName().contains(var_name))
                different_vars.add(var_name);
        }
        for (String var_name : f2.getName()) {
            if(!f1.getName().contains(var_name))
                different_vars.add(var_name);
        }
        return different_vars;
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

//TODO: Change to private!
    public static Factor join(BayesianNetwork network, Factor f1, Factor f2){
        ArrayList<String> name = new ArrayList<>();
        HashMap<String, Double> table = new HashMap<>();
        ArrayList<String> common_vars = getCommonVariables(f1, f2);
        ArrayList<String> different_vars = getDifferentVariables(f1, f2);
        name.addAll(common_vars);
        name.addAll(different_vars);
        ArrayList<ArrayList<String>> common_vars_values = getCommonVariablesValues(network, common_vars);
        List<List<String>> all_common_vars_combinations = cartesianProduct(common_vars_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
        if(different_vars.size()>0) {
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
        }else{
            for (List<String> common_vars_combination : all_common_vars_combinations) {
                ArrayList<String> key_list = new ArrayList<>(common_vars_combination);
                Collections.sort(key_list);
                String key = String.join(",", key_list.toArray(new String[key_list.size()]));
                double f1_value = f1.getEntry(key);
                double f2_value = f2.getEntry(key);
                table.put(key, f1_value * f2_value);
                multi_count++;
                //Maybe this is redundant..
                key_list.removeAll(common_vars_combination);

            }
        }
        return new Factor(name, table);
    }

    private static Factor eliminate(BayesianNetwork network, Factor f, String hidden_var_name){
        HashMap<String, Double> table = new HashMap<>();
        Set<String> new_keys = new HashSet<>();
        for (String f_key : f.getTable().keySet()) {
            String key = "";
            for (String hidden_var_value : network.getVariable(hidden_var_name).getValues()) {
                if(f_key.contains(hidden_var_name+"="+hidden_var_value)){
                    key = new String(f_key);
                    key = key.replace(hidden_var_name+"="+hidden_var_value, "");
                    if(key.contains(",,"))
                        key = key.replace(",,",",");
                    if(key.startsWith(","))
                        key = key.substring(1);
                    if(key.endsWith(","))
                        key = key.substring(0, key.length()-1);
                }
            }
            new_keys.add(key);
        }
        for (String new_key : new_keys) {
            String key = "";
            double value = 0;
            for (String hidden_var_value : network.getVariable(hidden_var_name).getValues()) {
                ArrayList<String> key_list;
                if(new_key.contains(",")){
                    key_list = new ArrayList<>(Arrays.asList(new_key.split(",")));
                }else{
                    key_list = new ArrayList<>();
                    key_list.add(new_key);
                }
                key_list.add(hidden_var_name+"="+hidden_var_value);
                Collections.sort(key_list);
                if(new_key.length()>0) {
                    key = String.join(",", key_list);
                }else {
                    key = String.join("", key_list);
                }
                value+=f.getEntry(key);
                add_count++;
//                if(f_key.contains(hidden_var_name+"="+hidden_var_value)){
//                    key = new String(f_key);
//                    key = key.replace(hidden_var_name+"="+hidden_var_value, "");
//                    if(key.contains(",,"))
//                        key = key.replace(",,",",");
//                    if(key.startsWith(","))
//                        key = key.substring(1);
//                    if(key.endsWith(","))
//                        key = key.substring(0, key.length()-1);
//                    value+=f.getEntry(f_key);
////                    table.put(key, value);
//                }
            }
            add_count--;
            table.put(new_key, value);
        }

        ArrayList<String> name = f.getName();
        name.remove(hidden_var_name);
        return new Factor(name, table);
    }

    private static Factor normalize(Factor f){
        HashMap<String, Double> table = new HashMap<>();
        double sum_of_values = 0;
        for (double value : f.getTable().values()) {
            sum_of_values+=value;
            add_count++;
        }
        double value;
        for (String key : f.getTable().keySet()) {
            value = f.getEntry(key)/sum_of_values;
            table.put(key, value);
        }
        add_count--;
        return new Factor(f.getName(), table);
    }

    private static ArrayList<Factor> multiply_order(BayesianNetwork network, ArrayList<Factor> hidden_var_factors){
//        ArrayList<ArrayList<Factor>> matrix = new ArrayList<>();
        int[][] matrix = new int[hidden_var_factors.size()][hidden_var_factors.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if(i!=j){
                    Factor f1 = hidden_var_factors.get(i);
                    Factor f2 = hidden_var_factors.get(j);
                    Set<String> name = new HashSet<>(f1.getName());
//                    ArrayList<String> name = f1.getName();
                    name.addAll(f2.getName());
                    int values_product = 1;
                    for (String var_name : name) {
                        values_product*=network.getVariable(var_name).getValues().size();
                    }
                    matrix[i][j]=values_product-Math.max(f1.getTable().size(), f2.getTable().size());
                }
            }
        }
        return null;
    }

    // function to sort hashmap by values
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
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

    private static ArrayList<String> heuristicOrder(BayesianNetwork network, ArrayList<String> hidden_vars){
        ArrayList<String> ordered = new ArrayList<>();
        HashMap<String, Integer> weights = new HashMap<>();
        for (String hidden_var : hidden_vars) {
            ArrayList<Variable> neighbors = new ArrayList<>();
            for (String parent : network.getVariable(hidden_var).getParents())
                neighbors.add(network.getVariable(parent));
            for (Variable var : network.getVariables()) {
                if (var.getParents().contains(hidden_var)){
                    neighbors.add(var);
                    for (String parent : var.getParents()) {
                        if(!parent.equals(hidden_var))
                            neighbors.add(network.getVariable(parent));
                    }
                }
            }
            int weight = 1;
            for (Variable var : neighbors) {
                weight *= var.getValues().size();
            }
            weights.put(hidden_var, weight);
            //TODO: need to remove 'hidden_var' from it's neighbors lists and add edges between any two neighbors of it.
        }
        weights = sortByValue(weights);
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            ordered.add(entry.getKey());
        }
        return ordered;
    }

    public static String variableElimination(BayesianNetwork network, HashMap<String, String> query_var, HashMap<String, String> evidence, boolean with_heuristic){
        multi_count = 0;
        add_count = 0;
        String result = "";
        String query_var_name = getQueryVarName(query_var);
        DecimalFormat df = new DecimalFormat("#.#####");
        if(chekIfCPT(network, query_var_name, evidence.keySet())){
            result = df.format(getResultFromCPT(network.getVariable(query_var_name), query_var, evidence));
        }else {
            ArrayList<Factor> factors = initialFactors(network, query_var, evidence);
            ArrayList<String> hidden_vars = new ArrayList<>();
//            ArrayList<ArrayList<String>> hidden_values = new ArrayList<>();
            for (Variable var : network.getNetwork().values()) {
                if (!evidence.containsKey(var.getName()) && !query_var.containsKey(var.getName())) {
                    hidden_vars.add(var.getName());
//                    hidden_values.add(var.getValues());
                }
            }
            if(with_heuristic){
                hidden_vars = heuristicOrder(network, hidden_vars);
            }else {
                Collections.sort(hidden_vars);
            }
            for (String hidden_var_name : hidden_vars) {
                if(!check_relation(network, query_var, evidence, network.getVariable(hidden_var_name)))
                    continue;
                ArrayList<Factor> hidden_var_factors = hiddenVarFactors(factors, hidden_var_name);
                //TODO: What if there is no elements in the ArrayList? Can this happen at all??
                Factor multi_factor = hidden_var_factors.get(0);
//                Factor multi_factor = hidden_var_factors.get(hidden_var_factors.size()-1);
                factors.remove(multi_factor);
                for (int i = 1; i < hidden_var_factors.size(); i++) {
                    multi_factor = join(network, multi_factor, hidden_var_factors.get(i));
                    factors.remove(hidden_var_factors.get(i));
                }
//                for (int i = hidden_var_factors.size()-2; i >= 0; i--) {
//                    multi_factor = join(network, multi_factor, hidden_var_factors.get(i));
//                    factors.remove(hidden_var_factors.get(i));
//                }
                Factor eliminate_factor = eliminate(network, multi_factor, hidden_var_name);
                factors.add(eliminate_factor);
            }
            Factor final_factor = factors.get(0);
            for (int i = 1; i < factors.size(); i++) {
                final_factor = join(network, final_factor, factors.get(i));
            }
            final_factor = normalize(final_factor);
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
