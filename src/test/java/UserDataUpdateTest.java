import Models.User;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UserDataUpdateTest {
    private String accessToken;
    private Helper helper;

    @Before
    public void setUp() {
        helper = new Helper();
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            helper.deleteUserByToken(accessToken);
        }
    }

    @Test
    @Description("Изменение email пользователя с авторизацией")
    public void updateUserWithAuthorizationEmail() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setEmail("test.@mail.ru");

        Response updateResponse = helper.updateUser(accessToken, user);
        helper.verifyUserUpdate(updateResponse, user);
    }

    @Test
    @Description("Изменение password пользователя с авторизацией")
    public void updateUserWithAuthorizationPassword() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setPassword("test");

        Response updateResponse = helper.updateUser(accessToken, user);
        helper.verifyUserUpdate(updateResponse, user);
    }

    @Test
    @Description("Изменение name пользователя с авторизацией")
    public void updateUserWithAuthorizationName() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setName("test");

        Response updateResponse = helper.updateUser(accessToken, user);
        helper.verifyUserUpdate(updateResponse, user);
    }

    @Test
    @Description("Изменение всех полей пользователя с авторизацией")
    public void updateUserWithAuthorizationAll() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setEmail("test.@mail.ru");
        user.setPassword("test");
        user.setName("test");

        Response updateResponse = helper.updateUser(accessToken, user);
        helper.verifyUserUpdate(updateResponse, user);
    }

    @Test
    @Description("Изменение email пользователя без авторизации")
    public void updateUserWithoutAuthorizationEmail() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setEmail("test.@mail.ru");

        Response updateResponse = helper.updateUserWithoutAuthorization(user);
        helper.verifyUserUpdateWithoutAuthorization(updateResponse, user);
    }

    @Test
    @Description("Изменение password пользователя без авторизации")
    public void updateUserWithoutAuthorizationPassword() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setPassword("test");

        Response updateResponse = helper.updateUserWithoutAuthorization(user);
        helper.verifyUserUpdateWithoutAuthorization(updateResponse, user);
    }

    @Test
    @Description("Изменение name пользователя без авторизации")
    public void updateUserWithoutAuthorizationName() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);
        user.setName("test");

        Response updateResponse = helper.updateUserWithoutAuthorization(user);
        helper.verifyUserUpdateWithoutAuthorization(updateResponse, user);
    }

    @Test
    @Description("Изменение всех полей пользователя без авторизации")
    public void updateUserWithoutAuthorizationAll() {
        User user = helper.generateRandomUser();
        Response response = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(response, user);

        user.setEmail("test.@mail.ru");
        user.setPassword("test");
        user.setName("test");

        Response updateResponse = helper.updateUserWithoutAuthorization(user);
        helper.verifyUserUpdateWithoutAuthorization(updateResponse, user);
    }
}
