import java.util.ArrayList;
import java.util.HashMap;

public class BayesianNetwork {
    private  ArrayList<Variable> variables; // List of all the variables in this network
    private HashMap<String, Variable> network;

    /**
     * Constructs empty Bayesian Network
     */
    public BayesianNetwork() {
        network = new HashMap<>();
        variables = new ArrayList<>();
    }

    /**
     * Add new variable to the network
     * @param var - The new variable which need to be added
     */
    public void addVariable(Variable var){
        network.put(var.getName(), var);
        variables.add(var);
    }

    /**
     * Returns a list of all existing variables in this network
     * @return a list of all existing variables in this network
     */
    public ArrayList<Variable> getVariables(){
        return variables;
    }

    public HashMap<String, Variable> getNetwork() {
        return network;
    }

    /**
     * Returns the variable in this network which is name is "name"
     * @param name - The name of the requested variable
     * @return the variable in this network which is name is "name"
     */
    public Variable getVariable(String name){
        return network.get(name);
    }

    public String toString(){
        return network.toString();
    }
}
