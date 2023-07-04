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
    private final static Configuration freemarker = initFreeMarker();


    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/", this::getCandidates);
        registerGet("/votes", this::getVotes);
        registerPost("/votes", this::postVote);
        registerGet("/thankyou", this::getVote);

    }

    private void getVote(HttpExchange exchange) {
        renderTemplate(exchange, "thankyou.html", candidate);
    }

    private void postVote(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> parsed = parseUrlEncoded(raw, "&");
        System.out.println(parsed);
        candidate = cModel.getCandidates().stream().filter(c -> c.getName().equals(parsed.get("candidateName"))).findFirst().orElse(null);
        cModel.vote(parsed.get(("candidateName")));
        cModel.setPercentageOfCandidate();
        redirect303(exchange, "/thankyou");
    }

    private void getVotes(HttpExchange exchange) {
        cModel.sortCandidates();
        renderTemplate(exchange, "votes.html", cModel);
    }

    private void getCandidates(HttpExchange exchange) {
        renderTemplate(exchange, "candidates.html", cModel);
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


}
