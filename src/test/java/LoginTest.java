import Models.User;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginTest {

    private Helper helper;
    private String accessToken;

    @Before
    public void setUp(){
        helper = new Helper();
    }
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            helper.deleteUserByToken(accessToken);
        }
    }

    @Test
    @Description("Логин под существующим пользователем")
    public void loginUser(){
        // создание пользователя
       User user = helper.generateRandomUser();
       Response registerUserResponse = helper.createUniqueUser(user);
       accessToken = helper.verifyUserCreation(registerUserResponse, user);

        Response loginUserResponse = helper.loginWithUser(user);
        accessToken = helper.verifyLoginSuccess(loginUserResponse);

    }

    @Test
    @Description("Отсутствие логина пользователя с некорректным email")
    public void incorrectEmailUser() {
        User user = helper.generateRandomUser();
        user.setEmail("notemail");
        Response inccorectEmailResponse = helper.loginWithUser(user);
        helper.verifyLoginWithInvalidCredentials(inccorectEmailResponse);
        }

    @Test
    @Description("Отсутствие логина пользователя с некорректным password")
    public void incorrectPasswordUser() {
        User user = helper.generateRandomUser();
        user.setPassword("notpassword");
        Response inccorectEmailResponse = helper.loginWithUser(user);
        helper.verifyLoginWithInvalidCredentials(inccorectEmailResponse);
    }
}

