package Models;

import java.util.List;

public class CreateOrder {
    private List<String> ingredients;
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public CreateOrder(List<String> ingredients){
        this.ingredients = ingredients;
    }

    public CreateOrder(){
    }
}
