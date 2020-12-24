import java.util.HashMap;

public class Query {
    private HashMap<String, String> query_variable; // Stores the name of the query variable and it's requested value - <name, requested value>
    private HashMap<String, String> evidence_variables; // Stores the names of the evidence variables and it's given values - <name, given value>
    private int algo; // Indicates which algorithm should be run to resolve this query

    public Query(HashMap<String, String> query_variable, HashMap<String, String> evidence_variables, int algo) {
        this.query_variable = query_variable;
        this.evidence_variables = evidence_variables;
        this.algo = algo;
    }

    /**
     * Returns the name of the query variable and it's requested value
     * @return the name of the query variable and it's requested value
     */
    public HashMap<String, String> getQueryVariable() {
        return query_variable;
    }

    /**
     * Returns a collection of names and values of all given variables (evidence)
     * @return a collection of names and values of all given variables (evidence)
     */
    public HashMap<String, String> getEvidenceVariables() {
        return evidence_variables;
    }

    /**
     * Returns which algorithm should be run to resolve this query
     * @return which algorithm should be run to resolve this query
     */
    public int getAlgo() {
        return algo;
    }
}
