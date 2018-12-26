package redmine.rest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Users implements Serializable {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User implements Serializable {
        private int id;
        private String login;
        private String firstname;
        private String lastname;

        public User() {
        }

        public User(int id, String login) {
            this.id = id;
            this.login = login;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public void setFirstname(String firstName) {
            this.firstname = firstName;
        }

        public void setLastname(String lastName) {
            this.lastname = lastName;
        }

        public int getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }
    }
}