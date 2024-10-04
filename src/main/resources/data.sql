-- Voeg een nieuwe user toe zonder handmatig een ID in te stellen
INSERT INTO users (user_name, email, password) VALUES ('John Doe', 'johndoe@example.com', 'password123');

-- Voeg een tweede nieuwe user toe
INSERT INTO users (user_name, email, password, weight, age, height, gender, activity_level, goal, role)
VALUES
    ('Jane Smith', 'janesmith@example.com', 'password456', 65.0, 25, 165.0, 'FEMALE', 'MODERATE', 'WEIGHT_LOSS', 'USER');

-- Voeg een derde nieuwe user toe
INSERT INTO users (user_name, email, password, weight, age, height, gender, activity_level, goal, role)
VALUES
    ('Tom Brown', 'tombrown@example.com', 'password789', 95.0, 35, 185.0, 'MALE', 'SEDENTARY', 'WEIGHT_GAIN', 'USER');

-- Voeg een vierde nieuwe user toe
INSERT INTO users (user_name, email, password, weight, age, height, gender, activity_level, goal, role)
VALUES
    ('Emily White', 'emilywhite@example.com', 'password101', 55.0, 28, 170.0, 'FEMALE', 'LIGHT', 'MAINTENANCE', 'ADMIN');
-- Voeg de maaltijden toe voor John Doe
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

-- Koppel de maaltijden aan John Doe (user_id = 1)
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 1); -- Ontbijt
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 2); -- Lunch
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 3); -- Diner

-- Voeg de maaltijden toe voor Jane Smith
-- Snack
INSERT INTO meals (name) VALUES ('Snack');

-- Salad
INSERT INTO meals (name) VALUES ('Salad');

-- Dinner Special
INSERT INTO meals (name) VALUES ('Dinner Special');

-- Voeg de ingrediënten voor de snack toe
-- Apple, raw (1 medium = 182 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (4, 5, 182);
-- Almonds, raw (28 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (4, 6, 28);

-- Voeg de ingrediënten voor de salade toe
-- Lettuce, romaine (1 head = 626 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (5, 9, 626);
-- Tomatoes, raw (1 medium = 123 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (5, 10, 123);
-- Cucumbers, raw (1 medium = 201 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (5, 12, 201);

-- Voeg de ingrediënten voor het Dinner Special toe
-- Chicken, cooked, roasted (1 cup = 140 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (6, 13, 140);
-- Rice, white, cooked (1 cup = 158 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (6, 14, 158);
-- Broccoli, raw (1 cup = 91 grams)
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity) VALUES (6, 16, 91);

-- Koppel de maaltijden aan Jane Smith (user_id = 2)
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 4); -- Snack
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 5); -- Salad
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 6); -- Dinner Special