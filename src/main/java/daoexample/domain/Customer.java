package daoexample.domain;

public class Customer {

    private int customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;

    public Customer() {
    }

    public Customer(int customerId, String firstName, String lastName, String phoneNumber, String email, String address) {
        setCustomerId(customerId);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
    }

    public Customer(String firstName, String lastName, String phoneNumber, String email, String address) {
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setAddress(address);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId < 0) {
            throw new IllegalArgumentException("Customer ID cannot be negative.");
        }
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty.");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty.");
        }
        this.lastName = lastName.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }
        this.phoneNumber = phoneNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Email must be valid.");
        }
        this.email = email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = address.trim();
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

