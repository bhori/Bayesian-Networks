import java.util.ArrayList;
import java.util.HashMap;

public class BayesianNetwork {
    private  ArrayList<Variable> variables;
    private HashMap<String, Variable> network;

    public BayesianNetwork() {
        network = new HashMap<>();
        variables = new ArrayList<>();
    }

    public BayesianNetwork(HashMap<String, Variable> network) {
        this.network = network;
    }

    public void addVariable(Variable var){
        network.put(var.getName(), var);
        variables.add(var);
    }

    public ArrayList<Variable> getVariables(){
        return variables;
    }

    public HashMap<String, Variable> getNetwork() {
        return network;
    }

    public Variable getVariable(String name){
        return network.get(name);
    }

    public String toString(){
        return network.toString();
    }
}
