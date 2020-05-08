Credit Card Points Calculator
=============================

Requirements
------------

* A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent
  over $50 in each transaction

  (e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).

* Given a record of every transaction during a three-month period, calculate the reward points earned for each customer
  per month and total.

* Make up a data set to best demonstrate your solution

Solution
--------

### API Specification

I'm a firm believer is starting a service with its API, so the first commit after project generation contains an
OpenAPI v3 specification.

The requirements were vague as to what the input for this service would be, so I did my best at interpreting what I
could. After being lead to believe that this team does not store state in a database, I drew the conclusion that the
entire list of transactions is to be submitted in a single HTTP request for immediate processing.

From there, I constructed a basic pair of objects - input and output. I've become used to the convention of all JSON
roots being objects (never arrays), which is why the request body is an object with only a single property. In this
simple case, a root-level array would have been equally functional, but I believe the convention has strong roots. Too
often have I started with the root being an array or primitive, and then finding out that more data needed to be added.
Such an enhancement requires a breaking if the root was not an object to begin with, and so it is always safer to start
with the root as an object.

I also included a date field in each transaction. This seemed like a natural field to include in a list of financial
transactions, even though it was not listed in the requirements. I chose to format that date as seconds-since-epoch,
as I'm a firm believer in using this format for dates whenever and wherever possible. Parsing dates from strings is
never fun.

Finally, I decided it would be convenient to include a bit of aggregate metadata in the response object. This brings us
to the `customer_count` and `points_total` fields. As I was nearing the end of the project, I realized that
`transaction_count` could have been a useful field to add as well, but determined that there was nothing more I needed
to prove by adding this field, and the work to refactor wasn't worth it.

One more point worth talking about is the use of snake_case in the JSON field names. This is another standard I picked
up on during my time at Union Pacific and strive to follow it when possible.

### Lombok & Functional Programming

After writing the spec, my first bit of coding involved utilizing Lombok to write all of my POJOs. I'm a big fan of
functional programming (though not functional programming _languages_) when possible, and therefore always treat
variables as constants unless there is a strong need for mutation. For this reason, all the POJOs' fields are marked
`final`, and I make use of Lombok's easy builder pattern for object creation. This caused me quite a headache when I
realized that the Jackson deserializer wasn't "magically" working with these builders, but a bit of time on Google and
I had a working pattern.

### Input Validation

With the POJOs created, it was time to move on to the controller and input validation. The `PointsControllerTest` is
reasonably self-explanatory, but one thing I believe is worth pointing out is my use of the `@WebMvcTest` annotation.
This annotation was actually new to me - I've always used plain JUnit/Mockito tests combined with
`MockMvcBuilders.standaloneSetup()` when unit-testing controllers. They usually end up something like so:

```java
@BeforeEach
void setUp() {
    this.mockMvc = MockMvcBuilders
                       .standaloneSetup(new VerificationController(
                           this.mockAuthenticationHolder,
                           this.mockRestOps,
                           this.mockEasyFileReader,
                           this.mockProfileReader,
                           this.mockProperties,
                           this.mockObjectMapper,
                           this.mockCompletedTransactions
                       ))
                       .setControllerAdvice(new GlobalExceptionHandler(this.mockObjectMapper))
                       .build();
}
```

Today, I had to find another solution. I was not able to find a (good) way for `MockMvcBuilders.standaloneSetup()` to
utilize `application.yml`, which contained a key property in charge of setting the snake_case preference. I've preferred
this method in the past because it is fast and limited in scope, allowing for fine-grained control over the unit test -
something I want in my unit tests (though not integration tests). After discovering that `@WebMvcTest` already limits
the Spring context's scope significantly, and that it can be _further_ limited with the annotation's `controller`,
parameter I was convinced to at least try it. And the speed of the test spoke for itself - the test ran in ~200ms. At
that speed, I didn't even bother reverting my changes to do a comparison with the "old" way - I was happy. I had a
fast-running test and that test was able to verify everything from the validation annotations to the custom Jackson
properties and a global exception handler (not that I implemented one in this project... though I typically do).

### A Note on `@Nonnull` (and the unused `@Nullable`)

One point that may be questioned is my use of both `@Nonnull` and `@NotNull`. This is simple:
`@javax.annotation.Nonnull` and `@javax.annotation.Nullable` are intended only for use by a developer's IDE or static
analysis tool. They can be used to help determine when an unnecessary if-statement is used (`if (foo != null) {...}`),
or when a guard needs to be added to protect against a potentially null-value. Editors and analysis tools can provide
the same type of help for `@javax.validation.constraint.NotNull`, but there is no equivalent
`@javax.validation.constraint.Nullable`. It's not that I use `@Nullable` often (that would be bad practice), but if a
property _is_ nullable, I want to be a big flashing red light on it.

So, rather than complicating my own rules by eliminating the redundancy between `@Nonnull` and `@NotNull` in the
few places that both are used, I simply treat each one as though it can only do one thing: `@Nonnull` is for my IDE, and
`@NotNull` is for Spring's input validation.

### Points Aggregation

With all the boiler plate out of the way, it was finally time to write the core logic. As is so often the case, this
was the fastest and easiest part of the project. It's also the least interesting to explain, I think. I made use of
both Java 8 lambdas and the Stream API; I'm a big fan of both.

It did take multiple times of reading through the requirements to fully understand them, but writing the code in true
TDD style - tests first - paid off. Of course, it's still up to the developer to write useful tests, and that is where
I slipped up. Had I taken the example provided in the requirements (one $120 transaction) and used it early on in my
test cases, I would have caught my reading mistake far earlier.

It is worth noting the use of `BigDecimal` instead of `float` or `double` for currency. This was one of the first
lessons I learned on the IT Finance team at Union Pacific.

### Return Transactions

The requirements lead me to believe that the scenario is a credit card with rewards points. One of the first test cases
that came to mind was negative numbers, simulating a return transaction. As I began writing the test cases for this,
it quickly became apparent that the requirements were no where near robust enough to properly support return
transactions. However, it just felt _wrong_ to ignore the possibility that a list of transactions might contain
one or two returns in it, and failing out or ignoring those transactions felt like a far worse idea than simply taking a
guess at how to handle them.

The problem I see is this:

* A customer purchases six DVDs for $20 each in a single transaction
* The customer earns 90 points for their purchase
* The customer then, one at a time, returns each of the six DVDs.

Because the input data accepted by my service is not verbose enough to correlate each of these 6 returns with the same
initial purchase, I have no option but treat each $20 return as a unique transaction... and therefore no points are ever
subtracted from the customer's account. This... is a problem. Clearly, were this a real service, we would need to go
back ot the drawing board and figure out how to handle this.

### PITest

We all know and love code coverage, but mutation coverage is the real deal. No more shoddy programmers cheating their
way through code coverage requirements with

```java
@Test
public void cheatTheSystem () {
  try {
    this.testable.doTheFoo(42);
  } catch (Exception e) {
  }
}
```

Mutation coverage hijacks the JVM, swaps certain bytecodes for different bytecodes (it might remove a line or replace
`+` with `-`), and then looks for a failing unit test. If a line of code was modified, but no test failed, then your
tests aren't thorough enough.

Mutation coverage is enabled in this project by activating the `pitest` profile in Maven, running then running `test`
lifecycle. The report is generated in HTML form in the `target/pit-reports/<date>` directory.

### End-to-End Tests

With all of the unit testing, code coverage, and mutation coverage in good shape, it was time to wrap things up with
a few end-to-end tests. For this, JetBrains comes to the rescue with their "[HTTP Request in Editor][1]" specification.
The file `tests.http` in the root of the project can be used by a JetBrains IDE (must be a paid version - the Community
Editions do not contain the HTTP client plugin) to quickly execute a number of HTTP requests and verify the results.
This is similar to running a Postman collection, but can be done from within the IDE and committed back to the project.

This was a great way for me to quickly and easily complete this project, but I am well aware that widespread use of this
in an enterprise environment is not likely to be beneficial. In an enterprise environment, it is more likely that tools
such as Cucumber (combined with a CI server that runs and publishes the results) would be more beneficial.

[1]: https://github.com/JetBrains/http-request-in-editor-spec
