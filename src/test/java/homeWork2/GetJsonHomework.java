package homeWork2;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetJsonHomework {
    @Test
    public void testJson() {

        Response response = given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .andReturn();

     //   response.prettyPrint();

         String second = response.jsonPath().getString("messages[1]");

        System.out.println(second);
    }

    @Test
    public void testRedirect() {
        RestAssured.baseURI = "https://playground.learnqa.ru";

        Response response = given()
                .when()
                .get("/api/long_redirect")
                .then()
                .extract().response();

        String redirectedUrl = response.getHeader("X-Host");
        // Выводим в консоль URL редиректа
        System.out.println("Redirected to: " + redirectedUrl);
    }

    @Test
    public void testMultipleRedirects() {
        // Устанавливаем базовый URL
        RestAssured.baseURI = "https://playground.learnqa.ru";

        // Первоначальный адрес для отправки запроса
        String currentUrl = "/api/long_redirect";

        // Счетчик редиректов
        int redirectCount = 0;

        // цикл до получения ответа с кодом 200
        while (true) {
            // Отправляем GET-запрос
            Response response = given()
                    .when()
                    .get(currentUrl)
                    .then()
                    .extract().response();

            // Увеличиваем счетчик редиректов
            redirectCount++;

            // Проверяем, если получен ответ с кодом 200, то завершаем цикл
            if (response.getStatusCode() == 200) {
                System.out.println("Final URL: " + currentUrl);
                System.out.println("Number of redirects: " + redirectCount);
                break;
            }

            // Получаем новый URL для редиректа из заголовка
            currentUrl = response.getHeader("X-Host");

            // Выводим в консоль текущий URL и переходим к следующей итерации цикла
            System.out.println("Redirected to: " + currentUrl);
        }
    }

    @Test
    public void testLongTimeJob() throws InterruptedException {
        // Устанавливаем базовый URL
        RestAssured.baseURI = "https://playground.learnqa.ru/ajax/api/longtime_job";

        // Шаг 1: Создаем задачу и получаем токен
        Response createJobResponse = given()
                .contentType(JSON)
                .when()
                .post()
                .then()
                .extract().response();

        int seconds = createJobResponse.jsonPath().getInt("seconds");
        String token = createJobResponse.jsonPath().getString("token");

        // Шаг 2: Проверяем статус задачи до завершения
        Response statusBeforeCompletionResponse = given()
                .param("token", token)
                .when()
                .get()
                .then()
                .extract().response();

        String statusBeforeCompletion = statusBeforeCompletionResponse.jsonPath().getString("status");
        assertEquals("Job is NOT ready", statusBeforeCompletion);

        // Шаг 3: Ждем нужное количество секунд
        Thread.sleep(seconds * 1000);

        // Шаг 4: Проверяем статус и результат задачи после завершения
        Response statusAfterCompletionResponse = given()
                .param("token", token)
                .when()
                .get()
                .then()
                .extract().response();

        String statusAfterCompletion = statusAfterCompletionResponse.jsonPath().getString("status");
        String result = statusAfterCompletionResponse.jsonPath().getString("result");

        assertEquals("Job is ready", statusAfterCompletion);
        System.out.println(statusAfterCompletion);
        // Проверьте также result на предмет ожидаемого значения, если необходимо
    }


    private static final String COLLEAGUE_LOGIN = "super_admin";
    private static final String PASSWORD_API_URL = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    private static final String AUTH_COOKIE_API_URL = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
    private static final String INCORRECT_AUTH_MESSAGE = "You are NOT authorized";
    private static final String CORRECT_AUTH_MESSAGE = "You are authorized";

    @Test
    public void testPasswordRecovery() {
        List<String> commonPasswords = Arrays.asList(
                "123456", "password", "123456789", "12345678", "12345",
                "1234567", "1234567890", "qwerty", "abc123", "111111",
                "123123", "admin", "letmein", "welcome", "monkey",
                "1234", "123", "password1", "123qwe", "123abc",
                "admin123", "passw0rd", "password123", "superadmin"
        );

        for (String password : commonPasswords) {
            // Шаг 1: Получаем авторизационную cookie
            Response getPasswordResponse = given()
                    .param("login", COLLEAGUE_LOGIN)
                    .param("password", password)
                    .when()
                    .post(PASSWORD_API_URL);

            // Извлекаем значение cookie
            String authCookieValue = getPasswordResponse.getCookie("auth_cookie");

            // Шаг 2: Проверяем правильность авторизационной cookie
            Response checkAuthResponse = given()
                    .cookie(new Cookie.Builder("auth_cookie", authCookieValue).build())
                    .when()
                    .get(AUTH_COOKIE_API_URL);

            // Проверяем сообщение в ответе
            String authMessage = checkAuthResponse.getBody().asString();

            if (authMessage.equals(INCORRECT_AUTH_MESSAGE)) {
                // Если пароль неправильный, переходим к следующему
                System.out.println("Incorrect password: " + password);
            } else if (authMessage.equals(CORRECT_AUTH_MESSAGE)) {
                // Если пароль правильный, выводим результат и завершаем тест
                System.out.println("Correct password found: " + password);
                System.out.println("Auth Message: " + authMessage);
                break;
            }
        }
    }
}
