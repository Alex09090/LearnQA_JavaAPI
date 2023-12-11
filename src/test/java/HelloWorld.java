import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import  io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;


public class HelloWorld {
    @Test
    public void testRestAssured() {

        Map<String, String> params = new HashMap<>();
        params.put("name", "Dan");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer2");
        if (name == null) {
            System.out.println("The key answer2 is absent");
        } else {
            System.out.println(name);
        }
    }

    @Test
    public void testCheck() {

        Map<String, Object> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
        response.print();
    }

    @Test
    public void testWithHeaders() {

        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        response.prettyPrint();

        Headers responceHeaders = response.getHeaders();
        System.out.println(responceHeaders);
    }

    @Test
    public void testCookie() {


        Map<String, Object> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String, String> responseCookies = response.getCookies();
        System.out.println(responseCookies);


        String responseCookie = response.getCookie("auth_cookie");

       Map<String, String> cookies = new HashMap<>();
       if(responseCookie != null) {
           cookies.put("auth_cookie", responseCookie);
       }


       Response responseForCheck = RestAssured
               .given()
               .body(data)
               .cookies(cookies)
               .when()
               .post("https://playground.learnqa.ru/api/check_auth_cookie")
               .andReturn();

       responseForCheck.print();
    }

//    public void testResponseCookie() {
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("login", "secret_login");
//        data.put("password", "secret_pass");
//
//        Response response = RestAssured
//                .given()
//                .body(data)
//                .when()
//                .post("https://playground.learnqa.ru/api/get_auth_cookie")
//                .andReturn();
//
//        String responseCookie = responseForGet.getCookie("auth_cookie");
//
//       Map<String, String> cookies = new HashMap<>();
//       cookies.put("auth_cookie", responseCookie);
//
//       Response
//    }
}
