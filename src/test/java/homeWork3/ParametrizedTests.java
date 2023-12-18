package homeWork3;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParametrizedTests {
    @Test
    public void testFor200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();

        assertEquals(200, response.statusCode(),    "Unexpected status code");
    }

    @Test
    public void testFor404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map2")
                .andReturn();

        assertEquals(404, response.statusCode(),    "Unexpected status code");
    }
    @ParameterizedTest
    @ValueSource(strings = {"", "Goga", "Dan"})
    public void testHelloMethodWithoutName(String name) {
        Map<String, String> queryParams = new HashMap<>();

        if (name.length() > 0) {
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0 ) ? name : "someone";

        assertEquals("Hello, " + expectedName, answer, "The answer is not expected");
    }

//    @Test
//    public void testHelloMethodWithName() {
//        String name = "Goga";
//        JsonPath response = RestAssured
//                .given()
//                .queryParam("name", name)
//                .get("https://playground.learnqa.ru/api/hello")
//                .jsonPath();
//        String answer = response.getString("answer");
//
//        assertEquals("Hello, " + name, answer, "The answer is not expected");
//    }
}
