package lib;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestcase {

    private ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testEditJustCreatedTest() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");


        // LOGIN

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEdituser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditUserUnauthenticated() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String userId = apiCoreRequests.createAndLoginUser(userData);

        // EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        // UNAUTHENTICATED EDIT
        Response responseEditUserUnauthenticated = apiCoreRequests.editUserDataUnauthenticated(userId, editData);

        // ASSERTIONS
        Assertions.assertResponseCodeEquals(responseEditUserUnauthenticated, 401);
    }

    @Test
    public void testEditUserAsDifferentUser() {
        // GENERATE USERS
        Map<String, String> userData1 = DataGenerator.getRegistrationData();
        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        String userId1 = apiCoreRequests.createAndLoginUser(userData1);
        apiCoreRequests.logoutUser(); // Logout after creating the first user
        String userId2 = apiCoreRequests.createAndLoginUser(userData2);

        // EDIT USER1 AS USER2
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        // EDIT USER1 AS USER2
        Response responseEditUserAsDifferentUser = apiCoreRequests.editUserData(userId1, editData);

        // ASSERTIONS
        Assertions.assertResponseCodeEquals(responseEditUserAsDifferentUser, 403);
    }

    @Test
    public void testEditUserInvalidEmail() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String userId = apiCoreRequests.createAndLoginUser(userData);

        // EDIT USER WITH INVALID EMAIL
        String newEmail = "newemailwithout@symbol";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        // EDIT USER WITH INVALID EMAIL
        Response responseEditUserInvalidEmail = apiCoreRequests.editUserData(userId, editData);

        // ASSERTIONS
        Assertions.assertResponseCodeEquals(responseEditUserInvalidEmail, 400);
    }

    @Test
    public void testEditUserShortFirstName() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String userId = apiCoreRequests.createAndLoginUser(userData);

        // EDIT USER WITH SHORT FIRST NAME
        String shortFirstName = "A";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", shortFirstName);

        // EDIT USER WITH SHORT FIRST NAME
        Response responseEditUserShortFirstName = apiCoreRequests.editUserData(userId, editData);

        // ASSERTIONS
        Assertions.assertResponseCodeEquals(responseEditUserShortFirstName, 400);
    }

}
