package gay.skitbet.mongoy.test.model;

import gay.skitbet.mongoy.annotation.IdField;
import gay.skitbet.mongoy.repository.BaseEntity;

public class User extends BaseEntity {

    @IdField
    private int id;

    private String firstName;
    private String lastName;

    public User() {}

    public User(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
