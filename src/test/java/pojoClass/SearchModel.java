package pojoClass;

public class SearchModel {
    private String testcase;
    private String descriptions;
    private String keyword;
    private String type;
    private String expectValued;
    private int expectRated;
    private String executed;


    public SearchModel() {}

    public SearchModel(String testcase, String descriptions, String keyword, String type,
                       String expectValued, int expectRated, String executed) {
        this.testcase = testcase;
        this.descriptions = descriptions;
        this.keyword = keyword;
        this.type = type;
        this.expectValued = expectValued;
        this.expectRated = expectRated;
        this.executed = executed;
    }

    // --- GETTERS ---
    public String getTestcase() { return this.testcase; }
    public String getDescriptions() { return descriptions; }
    public String getKeyword() { return keyword; }
    public String getType() { return type; }
    public String getExpectValued() { return expectValued; }
    public int getExpectRated() { return expectRated; }
    public String getExecuted() { return executed; }

    // --- SETTERS ---
    public void setTestcase(String testcase) { this.testcase = testcase; }
    public void setDescriptions(String descriptions) { this.descriptions = descriptions; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setType(String type) { this.type = type; }
    public void setExpectValued(String expectValued) { this.expectValued = expectValued; }
    public void setExpectRated(int expectRated) { this.expectRated = expectRated; }
    public void setExecuted(String executed) { this.executed = executed; }


}
