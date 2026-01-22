package pojoClass;

public class LoginModel {
    private String testcode;
    private String descriptions;
    private String email;
    private String password;
    private String execute;
    public LoginModel(){}

    public String getTestcode() { return testcode; }
    public void setTestcode(String testcode) { this.testcode = testcode; }

    public String getDescriptions() { return descriptions; }
    public void setDescriptions(String descriptions) { this.descriptions = descriptions; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getExecute() { return execute; }
    public void setExecute(String execute) { this.execute = execute; }
}
