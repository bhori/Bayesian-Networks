import java.util.HashMap;

public class CPT {
    private HashMap<String, HashMap<String, Double>> cpt; // The table which represents the conditional probability

    /**
     * Constructs empty CPT
     */
    public CPT(){
        cpt = new HashMap<>();
    }

    /**
     * Add new entry to the table (the cpt)
     * @param parent_key - The values of the parents
     * @param self_key - The value of the variable to which the table belongs
     * @param probability - The probability in this case
     */
    public void addEntry(String parent_key, String self_key, double probability){
        if(!cpt.containsKey(parent_key))
            cpt.put(parent_key, new HashMap<>());
        cpt.get(parent_key).put(self_key,probability);
    }

    /**
     * Returns this table
     * @return this table
     */
    public HashMap<String, HashMap<String, Double>> getCpt() {
        return cpt;
    }

    /**
     * Returns the probability that 'self_key' will happen given that 'parents_key' happened
     * @param parent_key - The values of the parents
     * @param self_key - The value of the variable to which the table belongs
     * @return the probability that 'self_key' will happen given that 'parents_key' happened
     */
    public double getEntry(String parent_key, String self_key){
        return cpt.get(parent_key).get(self_key);
    }

//    public void printCPT(){
//        for (String key : cpt.keySet()) {
//            for (String entry : cpt.get(key).keySet()) {
//                System.out.println(key+": "+entry+"="+cpt.get(key).get(entry));
//            }
//        }
//    }
}
