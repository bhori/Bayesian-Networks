import java.util.ArrayList;


public class Variable {
    private String name;
    private ArrayList<String> values;
    private ArrayList<String> parents;
    private CPT cpt;


    /**
     * Constructs Variable
     * @param name - The name of the variable
     * @param values - List of all the possible values of the variable
     * @param parents - List of all the parents of the variable
     */
    public Variable(String name, ArrayList<String> values, ArrayList<String> parents){
        this.name = name;
        this.values = new ArrayList<>();
        this.values.addAll(values);
        this.parents = new ArrayList<>();
        this.parents.addAll(parents);
        cpt = new CPT();
    }

    /**
     * Add new entry to the CPT of this variable
     * @param parent_key - The values of the parents
     * @param self_key - This variable value
     * @param probability - The probability in this case
     */
    public void addEntry(String parent_key, String self_key, double probability){
        cpt.addEntry(parent_key,self_key, probability);
    }

    /**
     * Returns the CPT of this variable
     * @return the CPT of this variable
     */
    public CPT getCpt() {
        return cpt;
    }

    /**
     * Returns a list of all possible values for this variable
     * @return a list of all possible values for this variable
     */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
     * Returns the name of this variable
     * @return the name of this variable
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of all parents of this variable
     * @return a list of all parents of this variable
     */
    public ArrayList<String> getParents() { return parents; }

    /**
     * Cehcks if this variable is equals to another variable
     * @param var - The variable being compared to
     * @return True if this two variables equals, Otherwise - return false
     */
    public boolean equals(Variable var){
        return name.equals(var.getName());
    }
}
