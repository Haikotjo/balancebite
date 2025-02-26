-- Voeg rollen toe aan de roles tabel
INSERT INTO roles (rolename) VALUES ('USER');
INSERT INTO roles (rolename) VALUES ('CHEF');
INSERT INTO roles (rolename) VALUES ('ADMIN');

-- Voeg gebruikers toe aan de users tabel (zonder rollen direct in te voegen)
INSERT INTO users (user_name, email, password) VALUES ('John Doe', 'johndoe@example.com', '$2a$10$jPPPOnf7yp7gCZu8MVzApO4euavXZ.RFWFYjuEOPRewVLcWgJQR1C');
INSERT INTO users (user_name, email, password) VALUES ('Jane Smith', 'janesmith@example.com', '$2a$10$EeRkWfH4ZL7rYL2kdtuaHOUAOgr731Z5kjUu0AoxzxUaadjnyi12K');
INSERT INTO users (user_name, email, password) VALUES ('Tom Brown', 'tombrown@example.com', '$2a$10$8kNk0oZxjHrBPqSGg1d9xuhga/0tX8KQkVTBRfvQylu/InVzOQpm2');
INSERT INTO users (user_name, email, password) VALUES ('Emily White', 'emilywhite@example.com', '$2a$10$VZAkFVLuVhPf08LLoK2Z/enlk2gpgzvPMDlb8K1surRSrRz4QN1NO');
INSERT INTO users (user_name, email, password) VALUES ('Haiko White', 'haikowhite@example.com', '$2a$10$WdJdBTECeJE7TfzngaGFK.3xF45rnmmwiUUYeVt4xt0vVSJstKWIW');
INSERT INTO users (user_name, email, password) VALUES ('Mieke White', 'miekewhite@example.com', '$2a$10$5wHAveL167oKGageyZgfGuLP9JJzRFbBHrtGgGd6V2w0hpueNbci6');

-- Koppel gebruikers aan rollen via de user_roles tabel
INSERT INTO user_roles (user_id, roles_rolename) VALUES (1, 'USER');
INSERT INTO user_roles (user_id, roles_rolename) VALUES (2, 'USER');
INSERT INTO user_roles (user_id, roles_rolename) VALUES (3, 'USER');
INSERT INTO user_roles (user_id, roles_rolename) VALUES (4, 'ADMIN');
INSERT INTO user_roles (user_id, roles_rolename) VALUES (5, 'ADMIN');
INSERT INTO user_roles (user_id, roles_rolename) VALUES (6, 'CHEF');

-- Add meals for John Doe
INSERT INTO meals (name, meal_description, user_count, is_template, created_by_user_id, image_url, total_calories, total_protein, total_carbs, total_fat, meal_type, cuisine, diet)
VALUES
    ('Breakfast',
     'Start your day with a nutritious breakfast featuring bananas, nonfat milk, and whole-wheat bread.',
     1, true, 1,
     'https://images.unsplash.com/photo-1473093295043-cdd812d0e601?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     267, 13, 53, 2,
     'BREAKFAST', 'AMERICAN', 'VEGETARIAN'),

    ('Lunch',
     'A vibrant lunch with peas, carrots, spinach, strawberries, and avocado.',
     1, true, 1,
     'https://images.unsplash.com/photo-1485451456034-3f9391c6f769?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     584, 25, 76, 37,
     'LUNCH', 'ITALIAN', 'VEGETARIAN'),

    ('Dinner',
     'A hearty dinner with sweet corn, cauliflower, and hard-boiled eggs.',
     1, true, 1,
     'https://images.unsplash.com/photo-1505932049984-db368d92fa63?q=80&w=1936&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     125, 8, 24, 4,
     'DINNER', 'JAPANESE', 'PESCATARIAN');

-- Voeg de ingrediënten voor het ontbijt (Breakfast) toe
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity, food_item_name)
VALUES (1, 4, 126, 'Bananas, raw');
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity, food_item_name)
VALUES (1, 17, 245, 'Milk, nonfat, fluid, with added vitamin A and vitamin D (fat free or skim)');
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity, food_item_name)
VALUES (1, 15, 28.35, 'Bread, whole-wheat, commercially prepared');

-- Voeg de ingrediënten voor de lunch (Lunch) toe
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (2, 1, 'Peas and carrots, frozen', 278);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (2, 2, 'Spinach, frozen', 284);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (2, 7, 'Strawberries, raw', 200);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (2, 11, 'Avocados, raw', 230);

-- Voeg de ingrediënten voor het diner (Dinner) toe
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (3, 3, 'Corn, sweet, yellow', 89);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (3, 8, 'Cauliflower, raw', 100);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (3, 14, 'Egg, whole, cooked, hard-boiled', 25.5);

-- Add meals for Jane Smith
INSERT INTO meals (name, meal_description, user_count, is_template, created_by_user_id, image_url, total_calories, total_protein, total_carbs, total_fat, meal_type, cuisine, diet)
VALUES
    ('Snack',
     'A delightful snack of fresh apples and crunchy almonds.',
     1, true, 2,
     'https://plus.unsplash.com/premium_photo-1678481245533-3b5c7a5e3d37?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     81, 6, 12, 3,
     'SNACK', 'GREEK', 'VEGAN'),

    ('Salad',
     'A refreshing salad with romaine lettuce, tomatoes, and cucumbers.',
     1, true, 2,
     'https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     200, 5, 30, 5,
     'LUNCH', 'ITALIAN', 'LOW_FAT'),

    ('Dinner Special',
     'A special dinner featuring roasted chicken, white rice, and broccoli.',
     1, true, 2,
     'https://images.unsplash.com/photo-1605926637512-c8b131444a4b?q=80&w=2080&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D',
     658, 22, 36, 48,
     'DINNER', 'AMERICAN', 'HIGH_PROTEIN');

-- Voeg de ingrediënten voor het Dinner Special toe
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (6, 13, 'Chicken, cooked, roasted', 140);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (6, 14, 'Rice, white, cooked', 158);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (6, 16, 'Broccoli, raw', 91);


-- Voeg ingrediënten toe
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (4, 5, 'Apple, raw', 182);
INSERT INTO meal_ingredients (meal_id, food_item_id, food_item_name, quantity) VALUES (4, 6, 'Almonds, raw', 28);

-- Koppel de maaltijden aan gebruikers
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 1);
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 2);
INSERT INTO user_meals (user_id, meal_id) VALUES (1, 3);
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 4);
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 5);
INSERT INTO user_meals (user_id, meal_id) VALUES (2, 6);
