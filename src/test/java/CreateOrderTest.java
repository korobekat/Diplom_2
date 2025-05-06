import Models.CreateOrder;
import Models.User;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class CreateOrderTest {
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
    @Description("создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorization(){
        User user = helper.generateRandomUser();

        Response registerResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerResponse, user);

        var ingredients = new ArrayList<String>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa6f");
        CreateOrder createOrder = new CreateOrder(ingredients);

        Response createOrderResponse = helper.createOrderWithIngredients(accessToken, createOrder);
        helper.verifyOrderCreation(createOrderResponse);
    }

    // баг, нельзя создать заказ без авторизации
    @Test
    @Description("Отсутствие создания заказа без авторизации, но с ингредиентами)")
    public void createOrderWithoutAuthorization(){
        User user = helper.generateRandomUser();

        Response registerResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation( registerResponse, user);

        var ingredients = new ArrayList<String>();
        ingredients.add("61c0c5a71d1f82001bdaaa6d");
        ingredients.add("61c0c5a71d1f82001bdaaa73");
        CreateOrder createOrder = new CreateOrder(ingredients);

        Response createOrderResponse = helper.createOrderWithoutAuthorization(createOrder);
        helper.verifyOrderCreationUnauthorized(createOrderResponse);
    }

    @Test
    @Description("Отсутствие создания заказа с авторизацией, но без ингредиентов")
    public void createOrderWithNoIngredient(){
        User user = helper.generateRandomUser();
        Response registerResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerResponse, user);

        Response createOrderResponse = helper.createOrderWitNoIngredients(accessToken);
        helper.verifyOrderCreationNoIngredients(createOrderResponse);
    }

    // баг, нельзя создать заказ без авторизации
    @Test
    @Description("Отсутствие создания заказа без ингредиентов и без авторизации")
    public void createOrderWithoutAuthorizationAndIngredients(){
        User user = helper.generateRandomUser();
        Response registerResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerResponse, user);

        Response createOrderResponse = helper.createOrderWithoutAuthorizationAndIngredients();
        helper.verifyOrderCreationWithoutAuthorization(createOrderResponse);
    }

    @Test
    @Description("Отсутствие создания заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredient(){
        User user = helper.generateRandomUser();
        Response registerResponse = helper.createUniqueUser(user);
        accessToken = helper.verifyUserCreation(registerResponse, user);

        var ingredients = new ArrayList<String>();
        ingredients.add("dfgh444");
        ingredients.add("45tggg");
        CreateOrder createOrder = new CreateOrder(ingredients);

        Response createOrderResponse = helper.createOrderWithInvalidIngredientsHash(accessToken, createOrder);
        helper.verifyOrderCreationInvalidIngredientsHash(createOrderResponse);
    }
}
