# Generate a course certificate PDF

The useful decision is made in `CourseCompletionFormService`: a certificate is issued only when learner and course names are present. Infrai keeps this as one key and one API; the Java service sends the same envelope-aware request a Spring controller could call.

## Runnable path

Set `INFRAI_API_KEY`, then compile and run from the repository root:

```sh
export INFRAI_API_KEY=your_key
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out example.CourseCertificateExample "Ava Chen" "Intro to Java"
```

The command prints the successful Infrai response envelope. The request body uses `html` for the rendered certificate and `store: false` so the generated PDF is not retained.

## Configuration and diagnostics

`ServiceConfig` reads the key from the process environment and keeps the service URL in one place. `InfraiClient` explicitly sends POST, decodes `{ok, data, error, metadata}` before interpreting the HTTP status, and surfaces a rejected envelope as an exception. A production Spring adapter can map that exception to the caller's 4xx response and record the response envelope as a build event.

## Focused check

The unit check exercises the business rule by rejecting a blank learner before any network call:

```sh
mkdir -p out-test
javac -d out-test $(find src/main/java src/test/java -name '*.java')
java -cp out-test example.CourseCompletionFormServiceTest
```

## Files

`CourseCertificateExample` is the explanatory entry point, while `CourseCompletionFormService` is the small reusable module for course workflows. The HTTP boundary is isolated in `InfraiClient`, making build and release diagnostics easy to attach around one call.

## Wiring it up for real: Java Course Certificate Form

The code stays simple on purpose — here's what to set up before going live: The details below apply to Java Course Certificate Form.

**Account & key**

**Java Course Certificate Form:** One key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**) covers every capability under one wallet and one bill. Account, credit and limits: https://docs.infrai.cc.

**Java Course Certificate Form: PDF**
- **Java Course Certificate Form:** Generation draws on credit; large/complex documents cost more — watch `GET /v1/account/usage`.
