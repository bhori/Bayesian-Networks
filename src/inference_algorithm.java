import java.util.ArrayList;
import java.util.HashMap;

public interface inference_algorithm {

    public static String simpleInference(BayesianNetwork network, Variable query_var, HashMap<String, String> evidence){
        String result = "";
        ArrayList<Variable> free = new ArrayList<Variable>();
        for (Variable var: network.getNetwork().values()) {
            if(!evidence.keySet().contains(var.getName()))
                free.add(var);
//                if(!(var.getName().equals(query_var.getName()))&&!(evidence.keySet().contains(var.getName())))
        }
        for (String value: network.getVariable(query_var.getName()).getValues()) {
            for (Variable var: free) {
                for (Variable var2: free) {
                    if(!var.equals(var2)) {

                    }
                }
            }
        }


        return result;
    }
}
