import java.util.ArrayList;
import java.util.HashMap;

public class BayesianNetwork {
    private HashMap<String, Variable> network;

    public BayesianNetwork() {
        network = new HashMap<String, Variable>();
    }

    public BayesianNetwork(HashMap<String, Variable> network) {
        this.network = network;
    }

    public void addVariable(Variable var){
        network.put(var.getName(), var);
    }

    public HashMap<String, Variable> getNetwork() {
        return network;
    }

    public Variable getVariable(String name){
        return network.get(name);
    }

    public String toString(){
        String base_network ="";
//        for (Variable var : network) {
//
//        }
        return network.toString();
    }
}
