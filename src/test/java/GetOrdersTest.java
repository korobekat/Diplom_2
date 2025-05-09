import Models.User;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetOrdersTest {
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
    @Description("Получение заказов конкретного пользователя с авторизацией")
    public void getOrdersWithAuthorization(){
        User user = helper.generateRandomUser();
        Response registerUserResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerUserResponse, user);

        Response getOrderResponse = helper.getUserOrders(accessToken);
        helper.verifyUserOrdersRetrieval(getOrderResponse);
    }

    @Test
    @Description("Отсутствие получение заказов конкретного пользователя без авторизации")
    public void getOrdersWithoutAuthorization(){
        User user = helper.generateRandomUser();
        Response registerUserResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerUserResponse, user);

        Response getOrderResponse = helper.getUserOrdersWithoutAuthorization();
        helper.verifyUnauthorizedOrdersRetrievalResponse(getOrderResponse);
    }
}

