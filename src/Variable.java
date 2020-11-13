import java.util.ArrayList;
import java.util.HashMap;


public class Variable {
    private String name;
    private ArrayList<String> values_;  //These three attributes below are three disjoint options to store the values of the variable.
    private HashMap<String, Double> values;
    private ArrayList<Pair> pair_list_of_values;
    private ArrayList<Variable> parents;
    private String cpt_;
    private HashMap<String, HashMap<ArrayList<Variable>, Double>> cpt;

    public Variable(){
        name ="";
        values=new HashMap<String, Double>();
        pair_list_of_values = new ArrayList<Pair>();
        parents = new ArrayList<Variable>();
        cpt_ = "";
        cpt = new HashMap<String, HashMap<ArrayList<Variable>, Double>>();
//        cpt.
    }

}
