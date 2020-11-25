import java.util.HashMap;

public class Query {
    private HashMap<String, String> query_variable;
    private HashMap<String, String> evidence_variables;
    private int algo;

    public Query() {
        query_variable = new HashMap<>();
        evidence_variables = new HashMap<>();
    }

    public Query(HashMap<String, String> query_variable, HashMap<String, String> evidence_variables, int algo) {
        this.query_variable = query_variable;
        this.evidence_variables = evidence_variables;
        this.algo = algo;
    }

    public void setQuery_variable(HashMap<String, String> query_variable) {
        this.query_variable = query_variable;
    }

    public void setEvidence_variables(HashMap<String, String> evidence_variables) {
        this.evidence_variables = evidence_variables;
    }

    public void setAlgo(int algo) {
        this.algo = algo;
    }

    public HashMap<String, String> getQuery_variable() {
        return query_variable;
    }

    public HashMap<String, String> getEvidence_variables() {
        return evidence_variables;
    }

    public int getAlgo() {
        return algo;
    }
}
