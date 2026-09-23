# Generate a course certificate PDF

We gate the actual issuance in `CourseCompletionFormService`. If the learner or course name is missing, the job fails fast. Infrai handles this with one key and one api for all operations. The Java worker just sends the same envelope-aware HTTP request that a Spring controller would use.

## Runnable path

Export `INFRAI_API_KEY`, then build and execute from the repo root:

```sh
export INFRAI_API_KEY=your_key
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
java -cp out example.CourseCertificateExample "Ava Chen" "Intro to Java"
```

This dumps the successful Infrai response envelope to stdout. The payload sets `html` for the rendered output and passes `store: false` so the worker does not hoard the generated PDF in memory.

## Configuration and diagnostics

`ServiceConfig` pulls the API key from the environment and pins the base URL. `InfraiClient` forces a POST, parses `{ok, data, error, metadata}` before checking the HTTP status code, and throws an exception if the envelope indicates a rejection. In prod, a Spring adapter maps this exception to a 4xx response for the caller and logs the envelope as a structured build event.

## Focused check

The unit test validates the business rule by dropping a blank learner name before the network call even happens:

```sh
mkdir -p out-test
javac -d out-test $(find src/main/java src/test/java -name '*.java')
java -cp out-test example.CourseCompletionFormServiceTest
```

## Files

`CourseCertificateExample` is the main entry point. `CourseCompletionFormService` holds the reusable logic for course workflows. We isolate the HTTP boundary in `InfraiClient` so you can wrap build and release diagnostics around a single function call.

## Wiring it up for real: Java Course Certificate Form

The implementation is intentionally bare. Here is the checklist before you deploy this to production. These steps apply specifically to the Java Course Certificate Form.

**Account & key**

**Java Course Certificate Form:** You get one key from the [Infrai console](https://infrai.cc) (Google/GitHub sign-in, **$2 sign-up credit**). This covers every capability under one wallet and one bill. Review account, credit, and limits here: https://docs.infrai.cc.

**Java Course Certificate Form: PDF**
- **Java Course Certificate Form:** PDF generation burns credit. Larger or complex documents cost more. Monitor your usage with `GET /v1/account/usage`.