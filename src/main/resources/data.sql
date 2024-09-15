-- Voeg een user toe
-- INSERT INTO users (id, name, email, password) VALUES ('John Doe', 'johndoe@example.com', 'password123');

-- Voeg de maaltijden toe
-- Ontbijt (Breakfast)
INSERT INTO meals (name) VALUES ('Breakfast');

-- Lunch
INSERT INTO meals (name) VALUES ('Lunch');

-- Diner (Dinner)
INSERT INTO meals (name) VALUES ('Dinner');

-- Voeg de ingrediënten voor het ontbijt (Breakfast) toe
-- Bananas, raw (126 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (1, 4, 126);
-- Milk, nonfat, fluid (1 cup = 245 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (1, 17, 245);
-- Bread, whole-wheat (1 slice = 28.35 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (1, 15, 28.35);

-- Voeg de ingrediënten voor de lunch (Lunch) toe
-- Peas and carrots, frozen (1 package = 278 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (2, 1, 278);
-- Spinach, frozen (1 package = 284 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (2, 2, 284);
-- Strawberries, raw (no specific portion, but we assume 200 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (2, 7, 200);
-- Avocados, raw (1 cup pureed = 230 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (2, 11, 230);

-- Voeg de ingrediënten voor het diner (Dinner) toe
-- Corn, sweet, yellow (1 ear small = 89 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (3, 3, 89);
-- Cauliflower, raw (100 grams assumed)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (3, 8, 100);
-- Egg, whole, cooked, hard-boiled (1 tbsp = 8.5 grams, assume 3 tablespoons)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (3, 14, 25.5);

-- Koppel de user aan de maaltijden
-- INSERT INTO user_meals (user_id, meal_id) VALUES (1, 1); -- User heeft het ontbijt
-- INSERT INTO user_meals (user_id, meal_id) VALUES (1, 2); -- User heeft de lunch
-- INSERT INTO user_meals (user_id, meal_id) VALUES (1, 3); -- User heeft het diner
