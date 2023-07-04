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

import java.util.Comparator;
import java.util.List;


public class CandidatesDataModel {
    private List<Candidate> sortedList = new ArrayList<>();
    private List<Candidate> candidates = new ArrayList<>();


    public List<Candidate> getSortedList() {
        return sortedList;
    }



    public CandidatesDataModel() {
        this.candidates = readFile();
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void vote(String name) {
        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).getName().equals(name)) {
                candidates.get(i).setVote();
                break;
            }
        }
        saveCandidatesToFile();

    }


    public void setPercentageOfCandidate() {
        var total = candidates.stream().mapToDouble(e -> e.getVote()).sum();
        candidates.stream()
                .forEach(candidate -> {

                    double value = (candidate.getVote() / total) * 100;
                    candidate.setPercentage((int) value);

                });

        saveCandidatesToFile();
    }

    public void sortCandidates() {
        sortedList = new ArrayList<>(candidates);
        sortedList.sort(Comparator.comparingInt(Candidate::getPercentage).reversed());

    }

    private List<Candidate> readFile() {
        List<Candidate> emplist = new ArrayList<>();
        try {
            Type listType = new TypeToken<ArrayList<Candidate>>() {
            }.getType();
            Path path = Paths.get("candidates.json");
            String json = Files.readString(path);
            emplist = new Gson().fromJson(json, listType);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return emplist;
    }

    public void saveCandidatesToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(candidates);
        try (FileWriter writer = new FileWriter("candidates.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Candidate {
        private String name;
        private String photo;
        private double vote = 0;
        private int percentage;

        public int getPercentage() {
            return percentage;
        }

        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }



        public double getVote() {
            return vote;
        }

        public void setVote() {
            vote++;
        }

        public Candidate(String name, String photo) {
            this.name = name;
            this.photo = photo;
        }

        public String getName() {
            return name;
        }



        public String getPhoto() {
            return photo;
        }


    }
}
