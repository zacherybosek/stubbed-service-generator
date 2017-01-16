#Stubbed Service Generator


###Description
The Stubbed Service Generator is a Java based integration testing library used to spin up fully configurable REST endpoints.
It is used to simulate calls to a real service, rather than a mock, fully configurable for a number of testing scenarios.
Whether testing something as trivial as an application's response to a 404, or something more complex like multiple calls to different endpoints with varying latency,
the Stubbed Service Generator can help a developer test their endpoint calls by invoking real, highly configurable, services.

###Syntax
The Stubbed Service Generator is written to have a Mockito like syntax. This is so any Java developer can pick up the tool and use it in their testing suites with a light learning curve.

##Sample Usage

```Java
//pass in the port number when the StubbedService is instantiated
StubbedService stubbedService = new StubbedService(9999);

//pass in a url to respond to, and specify a response
stubbedService
        .onRequestTo("/sample/url")
        .giveResponse("A Sample Response!");

//start the service
stubbedService.start();

// call to prove the service worked
RestTemplate sampleCall = new RestTemplate();
String results = sampleCall.getForObject("http://localhost:9999/sample/url", String.class);
assertEquals("A Sample Response!", results);

//shut down the service once finished
stubbedService.close();
```

##Advanced Usage
```Java
StubbedService stubbedService = new StubbedService(9999);

stubbedService
    // respond if the url (no parameters, no host information) matches the input string
    .onRequestTo("[/].*")
    // enables the incoming url to be a regular expression, to match multiple urls
    .withRegEx()
    // only respond to requests with a content type of text/plain
    .withContentType(MediaType.TEXT_PLAIN)
    // respond if ANY header is passed in. NONE is also acceptable.
    .withHeaders(StubbedService.any())
    // only respond if the service matches the headers in the incoming MultiValueMap
    .withHeaders(new LinkedMultiValueMap<String, String>())
    //respond if ANY parameter is passed in. None is also acceptable.
    .withParams(StubbedService.any())
    //only respond if the service matches the parameters in the incoming MultiValueMap
    .withParams(new LinkedMultiValueMap<String, String>())
    //only respond to a post
    .withMethod(HttpMethod.POST)
    //logs the last 100 calls, so they appear on the /info page
    .withRequestLogSize(100)
    //adds a delay to every response, to simulate a slow service
    .withSleep(100)
    //adds a verification step to confirm this service was called the correct amount of times.
    //never, times, range, atLEat, atMost, and exactly are allowed
    .verify(StubbedService.times(5))

    /**
     * The service can return different responses for each call it
     * will return responses in the order they are configured, the
     * last response configured will be returned for additional calls
     */

    //1st call - return a simple string
    .giveResponse("sample content")

    //2nd call - return xml content, with a XML media type
    .giveResponse("<sample>Content</sample>", MediaType.APPLICATION_XML)

    //3rd call - return a 500 status with an error
    .giveResponse("BOOOOM!!!", HttpStatus.INTERNAL_SERVER_ERROR)

    //4th call - (and all additional calls) return a json 200 message
    .giveResponse("{ json:\"fun\" }", MediaType.APPLICATION_JSON, HttpStatus.CREATED);


//start the service
stubbedService.start();

//set up the request headers
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.TEXT_PLAIN);
HttpEntity<String> entity = new HttpEntity<>("headers", headers);

//setup a call to the configured service
RestTemplate sampleCall = new TestRestTemplate();
//when
ResponseEntity<String> response = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
//then
assertEquals("sample content", response.getBody());
//when
ResponseEntity<String> response2 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
//then
assertEquals("<sample>Content</sample>", response2.getBody());
//when
ResponseEntity<String> response3 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
//then
assertEquals("BOOOOM!!!", response3.getBody());
assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response3.getStatusCode());
//when
ResponseEntity<String> response4 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
//then
assertEquals("{ json:\"fun\" }", response4.getBody());
assertEquals(HttpStatus.CREATED, response4.getStatusCode());
//when
ResponseEntity<String> response5 = sampleCall.exchange("http://localhost:9999/sample/url", HttpMethod.POST, entity, String.class);
//then
assertEquals("{ json:\"fun\" }", response5.getBody());
assertEquals(HttpStatus.CREATED, response5.getStatusCode());

//shut down the service once finished
stubbedService.close();
```