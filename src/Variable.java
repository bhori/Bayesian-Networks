import java.util.ArrayList;
import java.util.HashMap;


public class Variable {
    private String name;
    private ArrayList<String> values;  //These three attributes below are three disjoint options to store the values of the variable.
//    private HashMap<String, Double> values;
//    private ArrayList<Pair> pair_list_of_values;
    private boolean hasParents;
    private ArrayList<String> parents;
//    private String cpt_;
//    private HashMap<String, HashMap<String, Double>> cpt;
    private CPT cpt;


//    public Variable(){
//        name ="";
//        values=new HashMap<String, Double>();
//        pair_list_of_values = new ArrayList<Pair>();
//        parents = new ArrayList<Variable>();
//        cpt_ = "";
//        cpt = new HashMap<String, HashMap<String, Double>>();
////        cpt.
//    }

    public Variable(String name, ArrayList<String> values, ArrayList<String> parents){
        this.name = name; // Need to change this assignment?
        this.values = new ArrayList<>();
        this.values.addAll(values);
        this.parents = new ArrayList<>();
        this.parents.addAll(parents);
//        cpt = new HashMap<String, HashMap<String, Double>>();
        cpt = new CPT();
    }

    public void addEntry(String parent_key, String self_key, double probability){
////        HashMap<String, Double> entry = new HashMap<String, Double>();
////        entry.put(self_key,probability);
////        cpt.put(parent_key, entry);
//        if(!cpt.containsKey(parent_key))
//            cpt.put(parent_key, new HashMap<String, Double>());
//        cpt.get(parent_key).put(self_key,probability);
        cpt.addEntry(parent_key,self_key, probability);
    }

//    public HashMap<String, HashMap<String, Double>> getCpt() {
//        return cpt;
//    }

    public CPT getCpt() {
        return cpt;
    }


//    public void printCPT(){
//        for (String key : cpt.keySet()) {
//            for (String entry : cpt.get(key).keySet()) {
//                System.out.println(key+": "+entry+"="+cpt.get(key).get(entry));
//            }
//        }
////        System.out.println(cpt);
//    }

    public ArrayList<String> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return name+"\n"+values+"\n"+parents+"\n";
    }

    public ArrayList<String> getParents() {
        return parents;
    }

    public boolean equals(Variable var){
        return name.equals(var.getName());
    }
}
