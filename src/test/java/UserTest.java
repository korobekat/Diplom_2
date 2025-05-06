import Models.User;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
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
    @Description("Создание уникального пользователя")
    public void registerUser(){
        User user = helper.generateRandomUser();
        Response registerUserResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerUserResponse, user);
    }

    @Test
    @Description("Отсутствие возможности создания ранее зарегистрированного пользователя")
    public void registerSameUser(){
        User user = helper.generateRandomUser();
        Response registerUserResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerUserResponse, user);

        Response sameResponse = helper.createUniqueUser(user);
        helper.verifyDuplicateUserError(sameResponse);
    }

    @Test
    @Description("Отсутствие возможности создания пользователя без обязательного поля email")
    public void registerUserWithoutEmailField(){
        User user = helper.generateRandomUser();
        user.setEmail("");
        Response emptyEmailResponse = helper.createUniqueUser(user);
        helper.verifyUserCreationWithEmptyFields(emptyEmailResponse);
    }

    @Test
    @Description("Отсутствие возможности создания пользователя без обязательного поля password")
    public void registerUserWithoutPasswordField(){
        User user = helper.generateRandomUser();
        user.setPassword("");
        Response emptyPasswordResponse = helper.createUniqueUser(user);
        helper.verifyUserCreationWithEmptyFields(emptyPasswordResponse);
    }

    @Test
    @Description("Отсутствие возможности создания пользователя без обязательного поля name")
    public void registerUserWithoutNameField(){
        User user = helper.generateRandomUser();
        user.setName("");
        Response emptyNameResponse = helper.createUniqueUser(user);
        helper.verifyUserCreationWithEmptyFields(emptyNameResponse);
    }
}
