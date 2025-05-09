import Models.CreateOrder;
import Models.Order;
import Models.OrderClient;
import Models.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;


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
        ValidatableResponse getResponse = OrderClient.getIngredients();
        String firstIngredient = getResponse.extract().path("data[0]._id");
        String secondIngredient = getResponse.extract().path("data[1]._id");
        List<String> ingredients = new ArrayList<>();
        ingredients.add(firstIngredient);
        ingredients.add(secondIngredient);
        Order order = new Order();
        order.setIngredients(ingredients);

        ValidatableResponse createResponse = OrderClient.create(accessToken, order);
        assertEquals(200, createResponse.extract().statusCode());
        assertEquals( true, createResponse.extract().path("success"));
    }


    // баг, нельзя создать заказ без авторизации
    @Test
    @Description("Отсутствие создания заказа без авторизации, но с ингредиентами)")
    public void createOrderWithoutAuthorization(){
        ValidatableResponse getResponse = OrderClient.getIngredients();
        String firstIngredient = getResponse.extract().path("data[0]._id");
        String secondIngredient = getResponse.extract().path("data[1]._id");
        List<String> ingredients = new ArrayList<>();
        ingredients.add(firstIngredient);
        ingredients.add(secondIngredient);
        Order order = new Order();
        order.setIngredients(ingredients);

        ValidatableResponse createResponse = OrderClient.create(null, order);
        assertEquals( 401, createResponse.extract().statusCode());
        assertEquals( false, createResponse.extract().path("success"));
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
        ingredients.add(randomUUID().toString());
        ingredients.add(randomUUID().toString());
        CreateOrder createOrder = new CreateOrder(ingredients);

        Response createOrderResponse = helper.createOrderWithInvalidIngredientsHash(accessToken, createOrder);
        helper.verifyOrderCreationInvalidIngredientsHash(createOrderResponse);
    }
}
