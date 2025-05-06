import Models.CreateOrder;
import Models.User;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.security.SecureRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Helper {

    public static final String REGISTER_USER_METHOD = "api/auth/register";
    public static final String USER_LOGIN_METHOD = "/api/auth/login";
    public static final String USER_UPDATE_METHOD = "/api/auth/user";
    public static final String CREATE_ORDER_METHOD = "/api/orders";
    public static final String GET_ORDERS_METHOD = "/api/orders";
    public static final String DELETE_USER_METHOD = "/api/auth/user";

    public static final String DELETE_USER_MESSAGE = "User successfully removed";
    public static final String REGISTER_SAME_USER_ERROR_MESSAGE = "User already exists";
    public static final String LACK_DATA_TO_CREATE_USER = "Email, password and name are required fields";
    public static final String INCORRECT_LOGIN_OR_PASSWORD = "email or password are incorrect";
    public static final String UNAUTHORIZED_ERROR_MESSAGE = "You should be authorised";
    public static final String CREATE_ORDER_WITHOUT_INGREDIENT = "Ingredient ids must be provided";

    public Helper(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Step("Отправить запрос DELETE на удаление пользователя по токену /api/auth/user")
    public void deleteUserByToken(String cleanToken) {
        // Отправляем запрос на удаление пользователя
        Response deleteResponse = given()
                .header("Authorization", cleanToken)
                .when()
                .delete(DELETE_USER_METHOD);

        // Проверка успешного удаления
        assertThat(deleteResponse.getStatusCode(), Matchers.is(202));
        assertThat(deleteResponse.jsonPath().getBoolean("success"), Matchers.is(true));

        // Проверяем правильное сообщение
        assertThat(deleteResponse.jsonPath().getString("message"), Matchers.is(DELETE_USER_MESSAGE));
    }

    /**
     * Создает модель рандомного пользователя
     *
     * @return Модель пользователя
     */
    public User generateRandomUser() {
        // You can customize the characters that you want to add into
        // the random strings
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String NUMBER = "0123456789";

        SecureRandom random = new SecureRandom();
        int length = 5;
        StringBuilder sbEmail = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(CHAR_LOWER.length());
            char rndChar = CHAR_LOWER.charAt(rndCharAt);

            sbEmail.append(rndChar);
        }
        sbEmail.append("@mail.ru");

        StringBuilder sbPassword = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(NUMBER.length());
            char rndChar = NUMBER.charAt(rndCharAt);

            sbPassword.append(rndChar);
        }

        StringBuilder sbName = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(CHAR_LOWER.length());
            char rndChar = CHAR_LOWER.charAt(rndCharAt);

            sbName.append(rndChar);
        }

        return new User(sbEmail.toString(), sbPassword.toString(), sbName.toString());
    }

    @Step("Отправить запрос POST на создание уникального пользователя api/auth/register")
    public Response createUniqueUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(REGISTER_USER_METHOD);
    }

    @Step("Проверить ответ на создание уникального пользователя")
    public String verifyUserCreation(Response registerUserResponse, User user) {
        assertThat(registerUserResponse.getStatusCode(), is(200));
        assertThat(registerUserResponse.jsonPath().getBoolean("success"), Matchers.is(true));
        // Проверяем email и имя пользователя соответствуют ожиданиям
        assertThat(registerUserResponse.jsonPath().getString("user.email"), Matchers.is(user.getEmail()));
        assertThat(registerUserResponse.jsonPath().getString("user.name"), Matchers.is(user.getName()));

        // Проверяем, что accessToken и refreshToken не пустые
        String accessToken = registerUserResponse.jsonPath().getString("accessToken");
        assertThat(accessToken, not(isEmptyOrNullString()));
        assertThat(registerUserResponse.jsonPath().getString("refreshToken"), not(isEmptyOrNullString()));
        // Возвращаем accessToken для последующего использования
        return accessToken;
    }

    @Step("Проверка ошибки при повторной регистрации пользователя")
    public void verifyDuplicateUserError(Response duplicateUserResponse) {
        // Проверка кода ответа и сообщения об ошибке
        assertThat(duplicateUserResponse.getStatusCode(), is(403));
        assertThat(duplicateUserResponse.jsonPath().getString("message"), equalTo(REGISTER_SAME_USER_ERROR_MESSAGE));
    }

    @Step("Проверка ошибки при создание пользователя без password/email/name")
    public void verifyUserCreationWithEmptyFields(Response response) {
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is(LACK_DATA_TO_CREATE_USER));
    }

    // Методы для тестового класса LoginTest
    @Step("Отправить запрос POST /api/auth/login для получения логина существующего пользователя")
    public Response loginWithUser(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(USER_LOGIN_METHOD);
    }

    @Step("Проверка ответа успешного логина пользователя")
    public String verifyLoginSuccess(Response loginResponse) {
        assertThat(loginResponse.getStatusCode(), equalTo(200));
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(true));
        // Извлечение токена из ответа и возврат его
        return loginResponse.jsonPath().getString("accessToken");
    }


    @Step("Проверка ответа выполнения логина с неверными email или паролем")
    public void verifyLoginWithInvalidCredentials(Response loginResponse) {
        assertThat(loginResponse.getStatusCode(), is(401));
        assertThat(loginResponse.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(loginResponse.jsonPath().getString("message"), is(INCORRECT_LOGIN_OR_PASSWORD));
    }


    // Методы для тестового класса UserDataUpdateTest
    @Step ("Отправить запрос PATCH /api/auth/user для обновления email/name/password пользователя с авторизацией")
    public Response updateUser(String accessToken, User user) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(USER_UPDATE_METHOD);
    }

    @Step ("Проверка ответа обновления email/name/password пользователя с авторизацией")
    public void verifyUserUpdate(Response response, User user) {
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        // Проверяем email и имя пользователя соответствуют ожиданиям
        assertThat(response.jsonPath().getString("user.email"), is(user.getEmail()));
        assertThat(response.jsonPath().getString("user.name"), is(user.getName()));
    }

    @Step ("Отправить запрос PATCH /api/auth/user для обновления email/name/password пользователя без авторизации")
    public Response updateUserWithoutAuthorization(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .patch(USER_UPDATE_METHOD);
    }

    @Step ("Проверка ответа обновления email/name/password пользователя без авторизации")
    public void verifyUserUpdateWithoutAuthorization(Response response, User user) {
        assertThat(response.getStatusCode(), is(401));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(response.jsonPath().getString("message"), is(UNAUTHORIZED_ERROR_MESSAGE));
    }


    // Методы для тестового класса CreateOrderTest
    @Step("Отправить запрос POST /api/orders для создания заказа с ингредиентами и авторизацией")
    public Response createOrderWithIngredients(String accessToken, CreateOrder createOrder) {
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(createOrder)
                .when()
                .post(CREATE_ORDER_METHOD);
    }

    @Step("Проверка ответа успешного создания заказа с ингредиентами и авторизацией")
    public void verifyOrderCreation(Response orderResponse) {
        assertThat(orderResponse.getStatusCode(), is(200));
        assertThat(orderResponse.jsonPath().getBoolean("success"), is(true));
        assertThat(orderResponse.jsonPath().getString("name"), not(isEmptyOrNullString()));
        assertThat(orderResponse.jsonPath().getString("order.number"), not(isEmptyOrNullString()));
    }


    // Создание заказа с авторизацией, но без ингредиентов
    @Step("Отправить запрос POST /api/orders для создания заказа с авторизацией но без ингредиентов")
    public  Response createOrderWitNoIngredients(String accessToken) {
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .post(CREATE_ORDER_METHOD);
    }

    @Step("Проверка ответа ошибки создания заказа с авторизацией, но без ингредиентов")
    public  void verifyOrderCreationNoIngredients(Response orderResponse) {
        assertThat(orderResponse.getStatusCode(), is(400));
        assertThat(orderResponse.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(orderResponse.jsonPath().getString("message"), is(CREATE_ORDER_WITHOUT_INGREDIENT));
    }


    // Создание заказа без авторизации, но с ингредиентами
    @Step("Отправить запрос POST /api/orders для создания заказа с ингредиентами но без авторизации")
    public  Response createOrderWithoutAuthorization(CreateOrder createOrder) {
        // Отправка запроса на создание заказа без авторизации
        return given()
                .contentType(ContentType.JSON)
                .body(createOrder)
                .when()
                .post(CREATE_ORDER_METHOD);
    }

    @Step("Проверка ответа при отсутствии авторизации создания заказа с ингредиентами")
    public  void verifyOrderCreationUnauthorized(Response orderResponse) {
        assertThat(orderResponse.getStatusCode(), is(401));
        assertThat(orderResponse.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(orderResponse.jsonPath().getString("message"), is(UNAUTHORIZED_ERROR_MESSAGE));
    }


    // Создание заказа без авторизации и без ингредиентов
    @Step("Отправить запрос POST /api/orders для создания заказа без авторизации и без ингредиентов")
    public  Response createOrderWithoutAuthorizationAndIngredients() {
        // Отправка запроса на создание заказа без авторизации
        return given()
                .contentType(ContentType.JSON)
                .when()
                .post(CREATE_ORDER_METHOD);
    }

    @Step("Проверка ответа создания заказа при отсутствии авторизации")
    public  void verifyOrderCreationWithoutAuthorization(Response orderResponse) {
        assertThat(orderResponse.getStatusCode(), is(401));
        assertThat(orderResponse.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(orderResponse.jsonPath().getString("message"), is(UNAUTHORIZED_ERROR_MESSAGE));
    }

    @Step("Проверка ответа создания заказа при отсутствии ингредиентов")
    public  void verifyOrderCreationWithoutIngredients(Response orderResponse) {
        assertThat(orderResponse.getStatusCode(), is(400));
        assertThat(orderResponse.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(orderResponse.jsonPath().getString("message"), is(CREATE_ORDER_WITHOUT_INGREDIENT));
    }

    @Step("Отправить запрос POST /api/orders для создания заказа с неверным хешем ингредиентов")
    public  Response createOrderWithInvalidIngredientsHash(String accessToken, CreateOrder createOrder) {

        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(createOrder)
                .when()
                .post(CREATE_ORDER_METHOD);
    }

    @Step("Проверка ответа при неверном хеше ингредиентов")
    public  void verifyOrderCreationInvalidIngredientsHash(Response orderResponse) {
        assertThat(orderResponse.getStatusCode(), is(500));
    }



    // Методы для тестового класса GetOrdersTest
    // получения списка заказов с авторизацией
    @Step("Отправить запрос GET /api/orders для получения списка заказов авторизованного пользователя")
    public  Response getUserOrders(String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .when()
                .get(GET_ORDERS_METHOD);
    }

    @Step("Проверка ответа успешного получения списка заказов")
    public  void verifyUserOrdersRetrieval(Response getResponse) {
        assertThat(getResponse.getStatusCode(), is(200));
        assertThat(getResponse.jsonPath().getBoolean("success"), is(true));
        assertThat(getResponse.jsonPath().getString("orders"), not(isEmptyOrNullString()));
    }


    // Без авторизации
    @Step("Отправить запрос GET /api/orders для получения списка заказов без авторизации")
    public  Response getUserOrdersWithoutAuthorization() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(GET_ORDERS_METHOD);
    }

    @Step("Проверка ответа ошибки получения списка заказов пользователя без авторизации")
    public void verifyUnauthorizedOrdersRetrievalResponse(Response getResponse) {

        assertThat(getResponse.getStatusCode(), is(401));
        assertThat(getResponse.jsonPath().getBoolean("success"), is(false));
        // Ожидаемое сообщение
        assertThat(getResponse.jsonPath().getString("message"), is(UNAUTHORIZED_ERROR_MESSAGE));
    }
}


