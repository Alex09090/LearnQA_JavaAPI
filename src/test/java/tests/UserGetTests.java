package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTests extends BaseTestcase {

    private ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vitkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields =  {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseGetAuth, expectedFields);

//        Assertions.assertJsonHasField(responseUserData, "username");
//        Assertions.assertJsonHasField(responseUserData, "firstName");
//        Assertions.assertJsonHasField(responseUserData, "lastName");
//        Assertions.assertJsonHasField(responseUserData, "email");


    }

    @Test
    public void testGetUserDetailsAuthAsDifferentUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "anotheruser@example.com");
        authData.put("password", "5678");

        Response responseGetAuth = ApiCoreRequests.loginUser(authData);

        Assertions.assertNotNull(responseGetAuth.getHeader("x-csrf-token"), "CSRF-token is null");
        Assertions.assertNotNull(responseGetAuth.getCookie("auth_sid"), "auth_sid is null");

        Response responseUserData = apiCoreRequests.getUserData(3); // ID другого пользователя
        String[] expectedFields = {"username"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

}
