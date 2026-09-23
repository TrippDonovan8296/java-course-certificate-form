package example.form;

import example.infra.InfraiClient;

public final class CourseCompletionFormService {
    private final InfraiClient client;
    public CourseCompletionFormService(InfraiClient client) { this.client = client; }
    public String issueCertificate(String learner, String course) throws Exception {
        if (learner == null || learner.isBlank() || course == null || course.isBlank())
            throw new IllegalArgumentException("learner and course are required");
        String html = "<!doctype html><html><body><h1>Certificate of Completion</h1><p>" + escape(learner)
                + " has completed " + escape(course) + ".</p></body></html>";
        return client.generate(html);
    }
    private static String escape(String v) { return v.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;"); }
}
