package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a Get-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth-sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a Get-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth-sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a Get-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Perform user login with provided credentials")
    public Response loginUser(Map<String, String> authData) {
        return RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
    }

    @Step("Get user data for user ID {userId}")
    public Response getUserData(int userId) {
        return RestAssured
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Get user data for different user ID {differentUserId} with CSRF token {csrfToken} and auth_sid {authSid}")
    public Response getUserDataAsDifferentUser(int differentUserId, String csrfToken, String authSid) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-csrf-token", csrfToken);
        headers.put("Cookie", "auth_sid=" + authSid);

        return RestAssured
                .given()
                .headers(headers)
                .get("https://playground.learnqa.ru/api/user/" + differentUserId)
                .andReturn();
    }
    @Step("Create and login user with provided data")
    public String createAndLoginUser(Map<String, String> userData) {
        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();

        return responseCreateAuth.getString("id");
    }

    @Step("Logout user")
    public void logoutUser() {
        RestAssured
                .given()
                .post("https://playground.learnqa.ru/api/user/logout");
    }

    @Step("Edit user data with provided data for user ID {userId}")
    public Response editUserData(String userId, Map<String, String> editData) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-csrf-token", getCsrfToken());
        headers.put("Cookie", "auth_sid=" + getAuthSid());

        return RestAssured
                .given()
                .headers(headers)
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Edit user data with provided data for user ID {userId} without authentication")
    public Response editUserDataUnauthenticated(String userId, Map<String, String> editData) {
        return RestAssured
                .given()
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }

    @Step("Delete user with ID {userId}")
    public Response deleteUser(String userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-csrf-token", getCsrfToken());
        headers.put("Cookie", "auth_sid=" + getAuthSid());

        return RestAssured
                .given()
                .headers(headers)
                .delete("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();
    }
}
