package labWork;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserDataModel {
    private List<User> users=new ArrayList<>();

    public UserDataModel() {
        this.users = readFile();
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUsers(User user) {
        users.add(user);
        saveCandidatesToFile();
    }
    public void saveCandidatesToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(users);
        try (FileWriter writer = new FileWriter("users.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private List<User> readFile() {
        List<User> userList = new ArrayList<>();
        try {
            Type listType = new TypeToken<ArrayList<User>>() {
            }.getType();
            Path path = Paths.get("users.json");
            String json = Files.readString(path);
            userList = new Gson().fromJson(json, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    public static class User{
        String email;
        String password;
        CandidatesDataModel.Candidate candidate;
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User(String email, String password, String name, CandidatesDataModel.Candidate candidate) {
            this.email = email;
            this.password = password;
            this.name=name;
            this.candidate=candidate;
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
