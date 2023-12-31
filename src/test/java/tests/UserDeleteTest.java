package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestcase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testDeleteUserUnauthorized() {
        // Try to delete user with ID 2 without authentication
        Response response = apiCoreRequests.deleteUser("2");

        // Verify that the system does not allow deleting the user
        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "You can't delete user");
    }

    @Test
    public void testDeleteUserPositive() {
        // Create a new user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String userId = apiCoreRequests.createAndLoginUser(userData);

        // Delete the user
        Response deleteResponse = apiCoreRequests.deleteUser(userId);

        // Verify that the user is deleted successfully
        Assertions.assertResponseCodeEquals(deleteResponse, 200);

        // Try to get user details by ID and verify that the user is not found
        Response getUserResponse = apiCoreRequests.getUserData(userId);
        Assertions.assertResponseCodeEquals(getUserResponse, 404);
    }

    @Test
    public void testDeleteUserAsDifferentUser() {
        // Create two users
        Map<String, String> user1Data = DataGenerator.getRegistrationData();
        Map<String, String> user2Data = DataGenerator.getRegistrationData();

        String user1Id = apiCoreRequests.createAndLoginUser(user1Data);
        String user2Id = apiCoreRequests.createAndLoginUser(user2Data);

        // Try to delete user1 while being authenticated as user2
        Response deleteResponse = apiCoreRequests.deleteUser(user1Id);

        // Verify that the system does not allow deleting the user
        Assertions.assertResponseCodeEquals(deleteResponse, 400);
        Assertions.assertResponseTextEquals(deleteResponse, "You can't delete user");
    }
}
