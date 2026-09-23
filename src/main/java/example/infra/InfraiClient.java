package example.infra;

import example.config.ServiceConfig;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

public final class InfraiClient {
    private final ServiceConfig config;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    public InfraiClient(ServiceConfig config) { this.config = config; }

    public String generate(String html) throws Exception {
        String body = "{\"html\":" + quote(html) + ",\"store\":false}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.baseUrl() + "/v1/pdf/generate"))
                .timeout(Duration.ofSeconds(60)).header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        String envelope = response.body();
        if (envelope.contains("\"ok\":false")) throw new IllegalStateException("Infrai rejected PDF generation: " + envelope);
        if (response.statusCode() >= 500) throw new IllegalStateException("Infrai transport failure: " + response.statusCode());
        return envelope;
    }

    public String fillForm(String pdf, String fieldsJson, boolean flatten) throws Exception {
        String body = "{\"pdf\":" + quote(pdf) + ",\"fields\":" + fieldsJson + ",\"flatten\":" + flatten + "}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.baseUrl() + "/v1/pdf/form/fill"))
                .timeout(Duration.ofSeconds(60)).header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        String envelope = response.body();
        if (envelope.contains("\"ok\":false")) throw new IllegalStateException("Infrai rejected the form: " + envelope);
        if (response.statusCode() >= 500) throw new IllegalStateException("Infrai transport failure: " + response.statusCode());
        return envelope;
    }

    private static String quote(String value) { return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
}
