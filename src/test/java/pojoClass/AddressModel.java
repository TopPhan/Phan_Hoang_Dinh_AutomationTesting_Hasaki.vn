package pojoClass;

public class AddressModel {

    private String phoneNumber;
    private String fullName;
    private String city;
    private String district;
    private String ward;
    private String address;
    private String executed;

    public AddressModel() {
    }

    public AddressModel(String phoneNumber, String fullName, String city, String district, String ward, String address) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.city = city;
        this.district = district;
        this.ward = ward;
        this.address = address;
        this.executed = executed;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setExecuted(String executed) {
        this.executed = executed;
    }

    public String getExecuted() {
        return executed;
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", ward='" + ward + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
