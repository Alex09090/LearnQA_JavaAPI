package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestcase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.http.Headers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import lib.ApiCoreRequests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Epic("Authorisations cases")
@Feature("Auth")
public class UserAuthTest extends BaseTestcase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth,  "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }
    @Test
    @Description("This test succesfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.header, this.cookie);
//                .given()
//                .header("x-csrf-token", this.header)
//                .cookie("auth_sid", this.cookie)
//                .get("https://playground.learnqa.ru/api/user/auth")
//                .andReturn();

       Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Description("This test checks authorisations status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookies", "headers"})
    public void testNegativeAuthUser(String condition) {

//        RequestSpecification spec = RestAssured.given();
//            spec.baseUri("https://playground.learnqa.ru/api/user/auth");


                    if (condition.equals("cookie")) {
                        Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                                "https://playground.learnqa.ru/api/user/auth",
                                this. cookie
                        );
                        Assertions.assertJsonByName(responseForCheck, "user_id", 0);
                    } else if (condition.equals("headers")) {
                        Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                                "https://playground.learnqa.ru/api/user/auth",
                                this.header
                        );
                        Assertions.assertJsonByName(responseForCheck, "user_id", 0);
                    } else {
                        throw new IllegalArgumentException("condition value is not known:" + condition);
                    }

//                    if (condition.equals("cookie")){
//                        spec.cookie("auth_sid", this.cookie);
//                    }else if (condition.equals("headers")){
//                        spec.header("x-csrf-token", this.header);
//        }           else {
//                        throw new IllegalArgumentException("Condition value is known: " + condition);
//                    }
//                    Response responseForCheck = spec.get().andReturn();
//                    Assertions.assertJsonByName(responseForCheck, "user_id", 0);
                        }
}
