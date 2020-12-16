import java.util.*;

public class Factor {
    private HashMap<String, Double> table; // The table which represents the conditional probability
    private ArrayList<String> name; // List of all the names of the variables in the factor

    /**
     * Constructs factor from the CPT of a variable with changes according to the evidence
     * @param var - The variable
     * @param evidence - the evidence
     */
    public Factor(Variable var, HashMap<String, String> evidence) {
        CPT cpt = var.getCpt();
        table = new HashMap<>();
        name = new ArrayList<>();
        List<String> parent_evidence_list = new ArrayList<>();
        for (String parent : var.getParents()) { // Selects the relevant values from the parents that appear in the evidence, otherwise, takes all.
            if(evidence.containsKey(parent)){
                parent_evidence_list.add(parent + "=" + evidence.get(parent));
            }else{
                name.add(parent);
            }
        }
        if(!evidence.containsKey(var.getName())){
            name.add(var.getName());
            Collections.sort(name);
            for (String value : var.getValues()) {
                for (String parents_key : cpt.getCpt().keySet()) {
                    boolean flag = false;
                    for (String parent : parent_evidence_list) {
                        if(!parents_key.contains(parent)) // if the entry in the CPT contradicts the evidence, ignore it.
                            flag = true;
                    }
                    if(flag) // if the entry in the CPT contradicts the evidence, ignore it.
                        continue;
                    double probability =cpt.getEntry(parents_key, value);
                    for (String parent : parent_evidence_list) { // remove the evidence from the table (from the key)
                        parents_key = parents_key.replace(parent, ""); // remove the evidence from the table (from the key)
                    }
                    //TODO: fix the line numbers in the comment below...
                    if(parents_key.contains(",,")) // Small key fixes (lines 40-46)
                        parents_key = parents_key.replace(",,",",");
                    if(parents_key.startsWith(","))
                        parents_key = parents_key.substring(1);
                    if(parents_key.endsWith(","))
                        parents_key = parents_key.substring(0, parents_key.length()-1);
                    String key = "";
                    int var_index = name.indexOf(var.getName());
                    //TODO: fix the line numbers in the comment below...
                    if((var_index==name.size()-1) && (var_index==0)){ //Inserts the value of 'var' to the key in the right place and creates the key for the new table (lines 59-70)
                        key = var.getName()+"="+value;
                    }else if(var_index==name.size()-1){
                        key = parents_key+","+var.getName()+"="+value;
                    }else if(var_index==0){
                        key = var.getName()+"="+value+","+parents_key;
                    }else{
                        ArrayList<String> s = new ArrayList<>(Arrays.asList(parents_key.split(",")));
                        Collections.sort(s);
                        s.add(var_index, var.getName()+"="+value);
                        key = String.join(",", s.toArray(new String[s.size()]));
                    }
                    table.put(key, probability);
                }
            }
        }else{
            Collections.sort(name);
            String value = evidence.get(var.getName());
            for (String parents_key : cpt.getCpt().keySet()) {
                boolean flag = false;
                for (String parent : parent_evidence_list) {
                    if(!parents_key.contains(parent)) // if the entry in the CPT contradicts the evidence, ignore it.
                        flag = true;
                }
                if(flag) // if the entry in the CPT contradicts the evidence, ignore it.
                    continue;
                double probability = cpt.getEntry(parents_key, value);
                for (String parent : parent_evidence_list) { // remove the evidence from the table (from the key)
                    parents_key = parents_key.replace(parent, ""); // remove the evidence from the table (from the key)
                }
                //TODO: fix the line numbers in the comment below...
                if(parents_key.contains(",,")) // Small key fixes (lines 43-49)
                    parents_key = parents_key.replace(",,",",");
                if(parents_key.startsWith(","))
                    parents_key = parents_key.substring(1);
                if(parents_key.endsWith(","))
                    parents_key = parents_key.substring(0, parents_key.length()-1);
                String key = "";
                //TODO: fix the line numbers in the comment below...
                if(parents_key.contains(",")){ // Creates the key for the new table (lines 99-105)
                    ArrayList<String> s =  new ArrayList<>(Arrays.asList(parents_key.split(",")));
                    Collections.sort(s);
                    key = String.join(",",  s.toArray(new String[s.size()]));
                }else{
                    key = parents_key;
                }
                table.put(key, probability);
            }
        }
    }

    /**
     * Constructs factor from table and name
     * @param name - The name of the factor
     * @param table - the table of the factor
     */
    public Factor(ArrayList<String> name, HashMap<String, Double> table){
        this.name = name;
        Collections.sort(name);
        this.table = table;
    }

    /**
     * Returns the table of the factor
     * @return the table of the factor
     */
    public HashMap<String, Double> getTable() {
        return table;
    }

    /**
     * Returns the name of the factor
     * @return the name of the factor
     */
    public ArrayList<String> getName() {
        return name;
    }

    /**
     * Returns the probability in case that 'key' happened
     * @param key - The requested case
     * @return the probability in case that 'key' happened
     */
    public double getEntry(String key){
        return table.get(key);
    }

}