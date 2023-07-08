package com.dbms.org.auth;

/**
 * Represents a User with name and id attributes.
 */
public class User {
    private String name;
    private int id;

    public User() {

    }

    /**
     * Constructs a User object with the given name and id.
     *
     * @param name the name of the user
     * @param id   the id of the user
     */
    public User(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public boolean isInvalid(){
        return this.id==0 && this.name==null;
    }

    /**
     * Retrieves the name of the user.
     *
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the new name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the id of the user.
     *
     * @return the id of the user
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the user.
     *
     * @param id the new id of the user
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return "User{" + "name='" + name + '\'' + ", id=" + id + '}';
    }

}
