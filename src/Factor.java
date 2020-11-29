import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Factor {
    private HashMap<String, Double> table;
    private ArrayList<String> name;
    private int id;
    private static int key = 0;

    public HashMap<String, Double> getTable() {
        return table;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public Factor(Variable var, HashMap<String, String> evidence) {
        id =++key;
        CPT cpt = var.getCpt();
        table = new HashMap<>();
        name = new ArrayList<>();
        String parent_evidence = "";
        for (String parent : var.getParents()) {
            if(evidence.containsKey(parent)){
                if(parent_evidence.equals("")) {
                    parent_evidence += parent + "=" + evidence.get(parent);
                }else{
                    parent_evidence += ","+parent + "=" + evidence.get(parent);

                }
//                name.remove(parent);
            }else{
                name.add(parent);
            }
        }
        if(!evidence.containsKey(var.getName())){
            name.add(var.getName());
//            for (String parent_name: var.getParents()) {
//                name.add(parent_name);
//            }
            Collections.sort(name);
            System.out.println(name.toString());
            for (String value : var.getValues()) {
                for (String parents_key : cpt.getCpt().keySet()) {
                    if(!parents_key.contains(parent_evidence))
                        continue;
                    double probability =cpt.getEntry(parents_key, value);
                    parents_key = parents_key.replace(parent_evidence, "");
                    if(parents_key.contains(",,"))
                        parents_key = parents_key.replace(",,",",");
                    //TODO: What if there is no index '1' in parents_key? Is this can happen?? Same question with endsWith..
                    if(parents_key.startsWith(","))
                        parents_key = parents_key.substring(1);
                    if(parents_key.endsWith(","))
                        parents_key = parents_key.substring(0, parents_key.length()-1);
                    String key = "";
                    int var_index = name.indexOf(var.getName());
                    if((var_index==name.size()-1) && (var_index==0)){
                        key = var.getName()+"="+value;
                    }else if(var_index==name.size()-1){
                        key = parents_key+","+var.getName()+"="+value;
                    }else if(var_index==0){
                        key = var.getName()+"="+value+","+parents_key;
                    }else{
                        ArrayList<String> s = new ArrayList<>(Arrays.asList(parents_key.split(",")));
                        Collections.sort(s);
                        s.add(var_index, value);
                        key = String.join(",", s.toArray(new String[s.size()]));
                    }
                    table.put(key, probability);

                }
            }
        }else{
//            for (String parent_name: var.getParents()) {
//                name.add(parent_name);
//            }
            Collections.sort(name);
            System.out.println(name.toString());
            String value = evidence.get(var.getName());
            for (String parents_key : cpt.getCpt().keySet()) {
                if(!parents_key.contains(parent_evidence))
                    continue;
                double probability = cpt.getEntry(parents_key, value);
                parents_key = parents_key.replace(parent_evidence, "");
                if(parents_key.contains(",,"))
                    parents_key = parents_key.replace(",,",",");
                //TODO: What if there is no index '1' in parents_key? Is this can happen?? Same question with endsWith..
                if(parents_key.startsWith(","))
                    parents_key = parents_key.substring(1);
                if(parents_key.endsWith(","))
                    parents_key = parents_key.substring(0, parents_key.length()-1);
                String key = "";
                if(parents_key.contains(",")){
                    ArrayList<String> s =  new ArrayList<>(Arrays.asList(parents_key.split(",")));
                    Collections.sort(s);
                    key = String.join(",",  s.toArray(new String[s.size()]));
                }else{
                    key = parents_key;
                }
                table.put(key, probability);
            }
        }
        System.out.println(table);
    }

    public double getEntry(String key){
        return table.get(key);
    }

//    private boolean isEvidence(String parent_key, HashMap<String, String> evidence){
//        return evidence.containsKey(parent_key.substring(0,parent_key.indexOf('=')));
//    }
}