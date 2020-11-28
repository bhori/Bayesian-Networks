import java.util.HashMap;

public class CPT {
    private HashMap<String, HashMap<String, Double>> cpt;

    public CPT(){
        cpt = new HashMap<>();
    }

    public void addEntry(String parent_key, String self_key, double probability){
        if(!cpt.containsKey(parent_key))
            cpt.put(parent_key, new HashMap<String, Double>());
        cpt.get(parent_key).put(self_key,probability);
    }

    public HashMap<String, HashMap<String, Double>> getCpt() {
        return cpt;
    }

    public double getEntry(String parent_key, String self_key){
        return cpt.get(parent_key).get(self_key);
    }

    public void printCPT(){
        for (String key : cpt.keySet()) {
            for (String entry : cpt.get(key).keySet()) {
                System.out.println(key+": "+entry+"="+cpt.get(key).get(entry));
            }
        }
//        System.out.println(cpt);
    }
}
