//package balancebite.utils;
//
//import balancebite.dto.NutrientInfoDTO;
//import balancebite.model.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class NutrientCalculator {
//
//    public static Map<String, NutrientInfoDTO> calculateNutrients(Meal meal) {
//        Map<String, NutrientInfoDTO> totalNutrients = new HashMap<>();
//        Macronutrients macronutrients = new Macronutrients();
//        VitaminsAndMinerals vitaminsAndMinerals = new VitaminsAndMinerals();
//
//        for (MealIngredient ingredient : meal.getMealIngredients()) {
//            FoodItem foodItem = ingredient.getFoodItem();
//            System.out.println("Calculating nutrients for FoodItem: " + foodItem.getName() + ", Quantity: " + ingredient.getQuantity());
//
//            for (NutrientInfo nutrient : foodItem.getNutrients()) {
//                double nutrientValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);
//
//                if (nutrientValue > 0) {
//                    String key = nutrient.getNutrientName() + " (" + nutrient.getUnitName() + ")";
//                    totalNutrients.computeIfAbsent(key, k -> new NutrientInfoDTO(nutrient.getNutrientName(), 0.0, nutrient.getUnitName()))
//                            .setValue(totalNutrients.get(key).getValue() + nutrientValue);
//
//// Verdeel de nutriënten tussen macronutriënten en vitamines/mineralen
//                    switch (nutrient.getNutrientName().toLowerCase()) {
//                        // Macronutriënten
//                        case "energy":
//                            if (nutrient.getUnitName().equalsIgnoreCase("kcal")) {
//                                macronutrients.setKcals(macronutrients.getKcals() + nutrientValue);
//                            } else if (nutrient.getUnitName().equalsIgnoreCase("kj")) {
//                                macronutrients.setKcals(macronutrients.getKcals() + nutrientValue / 4.184);
//                            }
//                            break;
//                        case "protein":
//                            macronutrients.setProteins(macronutrients.getProteins() + nutrientValue);
//                            break;
//                        case "total lipid (fat)":
//                            macronutrients.setFats(macronutrients.getFats() + nutrientValue);
//                            break;
//                        case "carbohydrate, by difference":
//                            macronutrients.setCarbohydrates(macronutrients.getCarbohydrates() + nutrientValue);
//                            break;
//
//                        // Vitaminen
//                        case "vitamin a, rae":
//                            vitaminsAndMinerals.setVitaminA(vitaminsAndMinerals.getVitaminA() + nutrientValue);
//                            break;
//                        case "vitamin c, total ascorbic acid":
//                            vitaminsAndMinerals.setVitaminC(vitaminsAndMinerals.getVitaminC() + nutrientValue);
//                            break;
//                        case "vitamin d (d2 + d3)":
//                            vitaminsAndMinerals.setVitaminD(vitaminsAndMinerals.getVitaminD() + nutrientValue);
//                            break;
//                        case "vitamin e (alpha-tocopherol)":
//                            vitaminsAndMinerals.setVitaminE(vitaminsAndMinerals.getVitaminE() + nutrientValue);
//                            break;
//                        case "vitamin k (phylloquinone)":
//                            vitaminsAndMinerals.setVitaminK(vitaminsAndMinerals.getVitaminK() + nutrientValue);
//                            break;
//                        case "thiamin":
//                            vitaminsAndMinerals.setThiamin(vitaminsAndMinerals.getThiamin() + nutrientValue);
//                            break;
//                        case "riboflavin":
//                            vitaminsAndMinerals.setRiboflavin(vitaminsAndMinerals.getRiboflavin() + nutrientValue);
//                            break;
//                        case "niacin":
//                            vitaminsAndMinerals.setNiacin(vitaminsAndMinerals.getNiacin() + nutrientValue);
//                            break;
//                        case "vitamin b-6":
//                            vitaminsAndMinerals.setVitaminB6(vitaminsAndMinerals.getVitaminB6() + nutrientValue);
//                            break;
//                        case "folate, total":
//                            vitaminsAndMinerals.setFolate(vitaminsAndMinerals.getFolate() + nutrientValue);
//                            break;
//                        case "vitamin b-12":
//                            vitaminsAndMinerals.setVitaminB12(vitaminsAndMinerals.getVitaminB12() + nutrientValue);
//                            break;
//                        case "pantothenic acid":
//                            vitaminsAndMinerals.setPantothenicAcid(vitaminsAndMinerals.getPantothenicAcid() + nutrientValue);
//                            break;
//                        case "biotin":
//                            vitaminsAndMinerals.setBiotin(vitaminsAndMinerals.getBiotin() + nutrientValue);
//                            break;
//                        case "choline, total":
//                            vitaminsAndMinerals.setCholine(vitaminsAndMinerals.getCholine() + nutrientValue);
//                            break;
//
//                        // Mineralen
//                        case "calcium, ca":
//                            vitaminsAndMinerals.setCalcium(vitaminsAndMinerals.getCalcium() + nutrientValue);
//                            break;
//                        case "iron, fe":
//                            vitaminsAndMinerals.setIron(vitaminsAndMinerals.getIron() + nutrientValue);
//                            break;
//                        case "magnesium, mg":
//                            vitaminsAndMinerals.setMagnesium(vitaminsAndMinerals.getMagnesium() + nutrientValue);
//                            break;
//                        case "phosphorus, p":
//                            vitaminsAndMinerals.setPhosphorus(vitaminsAndMinerals.getPhosphorus() + nutrientValue);
//                            break;
//                        case "potassium, k":
//                            vitaminsAndMinerals.setPotassium(vitaminsAndMinerals.getPotassium() + nutrientValue);
//                            break;
//                        case "sodium, na":
//                            vitaminsAndMinerals.setSodium(vitaminsAndMinerals.getSodium() + nutrientValue);
//                            break;
//                        case "zinc, zn":
//                            vitaminsAndMinerals.setZinc(vitaminsAndMinerals.getZinc() + nutrientValue);
//                            break;
//                        case "copper, cu":
//                            vitaminsAndMinerals.setCopper(vitaminsAndMinerals.getCopper() + nutrientValue);
//                            break;
//                        case "manganese, mn":
//                            vitaminsAndMinerals.setManganese(vitaminsAndMinerals.getManganese() + nutrientValue);
//                            break;
//                        case "selenium, se":
//                            vitaminsAndMinerals.setSelenium(vitaminsAndMinerals.getSelenium() + nutrientValue);
//                            break;
//                        case "fluoride, f":
//                            vitaminsAndMinerals.setFluoride(vitaminsAndMinerals.getFluoride() + nutrientValue);
//                            break;
//                        case "chromium, cr":
//                            vitaminsAndMinerals.setChromium(vitaminsAndMinerals.getChromium() + nutrientValue);
//                            break;
//                        case "iodine, i":
//                            vitaminsAndMinerals.setIodine(vitaminsAndMinerals.getIodine() + nutrientValue);
//                            break;
//                        case "molybdenum, mo":
//                            vitaminsAndMinerals.setMolybdenum(vitaminsAndMinerals.getMolybdenum() + nutrientValue);
//                            break;
//
//                        default:
//                            // Andere voedingsstoffen negeren we voor nu
//                            break;
//                    }
//
//                    System.out.println("Updated total for " + key + ": " + totalNutrients.get(key).getValue());
//                }
//            }
//        }
//
//        // Voeg macronutriënten toe aan de totale nutriëntenkaart
//        totalNutrients.put("Proteins (g)", new NutrientInfoDTO("Proteins", macronutrients.getProteins(), "g"));
//        totalNutrients.put("Carbohydrates (g)", new NutrientInfoDTO("Carbohydrates", macronutrients.getCarbohydrates(), "g"));
//        totalNutrients.put("Fats (g)", new NutrientInfoDTO("Fats", macronutrients.getFats(), "g"));
//        totalNutrients.put("Energy (kcal)", new NutrientInfoDTO("Energy", macronutrients.getKcals(), "kcal"));
//
//        // Voeg vitaminen en mineralen toe aan de totale nutriëntenkaart
//        totalNutrients.put("Vitamin A (µg)", new NutrientInfoDTO("Vitamin A", vitaminsAndMinerals.getVitaminA(), "µg"));
//        totalNutrients.put("Vitamin C (mg)", new NutrientInfoDTO("Vitamin C", vitaminsAndMinerals.getVitaminC(), "mg"));
//        totalNutrients.put("Vitamin D (IU)", new NutrientInfoDTO("Vitamin D", vitaminsAndMinerals.getVitaminD(), "IU"));
//        totalNutrients.put("Vitamin E (mg)", new NutrientInfoDTO("Vitamin E", vitaminsAndMinerals.getVitaminE(), "mg"));
//        totalNutrients.put("Vitamin K (µg)", new NutrientInfoDTO("Vitamin K", vitaminsAndMinerals.getVitaminK(), "µg"));
//        totalNutrients.put("Thiamin (mg)", new NutrientInfoDTO("Thiamin", vitaminsAndMinerals.getThiamin(), "mg"));
//        totalNutrients.put("Riboflavin (mg)", new NutrientInfoDTO("Riboflavin", vitaminsAndMinerals.getRiboflavin(), "mg"));
//        totalNutrients.put("Niacin (mg)", new NutrientInfoDTO("Niacin", vitaminsAndMinerals.getNiacin(), "mg"));
//        totalNutrients.put("Vitamin B6 (mg)", new NutrientInfoDTO("Vitamin B6", vitaminsAndMinerals.getVitaminB6(), "mg"));
//        totalNutrients.put("Folate (µg)", new NutrientInfoDTO("Folate", vitaminsAndMinerals.getFolate(), "µg"));
//        totalNutrients.put("Vitamin B12 (µg)", new NutrientInfoDTO("Vitamin B12", vitaminsAndMinerals.getVitaminB12(), "µg"));
//        totalNutrients.put("Pantothenic Acid (mg)", new NutrientInfoDTO("Pantothenic Acid", vitaminsAndMinerals.getPantothenicAcid(), "mg"));
//        totalNutrients.put("Biotin (µg)", new NutrientInfoDTO("Biotin", vitaminsAndMinerals.getBiotin(), "µg"));
//        totalNutrients.put("Choline (mg)", new NutrientInfoDTO("Choline", vitaminsAndMinerals.getCholine(), "mg"));
//
//        totalNutrients.put("Calcium (mg)", new NutrientInfoDTO("Calcium", vitaminsAndMinerals.getCalcium(), "mg"));
//        totalNutrients.put("Iron (mg)", new NutrientInfoDTO("Iron", vitaminsAndMinerals.getIron(), "mg"));
//        totalNutrients.put("Magnesium (mg)", new NutrientInfoDTO("Magnesium", vitaminsAndMinerals.getMagnesium(), "mg"));
//        totalNutrients.put("Phosphorus (mg)", new NutrientInfoDTO("Phosphorus", vitaminsAndMinerals.getPhosphorus(), "mg"));
//        totalNutrients.put("Potassium (mg)", new NutrientInfoDTO("Potassium", vitaminsAndMinerals.getPotassium(), "mg"));
//        totalNutrients.put("Sodium (mg)", new NutrientInfoDTO("Sodium", vitaminsAndMinerals.getSodium(), "mg"));
//        totalNutrients.put("Zinc (mg)", new NutrientInfoDTO("Zinc", vitaminsAndMinerals.getZinc(), "mg"));
//        totalNutrients.put("Copper (mg)", new NutrientInfoDTO("Copper", vitaminsAndMinerals.getCopper(), "mg"));
//        totalNutrients.put("Manganese (mg)", new NutrientInfoDTO("Manganese", vitaminsAndMinerals.getManganese(), "mg"));
//        totalNutrients.put("Selenium (µg)", new NutrientInfoDTO("Selenium", vitaminsAndMinerals.getSelenium(), "µg"));
//        totalNutrients.put("Fluoride (mg)", new NutrientInfoDTO("Fluoride", vitaminsAndMinerals.getFluoride(), "mg"));
//        totalNutrients.put("Chromium (µg)", new NutrientInfoDTO("Chromium", vitaminsAndMinerals.getChromium(), "µg"));
//        totalNutrients.put("Iodine (µg)", new NutrientInfoDTO("Iodine", vitaminsAndMinerals.getIodine(), "µg"));
//        totalNutrients.put("Molybdenum (µg)", new NutrientInfoDTO("Molybdenum", vitaminsAndMinerals.getMolybdenum(), "µg"));
//
//        // Log de uiteindelijke macronutriënten
//        System.out.println("Final Macronutrient Totals:");
//        System.out.println("Total Proteins: " + macronutrients.getProteins() + " g");
//        System.out.println("Total Carbohydrates: " + macronutrients.getCarbohydrates() + " g");
//        System.out.println("Total Fats: " + macronutrients.getFats() + " g");
//        System.out.println("Total Energy: " + macronutrients.getKcals() + " kcal");
//
//
//        // Log de uiteindelijke vitaminen en mineralen
//        System.out.println("Final Vitamins and Minerals Totals:");
//        System.out.println("Vitamin A: " + vitaminsAndMinerals.getVitaminA() + " µg");
//        System.out.println("Vitamin C: " + vitaminsAndMinerals.getVitaminC() + " mg");
//        System.out.println("Vitamin D: " + vitaminsAndMinerals.getVitaminD() + " IU");
//        System.out.println("Vitamin E: " + vitaminsAndMinerals.getVitaminE() + " mg");
//        System.out.println("Vitamin K: " + vitaminsAndMinerals.getVitaminK() + " µg");
//        System.out.println("Thiamin: " + vitaminsAndMinerals.getThiamin() + " mg");
//        System.out.println("Riboflavin: " + vitaminsAndMinerals.getRiboflavin() + " mg");
//        System.out.println("Niacin: " + vitaminsAndMinerals.getNiacin() + " mg");
//        System.out.println("Vitamin B6: " + vitaminsAndMinerals.getVitaminB6() + " mg");
//        System.out.println("Folate: " + vitaminsAndMinerals.getFolate() + " µg");
//        System.out.println("Vitamin B12: " + vitaminsAndMinerals.getVitaminB12() + " µg");
//        System.out.println("Pantothenic Acid: " + vitaminsAndMinerals.getPantothenicAcid() + " mg");
//        System.out.println("Biotin: " + vitaminsAndMinerals.getBiotin() + " µg");
//        System.out.println("Choline: " + vitaminsAndMinerals.getCholine() + " mg");
//
//        System.out.println("Calcium: " + vitaminsAndMinerals.getCalcium() + " mg");
//        System.out.println("Iron: " + vitaminsAndMinerals.getIron() + " mg");
//        System.out.println("Magnesium: " + vitaminsAndMinerals.getMagnesium() + " mg");
//        System.out.println("Phosphorus: " + vitaminsAndMinerals.getPhosphorus() + " mg");
//        System.out.println("Potassium: " + vitaminsAndMinerals.getPotassium() + " mg");
//        System.out.println("Sodium: " + vitaminsAndMinerals.getSodium() + " mg");
//        System.out.println("Zinc: " + vitaminsAndMinerals.getZinc() + " mg");
//        System.out.println("Copper: " + vitaminsAndMinerals.getCopper() + " mg");
//        System.out.println("Manganese: " + vitaminsAndMinerals.getManganese() + " mg");
//        System.out.println("Selenium: " + vitaminsAndMinerals.getSelenium() + " µg");
//        System.out.println("Fluoride: " + vitaminsAndMinerals.getFluoride() + " mg");
//        System.out.println("Chromium: " + vitaminsAndMinerals.getChromium() + " µg");
//        System.out.println("Iodine: " + vitaminsAndMinerals.getIodine() + " µg");
//        System.out.println("Molybdenum: " + vitaminsAndMinerals.getMolybdenum() + " µg");
//
//        return totalNutrients;
//    }
//}
