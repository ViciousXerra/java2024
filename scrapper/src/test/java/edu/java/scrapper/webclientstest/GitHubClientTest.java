package edu.java.scrapper.webclientstest;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest(httpPort = 8080)
class GitHubClientTest {

    private final static String TEST_URL = "http://localhost:8080/";
    private final static String REPOSITORY_NAME = "test_repository";
    private final static String USER_NAME = "test_user";

}
