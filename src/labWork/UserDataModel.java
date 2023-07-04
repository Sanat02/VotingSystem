package labWork;

import java.util.ArrayList;
import java.util.List;

public class UserDataModel {
    private List<User> users=new ArrayList<>();

    public UserDataModel(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static class User{
        String email;
        String password;
        CandidatesDataModel.Candidate candidate;

        public User(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public CandidatesDataModel.Candidate getCandidate() {
            return candidate;
        }

        public void setCandidate(CandidatesDataModel.Candidate candidate) {
            this.candidate = candidate;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
