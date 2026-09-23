package example;

import example.config.ServiceConfig;
import example.form.CourseCompletionFormService;
import example.infra.InfraiClient;

public final class CourseCertificateExample {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) throw new IllegalArgumentException("usage: <learner> <course>");
        var service = new CourseCompletionFormService(new InfraiClient(ServiceConfig.fromEnvironment()));
        System.out.println(service.issueCertificate(args[0], args[1]));
    }
}
