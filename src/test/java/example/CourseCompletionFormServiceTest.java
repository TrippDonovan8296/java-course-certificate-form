package example;

import example.form.CourseCompletionFormService;
import example.infra.InfraiClient;
import example.config.ServiceConfig;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public final class CourseCompletionFormServiceTest {
    public static void main(String[] args) throws Exception {
        try { new CourseCompletionFormService(null).issueCertificate("", "Java"); throw new AssertionError("blank learner must be rejected"); }
        catch (IllegalArgumentException expected) { /* expected */ }
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        var request = new AtomicReference<String>();
        server.createContext("/v1/pdf/generate", exchange -> {
            request.set(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " "
                    + new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = "{\"ok\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            try (var output = exchange.getResponseBody()) { output.write(response); }
        });
        server.start();
        try {
            var service = new CourseCompletionFormService(new InfraiClient(
                    new ServiceConfig("test", "http://127.0.0.1:" + server.getAddress().getPort())));
            service.issueCertificate("Ava <Chen>", "Intro & Java");
            String sent = request.get();
            if (sent == null || !sent.startsWith("POST /v1/pdf/generate ")
                    || !sent.contains("Ava &lt;Chen&gt;") || !sent.contains("Intro &amp; Java")
                    || !sent.contains("\"store\":false") || sent.contains("\"flatten\""))
                throw new AssertionError("unexpected PDF request: " + sent);
            System.out.println("business decision and PDF request verified");
        } finally { server.stop(0); }
    }
}
