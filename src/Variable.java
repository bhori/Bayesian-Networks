import java.util.ArrayList;


public class Variable {
    private String name;
    private ArrayList<String> values;
    private ArrayList<String> parents;
    private CPT cpt;


    public Variable(String name, ArrayList<String> values, ArrayList<String> parents){
        this.name = name; // Need to change this assignment?
        this.values = new ArrayList<>();
        this.values.addAll(values);
        this.parents = new ArrayList<>();
        this.parents.addAll(parents);
        cpt = new CPT();
    }

    public void addEntry(String parent_key, String self_key, double probability){
        cpt.addEntry(parent_key,self_key, probability);
    }

    public CPT getCpt() {
        return cpt;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return name+"\n"+values+"\n"+parents+"\n";
    }

    public ArrayList<String> getParents() { return parents; }

    public boolean equals(Variable var){
        return name.equals(var.getName());
    }
}
