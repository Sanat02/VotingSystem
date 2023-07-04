package labWork;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import server.BasicServer;
import server.ContentType;
import server.ResponseCodes;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.nio.file.Path;
import java.util.Map;

import static server.Utils.parseUrlEncoded;

public class Lesson44Server extends BasicServer {

    CandidatesDataModel cModel = new CandidatesDataModel();
    CandidatesDataModel.Candidate candidate = null;
    UserDataModel userData = new UserDataModel();
    UserDataModel.User user = null;
    private final static Configuration freemarker = initFreeMarker();


    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/", this::getCandidates);
        registerGet("/votes", this::getVotes);
        registerPost("/votes", this::postVote);
        registerGet("/thankyou", this::getVote);
        registerGet("/register", this::handleRegisterGet);
        registerPost("/register", this::handleRegisterPost);
        registerGet("/login", this::handleLoginGet);
        registerPost("/login", this::handleLoginPost);
        registerGet("/logout", this::handleLogOut);

    }

    private void handleLogOut(HttpExchange exchange) {
        user=null;
        redirect303(exchange,"/");
    }

    public void handleLoginGet(HttpExchange exchange) {
        Path path = makeFilePath("login.html");
        sendFile(exchange, path, ContentType.TEXT_HTML);
    }

    public void handleLoginPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = parseUrlEncoded(raw, "&");


        for (UserDataModel.User logUser : userData.getUsers()) {
            if (logUser.getEmail().equals(parsed.get("email")) && logUser.getPassword().equals(parsed.get("user-password"))) {
                user = logUser;
                break;
            }
        }
        cModel.setUser(user);
        redirect303(exchange, "/");

    }


    private void handleRegisterGet(HttpExchange exchange) {
        renderTemplate(exchange, "register.html", user);
    }

    private void handleRegisterPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = parseUrlEncoded(raw, "&");
        System.out.println(parsed);

        for (int i = 0; i < userData.getUsers().size(); i++) {
            if (userData.getUsers().get(i).email.equals(parsed.get("email"))) {
                Path path = makeFilePath("registrationError.html");
                sendFile(exchange, path, ContentType.TEXT_HTML);
                return;
            }
        }

        String email = parsed.get("email");
        String password = parsed.get("password");
        String name = parsed.get("name");
        if (hasNumber(name) == 1) {
            Path path = makeFilePath("invalid.html");
            sendFile(exchange, path, ContentType.TEXT_HTML);
        } else {

            user = new UserDataModel.User(email, password, name, null);
            userData.addUsers(user);

            redirect303(exchange, "/login");
        }

    }


    private void getVote(HttpExchange exchange) {
        renderTemplate(exchange, "thankyou.html", candidate);
    }

    private void postVote(HttpExchange exchange) {
        System.out.println();
        if (user.getCandidate()!=null) {
            Path path = makeFilePath("errorVote.html");
            sendFile(exchange,path,ContentType.TEXT_HTML);



        } else {


            String raw = getBody(exchange);
            Map<String, String> parsed = parseUrlEncoded(raw, "&");
            System.out.println(parsed);
            candidate = cModel.getCandidates().stream().filter(c -> c.getName().equals(parsed.get("candidateName"))).findFirst().orElse(null);
            for(int i=0;i<userData.getUsers().size();i++)
            {
                if(userData.getUsers().get(i).getEmail().equals(user.email))
                {
                    userData.getUsers().get(i).setCandidate(candidate);
                    userData.saveCandidatesToFile();
                    break;
                }
            }

            cModel.vote(parsed.get(("candidateName")));
            cModel.setPercentageOfCandidate();
            redirect303(exchange, "/thankyou");


        }
    }

    private void getVotes(HttpExchange exchange) {
        cModel.sortCandidates();
        renderTemplate(exchange, "votes.html", cModel);
    }

    private void getCandidates(HttpExchange exchange) {
        if (user == null) {
            Path path = makeFilePath("index.html");
            sendFile(exchange, path, ContentType.TEXT_HTML);

        } else {
            renderTemplate(exchange, "candidates.html", cModel);
        }
    }


    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                temp.process(dataModel, writer);
                writer.flush();
                var data = stream.toByteArray();

                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    public static int hasNumber(String input) {
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                return 1;
            }
        }
        return 0;
    }


}
