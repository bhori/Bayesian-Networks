import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface inference_algorithm {

    public static <T> List<List<T>> cartesianProduct(List<T>... lists) {

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
        String result = "";
        double sum_of_prob = 0;
//        double local_prob = 1;
        int mult_count = 0;
        int add_count = 0;
        ArrayList<Variable> free = new ArrayList<Variable>();
        ArrayList<ArrayList<String>> free_values = new ArrayList<ArrayList<String>>();
        for (Variable var: network.getNetwork().values()) {
            if(!evidence.keySet().contains(var.getName())) {
                free.add(var);
                free_values.add(var.getValues());
            }
//                if(!(var.getName().equals(query_var.getName()))&&!(evidence.keySet().contains(var.getName())))
        }
        // TODO: Check if 'query_var.keySet().toString()' gives the name of the variable!!!
        for (String value: network.getVariable(query_var.keySet().toString()).getValues()) { // Compute the probability for every value of the query var
            double prob = 0;
            double local_prob = 1;
            List<List<String>> comb = cartesianProduct(free_values.toArray(new ArrayList[]{new ArrayList<ArrayList<String>>()}));
            for (List<String> combination : comb) {
                HashMap<String, String> free_comb = new HashMap<>();
                for (int i = 0; i < combination.size(); i++) {
                    free_comb.put(free.get(i).getName(), combination.get(i));
                }
                for (Variable v: network.getVariables()) {
                    String parents_key = "";
                    for (String parent: v.getParents()) {
                        if(evidence.containsKey(parent)){
                            parents_key +=evidence.get(parent)+",";
                        }else if (free_comb.containsKey(parent)){
                            parents_key +=free_comb.get(parent)+",";
                        }else{
                            parents_key +=value+",";
                        }
                    }
                    if(parents_key.charAt(parents_key.length()-1) == ',')
                        parents_key=parents_key.substring(0,parents_key.length()-1);
                    String self_key = "";
                    if(evidence.containsKey(v.getName())){
                        self_key=evidence.get(v.getName());
                    }else if(free_comb.containsKey(v.getName())){
                        self_key=free_comb.get(v.getName());
                    }else{
                        self_key=value;
                    }
                    local_prob*=v.getCpt().get(parents_key).get(self_key);
                    mult_count++;
                }
                if(mult_count>0)
                    mult_count--;
                prob+=local_prob;
                add_count++;
            }
            sum_of_prob+=prob;
            add_count++;
        }
//        List<List<Variable>> comb = cartesianProduct(free.toArray(new ArrayList[]{new ArrayList<Variable>()}));
//        for (List<Variable> l1:comb
//             ) {
//            for (Variable v:l1
//                 ) {
//                System.out.println(v.getName());
//            }
//
//        }


        return result;
    }
}
