package balancebite.utils;

import balancebite.model.MealIngredient;
import balancebite.model.diet.DietDay;
import balancebite.model.diet.DietPlan;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.meal.Meal;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCartCalculator {

    public static Map<FoodItem, Double> calculateShoppingList(DietPlan dietPlan) {
        Map<FoodItem, Double> shoppingMap = new HashMap<>();

        for (DietDay day : dietPlan.getDietDays()) {
            for (Meal meal : day.getMeals()) {
                for (MealIngredient ingredient : meal.getMealIngredients()) {
                    FoodItem item = ingredient.getFoodItem();
                    double qty = ingredient.getQuantity();

                    shoppingMap.merge(item, qty, Double::sum);
                }
            }
        }

        return shoppingMap;
    }
}

