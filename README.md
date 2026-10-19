# jev-sdk

A small Java client for the Jev API (`https://api.typesafe.ai/v1/systemone`). You give it a
piece of text (the *state*) and one or more questions about that text, and it returns a
typed answer for each question along with token usage.

Three kinds of question are supported:

| Type   | Ask it to...                                      | You get back                                                                   |
|--------|---------------------------------------------------|--------------------------------------------------------------------------------|
| Choice | pick one of several named options                 | the chosen option, confidence, and a probability per option                    |
| Score  | rate the text against an ordered list of criteria | a score, confidence, a legend of the criteria, and a probability per criterion |
| Noul   | give a single judgement between 0 and 1           | one number from 0 to 1                                                         |

## Requirements

- Java 25 or newer.
- A Jev API key.

## Installation

The artifact coordinates are `dev.isira:jev-sdk:1.0`.

From a repository that hosts it:

```kotlin
dependencies {
    implementation("dev.isira:jev-sdk:1.0")
}
```

From a local checkout, without publishing anything, use a Gradle composite build:

```kotlin
// settings.gradle.kts
includeBuild("../jev-sdk")

// build.gradle.kts
dependencies {
    implementation("dev.isira:jev-sdk:1.0")
}
```

All classes live under the package
`com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev`.

## Authentication

Pass your API key to the client. The SDK never reads environment variables or files
itself, so load the key however your application prefers and keep it out of source control.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevClient;

public class ClientSetup {
    // One client is enough for the whole application; it is safe to share between threads.
    static final JevClient CLIENT = new JevClient(System.getenv("JEV_KEY"));
}
```

## Quick start: one question

The single-question request gives you a typed answer directly. The type parameter is the
answer class that matches your question type.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevClient;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevSingleQuestionRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevChoiceQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevSingleAnswerResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;

import java.io.IOException;
import java.util.Map;

public class QuickStart {
    static void main(String[] args) throws IOException, InterruptedException {
        var client = new JevClient(System.getenv("JEV_KEY"));

        var request = JevSingleQuestionRequest.<JevChoiceAnswer>singleQuestionBuilder()
                .state("The quick brown fox jumps over the lazy dog. The fox is fast and the dog is sleepy.")
                .question(new JevChoiceQuestion(
                        "Which animal is described as fast?",
                        Map.of("fox", "the fox", "dog", "the dog")))
                .build();

        JevSingleAnswerResponse<JevChoiceAnswer> response = client.sendSingleQuestionRequest(request);

        JevChoiceAnswer answer = response.answer();
        System.out.println(answer.choice);        // fox
        System.out.println(answer.confidence);    // e.g. 1.0
        System.out.println(answer.probabilities); // {fox=1.0, dog=0.0}

        System.out.println(response.model());               // the model that answered, e.g. jev-1.13.0
        System.out.println(response.usage().inputTokens()); // tokens consumed
        System.out.println(response.usage().outputTokens());
    }
}
```

## Several questions in one request

Give each question a key of your choosing. After the call, ask each question object for
its own answer; it knows which key it was sent under, so you get the right answer type
without casting.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevClient;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevChoiceQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevNoulQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevScoreQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevNoulAnswer;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevScoreAnswer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MultipleQuestions {
    static void main(String[] args) throws IOException, InterruptedException {
        var client = new JevClient(System.getenv("JEV_KEY"));

        var animal = new JevChoiceQuestion(
                "Which animal is described as fast?",
                Map.of("fox", "the fox", "dog", "the dog"));

        var energy = new JevScoreQuestion(
                "How energetic is the dog?",
                List.of("1: asleep", "2: lazy", "3: neutral", "4: active", "5: hyper"));

        var foxFast = new JevNoulQuestion("Is the fox fast?");

        var request = JevRequest.builder()
                .state("The quick brown fox jumps over the lazy dog. The fox is fast and the dog is sleepy.")
                .questions(Map.of("animal", animal, "energy", energy, "fox_fast", foxFast))
                .build();

        JevResponse response = client.sendRequest(request);

        JevChoiceAnswer a = animal.getAnswerFromResponse(response);
        JevScoreAnswer s = energy.getAnswerFromResponse(response);
        JevNoulAnswer n = foxFast.getAnswerFromResponse(response);

    }
}
```

Use the same question instances you put into the request. A freshly constructed question
has no key and cannot look up its answer.

If you prefer, `response.answers()` is the raw map from your keys to `JevAnswer` values.

## Question and answer types

The examples below assume the question was sent in a request as shown above.

### Choice

`choices` maps each option's identifier to a description of that option. The answer's
`choice` is one of those identifiers.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevChoiceQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;

import java.util.Map;

public class ChoiceExample {
    JevChoiceQuestion question = new JevChoiceQuestion(
            "Which animal is described as fast?",
            Map.of("fox", "the fox", "dog", "the dog"));

    String describe(JevResponse response) {
        JevChoiceAnswer answer = question.getAnswerFromResponse(response);
        return answer.choice                    // one of the option identifiers
                + " (" + answer.confidence + ") "
                + answer.probabilities;         // one entry per option
    }
}
```

### Score

`criteria` is the ordered scale to rate against, lowest first. The answer's `legend` maps
each position on the scale (a string index starting at `"0"`) to the criterion text, and
`probabilities` has one entry per position using the same indexes.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevScoreQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevScoreAnswer;

import java.util.List;

public class ScoreExample {
    JevScoreQuestion question = new JevScoreQuestion(
            "How energetic is the dog?",
            List.of("1: asleep", "2: lazy", "3: neutral", "4: active", "5: hyper"));

    String describe(JevResponse response) {
        JevScoreAnswer answer = question.getAnswerFromResponse(response);
        return answer.score
                + " (" + answer.confidence + ") "
                + answer.legend                 // {0=1: asleep, 1=2: lazy, ...}
                + " " + answer.probabilities;   // {0=0.06, 1=0.94, ...}
    }
}
```

### Noul

`criteria` is optional and lets you name the aspects the judgement should consider. The
answer is a single `noul` value from 0 to 1.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevNoulQuestion;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevNoulAnswer;

import java.util.Map;

public class NoulExample {
    JevNoulQuestion plain = new JevNoulQuestion("Is the fox fast?");

    JevNoulQuestion detailed = new JevNoulQuestion(
            "Is the fox fast?",
            Map.of("speed", "how quickly the fox moves"));

    double read(JevResponse response) {
        JevNoulAnswer answer = plain.getAnswerFromResponse(response);
        return answer.noul; // from 0 to 1
    }
}
```

Every answer also exposes `type`, one of `JevType.CHOICE`, `JevType.SCORE` or
`JevType.NOUL`.

## Choosing a model

Requests default to `jev-latest`. To pin a version, set it on the builder. The response's
`model()` tells you which concrete version answered.

```java
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.question.JevQuestion;

import java.util.Map;

public class PinnedModel {
    JevRequest build(String text, Map<String, JevQuestion<?>> questions) {
        return JevRequest.builder()
                .model("jev-1.13.0") // omit for jev-latest
                .state(text)
                .questions(questions)
                .build();
    }
}
```

## Errors

- A non-2xx reply throws `IOException` with the message `HTTP <status>`. A wrong or
  missing API key gives `HTTP 401`; a request the API cannot accept gives `HTTP 422`.
- Network and connection failures also surface as `IOException`.
- Both send methods declare `InterruptedException`, as the call blocks the current thread.
- If the API omits an answer for one of your keys, that question's answer is `null`.

## Running the tests

```sh
./gradlew test
```

Unit tests need nothing. The live API tests need a key: copy `.env.example` to `.env`,
set `JEV_KEY`, and run again. Without the key they are reported as skipped. The live
tests make a small number of real requests per run.
