package Models;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    @Step("Создание нового заказа")
    public static ValidatableResponse create(String token, Order order) {
        RequestSpecification requestSpecification = given();
        if (token != null) {
            requestSpecification.header("Authorization", token);
        }
        return requestSpecification
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/orders")
                .then();
    }

    @Step("Получение заказов конкретного пользователя")
    public static ValidatableResponse getByUser(String token) {
        RequestSpecification requestSpecification = given();
        if (token != null) {
            requestSpecification.header("Authorization", token);
        }
        return requestSpecification
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .get("/api/orders")
                .then();
    }

    @Step("Получение списка допустимых ингридиентов")
    public static ValidatableResponse getIngredients() {
        RequestSpecification requestSpecification = given();
        return requestSpecification
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .get("/api/ingredients")
                .then();
    }
}