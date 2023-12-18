package homeWork3;

import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class Homework3 {
    @Test
    public void testStringLength() {
        // Переменная типа String
        String text = "Это текст с длиной больше 15 символов";

        // Проверка длины строки
        if (text.length() <= 15) {
            fail("Тест провален: Длина строки должна быть больше 15 символов");
        } else {
            System.out.println("Тест успешно пройден!");
        }
    }

    @Test
    public void testHomeworkCookie() {
        // Устанавливаем базовый URL
        RestAssured.baseURI = "https://playground.learnqa.ru/api";

        // Отправляем GET-запрос на указанный метод
        Response response = RestAssured.given()
                .when()
                .get("/homework_cookie")
                .then()
                .extract().response();

        // Получаем значение cookie из ответа
        Cookie cookie = response.getDetailedCookie("HomeWorkCookie");

        // Проверяем, что cookie существует и не пуста
        assertNotNull(cookie, "Cookie не найдена в ответе");
        assertNotNull(cookie.getValue(), "Значение cookie не должно быть пустым");

        // Печатаем информацию о cookie в консоль
        System.out.println("Name: " + cookie.getName());
        System.out.println("Value: " + cookie.getValue());

        // Проверяем, что значение cookie соответствует ожидаемому значению
        assertEquals("expected_value", cookie.getValue(), "Значение cookie не соответствует ожидаемому");
    }

    @Test
    public void testHomeworkHeader() {
        // Устанавливаем базовый URL
        RestAssured.baseURI = "https://playground.learnqa.ru/api";

        // Отправляем GET-запрос на указанный метод
        Response response = RestAssured.given()
                .when()
                .get("/homework_header")
                .then()
                .extract().response();

        // Получаем все заголовки ответа
        Headers headers = response.headers();

        // Проверяем, что заголовки существуют и не пусты
        assertNotNull(headers, "Заголовки не найдены в ответе");

        // Печатаем информацию о заголовках в консоль
        for (Header header : headers) {
            System.out.println("Header Name: " + header.getName());
            System.out.println("Header Value: " + header.getValue());
        }

        // Проверяем, что значение заголовка "Expected-Header" соответствует ожидаемому значению
        assertEquals("expected_value", headers.getValue("Expected-Header"),
                "Значение заголовка 'Expected-Header' не соответствует ожидаемому");
    }

    private static final String API_URL = "https://playground.learnqa.ru/ajax/api/user_agent_check";

    @ParameterizedTest
    @ArgumentsSource(UserAgentDataProvider.class)
    public void testUserAgentCheck(String userAgent, String expectedDevice, String expectedBrowser, String expectedPlatform) {
        Response response = RestAssured.given()
                .header("User-Agent", userAgent)
                .when()
                .get(API_URL);

        String actualDevice = response.jsonPath().getString("device");
        String actualBrowser = response.jsonPath().getString("browser");
        String actualPlatform = response.jsonPath().getString("platform");

        assertEquals(expectedDevice, actualDevice, "Incorrect device for User Agent: " + userAgent);
        assertEquals(expectedBrowser, actualBrowser, "Incorrect browser for User Agent: " + userAgent);
        assertEquals(expectedPlatform, actualPlatform, "Incorrect platform for User Agent: " + userAgent);
    }

    private static class UserAgentDataProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // Возвращаем поток аргументов с User Agent'ами и ожидаемыми результатами
            return Stream.of(
                    Arguments.of("User-Agent-String-1", "iOS", "Chrome", "mobile"),
                    Arguments.of("User-Agent-String-2", "Android", "Firefox", "web")
                    // ... добавьте другие User Agent'ы и ожидаемые результаты
            );
        }
    }
}
