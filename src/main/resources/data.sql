-- Voeg een nieuwe user toe zonder handmatig een ID in te stellen
INSERT INTO users (user_name, email, password, role) VALUES ('John Doe', 'johndoe@example.com', 'password123', 'USER');

-- Voeg een tweede nieuwe user toe
INSERT INTO users (user_name, email, password, role)
VALUES ('Jane Smith', 'janesmith@example.com', 'password456', 'USER');

-- Voeg een derde nieuwe user toe
INSERT INTO users (user_name, email, password, role)
VALUES ('Tom Brown', 'tombrown@example.com', 'password789', 'USER');

-- Voeg een vierde nieuwe user toe
INSERT INTO users (user_name, email, password, role)
VALUES ('Emily White', 'emilywhite@example.com', 'password101', 'ADMIN');

-- Voeg een vijfde nieuwe user toe
INSERT INTO users (user_name, email, password, role)
VALUES ('Haiko White', 'haikowhite@example.com', 'password101', 'ADMIN');

-- Voeg een zesde nieuwe user toe
INSERT INTO users (user_name, email, password, role)
VALUES ('Mieke White', 'miekewhite@example.com', 'password101', 'ADMIN');

-- Voeg de maaltijden toe voor John Doe
-- Ontbijt (Breakfast)
INSERT INTO meals (name, user_count, is_template, created_by_user_id) VALUES ('Breakfast', 0, true, 1);

-- Lunch
INSERT INTO meals (name, user_count, is_template, created_by_user_id) VALUES ('Lunch', 0, true, 1);

-- Diner (Dinner)
INSERT INTO meals (name, user_count, is_template, created_by_user_id) VALUES ('Dinner', 0, true, 1);

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

-- Voeg de maaltijden toe voor Jane Smith
-- Snack
INSERT INTO meals (name, user_count, is_template, created_by_user_id) VALUES ('Snack', 1, true, 2);

-- Salad
INSERT INTO meals (name, user_count, is_template, created_by_user_id) VALUES ('Salad', 1, true, 2);

-- Dinner Special
INSERT INTO meals (name, user_count, is_template, created_by_user_id) VALUES ('Dinner Special', 1, true, 2);


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

-- Koppel de maaltijden aan John Doe (user_id = 1)
INSERT INTO meals (name, user_count, is_template, created_by_user_id, adjusted_by_user_id) VALUES ('Snack', 1, false, 2, 1);
INSERT INTO meals (name, user_count, is_template, created_by_user_id, adjusted_by_user_id) VALUES ('Salad', 1, false, 2, 1);
INSERT INTO meals (name, user_count, is_template, created_by_user_id, adjusted_by_user_id) VALUES ('Dinner Special', 1, false, 2, 1);

-- Koppel de maaltijden aan Jane Smith (user_id = 2)
INSERT INTO meals (name, user_count, is_template, created_by_user_id, adjusted_by_user_id) VALUES ('Breakfast', 0, false, 1, 2);
INSERT INTO meals (name, user_count, is_template, created_by_user_id, adjusted_by_user_id) VALUES ('Lunch', 0, false, 1, 2);
INSERT INTO meals (name, user_count, is_template, created_by_user_id, adjusted_by_user_id) VALUES ('Dinner', 0, false, 1, 2);

-- Koppel de maaltijden aan John Doe (user_id = 1)
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 1); -- Koppel Breakfast aan John Doe
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 2); -- Koppel Lunch aan John Doe
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 3); -- Koppel Dinner aan John Doe

-- Koppel de maaltijden aan Jane Smith (user_id = 2)
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 4); -- Koppel Snack aan Jane Smith
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 5); -- Koppel Salad aan Jane Smith
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 6); -- Koppel Dinner Special aan Jane Smith

