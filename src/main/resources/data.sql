-- Voeg rollen toe
INSERT INTO roles (rolename) VALUES ('USER');
INSERT INTO roles (rolename) VALUES ('CHEF');
INSERT INTO roles (rolename) VALUES ('ADMIN');

-- Voeg gebruikers toe
INSERT INTO users (user_name, email, password) VALUES
                                                   ('John Doe', 'johndoe@example.com', '$2a$10$jPPPOnf7yp7gCZu8MVzApO4euavXZ.RFWFYjuEOPRewVLcWgJQR1C'),
                                                   ('Jane Smith', 'janesmith@example.com', '$2a$10$EeRkWfH4ZL7rYL2kdtuaHOUAOgr731Z5kjUu0AoxzxUaadjnyi12K'),
                                                   ('Tom Brown', 'tombrown@example.com', '$2a$10$8kNk0oZxjHrBPqSGg1d9xuhga/0tX8KQkVTBRfvQylu/InVzOQpm2'),
                                                   ('Emily White', 'emilywhite@example.com', '$2a$10$VZAkFVLuVhPf08LLoK2Z/enlk2gpgzvPMDlb8K1surRSrRz4QN1NO'),
                                                   ('Haiko White', 'haikowhite@example.com', '$2a$10$WdJdBTECeJE7TfzngaGFK.3xF45rnmmwiUUYeVt4xt0vVSJstKWIW'),
                                                   ('Mieke White', 'miekewhite@example.com', '$2a$10$5wHAveL167oKGageyZgfGuLP9JJzRFbBHrtGgGd6V2w0hpueNbci6');

-- Koppel gebruikers aan rollen
INSERT INTO user_roles (user_id, roles_rolename) VALUES
                                                     (1, 'USER'), (2, 'USER'), (3, 'USER'),
                                                     (4, 'ADMIN'), (5, 'ADMIN'), (6, 'CHEF');

-- Voeg maaltijden toe (met uitgebreidere beschrijvingen)
INSERT INTO meals (name, meal_description, user_count, is_template, created_by_user_id, image_url, total_calories, total_protein, total_carbs, total_fat)
VALUES
    ('Breakfast',
     'Start your day with a nourishing breakfast featuring naturally sweet bananas, creamy nonfat milk, and wholesome whole-wheat bread. This energizing combination provides a perfect balance of carbohydrates, protein, and fiber to fuel your morning and keep you satisfied until your next meal.',
     1, true, 1, 'https://images.unsplash.com/photo-1473093295043-cdd812d0e601?q=80', 267, 13, 53, 2),

    ('Lunch',
     'Enjoy a colorful and vibrant lunch packed with nutrients and flavor. This meal brings together tender peas and carrots, hearty spinach, juicy strawberries, and creamy avocado for a delicious and refreshing midday boost. A perfect choice for those seeking a plant-forward, balanced plate.',
     1, true, 1, 'https://images.unsplash.com/photo-1485451456034-3f9391c6f769?q=80', 584, 25, 76, 37),

    ('Dinner',
     'Unwind with a comforting and hearty dinner that combines naturally sweet yellow corn, crisp cauliflower, and perfectly cooked hard-boiled eggs. This simple yet satisfying meal offers a rich blend of textures and nutrients, ideal for a lighter evening option that still nourishes and restores.',
     1, true, 1, 'https://images.unsplash.com/photo-1505932049984-db368d92fa63?q=80', 125, 8, 24, 4),

    ('Snack',
     'Recharge your energy with a delightful snack that pairs the refreshing crunch of raw apples with the rich, satisfying taste of raw almonds. Whether you’re on the go or enjoying a quick break, this simple duo provides a naturally sweet, fiber-rich, and protein-packed option to tide you over.',
     1, true, 2, 'https://plus.unsplash.com/premium_photo-1678481245533-3b5c7a5e3d37?q=80', 81, 6, 12, 3),

    ('Salad',
     'Treat yourself to a light and refreshing salad composed of crisp romaine lettuce, ripe tomatoes, and cool cucumbers. This vibrant mix is not only visually appealing but also packed with hydration, vitamins, and antioxidants, making it a perfect addition to any lunch or light dinner.',
     1, true, 2, 'https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?q=80', 200, 5, 30, 5),

    ('Dinner Special',
     'Indulge in a hearty dinner special featuring savory roasted chicken, fluffy white rice, and fresh broccoli florets. This balanced meal delivers a satisfying combination of lean protein, energizing carbohydrates, and essential nutrients, perfect for a fulfilling and wholesome end to your day.',
     1, true, 2, 'https://images.unsplash.com/photo-1605926637512-c8b131444a4b?q=80', 658, 22, 36, 48);


-- Meal types (ElementCollection)
INSERT INTO meal_meal_types (meal_id, meal_type) VALUES
                                                     (1, 'BREAKFAST'), (1, 'SNACK'),
                                                     (2, 'LUNCH'),
                                                     (3, 'DINNER'),
                                                     (4, 'SNACK'),
                                                     (5, 'LUNCH'),
                                                     (6, 'DINNER');

-- Cuisines (ElementCollection)
INSERT INTO meal_cuisines (meal_id, cuisine) VALUES
                                                 (1, 'AMERICAN'), (1, 'FRENCH'),
                                                 (2, 'ITALIAN'), (2, 'GREEK'),
                                                 (3, 'JAPANESE'), (3, 'KOREAN'),
                                                 (4, 'GREEK'),
                                                 (5, 'ITALIAN'), (5, 'SPANISH'),
                                                 (6, 'AMERICAN'), (6, 'MEXICAN');

-- Diets (ElementCollection)
INSERT INTO meal_diets (meal_id, diet) VALUES
                                           (1, 'VEGETARIAN'), (1, 'DAIRY_FREE'),
                                           (2, 'VEGETARIAN'), (2, 'LOW_CARB'),
                                           (3, 'PESCATARIAN'),
                                           (4, 'VEGAN'), (4, 'NUT_FREE'),
                                           (5, 'LOW_FAT'),
                                           (6, 'HIGH_PROTEIN'), (6, 'LOW_SODIUM');

-- Ingrediënten voor alle maaltijden
INSERT INTO meal_ingredients (meal_id, food_item_id, quantity, food_item_name) VALUES
                                                                                   (1, 4, 126, 'Bananas, raw'),
                                                                                   (1, 17, 245, 'Milk, nonfat...'),
                                                                                   (1, 15, 28.35, 'Bread, whole-wheat'),
                                                                                   (2, 1, 278, 'Peas and carrots'),
                                                                                   (2, 2, 284, 'Spinach'),
                                                                                   (2, 7, 200, 'Strawberries'),
                                                                                   (2, 11, 230, 'Avocados'),
                                                                                   (3, 3, 89, 'Corn'),
                                                                                   (3, 8, 100, 'Cauliflower'),
                                                                                   (3, 14, 25.5, 'Egg, hard-boiled'),
                                                                                   (4, 5, 182, 'Apple'),
                                                                                   (4, 6, 28, 'Almonds'),
                                                                                   (5, 13, 140, 'Chicken, roasted'),
                                                                                   (5, 14, 158, 'Rice, white'),
                                                                                   (5, 16, 91, 'Broccoli');

-- Koppel maaltijden aan gebruikers
INSERT INTO user_meals (user_id, meal_id) VALUES
                                              (1, 1), (1, 2), (1, 3),
                                              (2, 4), (2, 5), (2, 6);

-- Voeg testFoodItem toe
INSERT INTO food_items (name, fdc_id, portion_description, gram_weight, source)
VALUES ('TestFoodItem', 999999, '1 portie (200g)', 200, 'Albert Heijn');

-- Voeg voedingswaarden toe voor testFoodItem
INSERT INTO food_item_nutrients (food_item_id, nutrient_name, value, unit_name, nutrient_id) VALUES
                                                                                                 ((SELECT id FROM food_items WHERE fdc_id = 999999), 'Energy', 100, 'kcal', 1008),
                                                                                                 ((SELECT id FROM food_items WHERE fdc_id = 999999), 'Protein', 100, 'g', 1003),
                                                                                                 ((SELECT id FROM food_items WHERE fdc_id = 999999), 'Carbohydrates', 100, 'g', 1005),
                                                                                                 ((SELECT id FROM food_items WHERE fdc_id = 999999), 'Total lipid (fat)', 100, 'g', 1004);
