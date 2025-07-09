package balancebite.service.interfaces.meal;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.ClientLinkRequestDTO;
import balancebite.model.user.User;

import java.util.List;

public interface IDietitianService {

    /**
     * Laat een diëtist een cliënt uitnodigen op basis van e-mailadres.
     *
     * @param requestDTO Bevat het e-mailadres van de cliënt.
     * @param dietitian  De ingelogde gebruiker met de rol DIETITIAN.
     */
    void inviteClientByEmail(ClientLinkRequestDTO requestDTO, User dietitian);

    /**
     * Laat een diëtist een nieuwe maaltijd aanmaken, optioneel gedeeld met andere gebruikers of e-mails.
     *
     * @param mealInputDTO   De gegevens van de maaltijd.
     * @param dietitianId    De ID van de aanmakende diëtist.
     * @param sharedUserIds  Lijst met gebruikers-ID's waarmee gedeeld moet worden (optioneel).
     * @param sharedEmails   Lijst met e-mails waarmee gedeeld moet worden (optioneel).
     * @return De aangemaakte maaltijd als DTO.
     */
    MealDTO createMealAsDietitian(MealInputDTO mealInputDTO, Long dietitianId, List<Long> sharedUserIds, List<String> sharedEmails);

    /**
     * Laat een diëtist een nieuw dieetplan aanmaken, optioneel gedeeld met andere gebruikers of e-mails.
     *
     * @param input           De gegevens van het dieetplan.
     * @param dietitianId     De ID van de aanmakende diëtist.
     * @param sharedUserIds   Lijst met gebruikers-ID's waarmee gedeeld moet worden (optioneel).
     * @param sharedEmails    Lijst met e-mails waarmee gedeeld moet worden (optioneel).
     * @return Het aangemaakte dieetplan als DTO.
     */
    DietPlanDTO createDietPlanAsDietitian(DietPlanInputDTO input, Long dietitianId, List<Long> sharedUserIds, List<String> sharedEmails);

    /**
     * Voeg gedeelde toegang toe aan een bestaande private maaltijd.
     *
     * @param mealId        De ID van de maaltijd.
     * @param sharedUserIds Lijst met gebruikers-ID's waarmee gedeeld moet worden (optioneel).
     * @param sharedEmails  Lijst met e-mails waarmee gedeeld moet worden (optioneel).
     * @param dietitianId   De ID van de diëtist die eigenaar is van de maaltijd.
     */
    void addSharedAccessToMeal(Long mealId, List<Long> sharedUserIds, List<String> sharedEmails, Long dietitianId);

    /**
     * Voeg gedeelde toegang toe aan een bestaand private dieetplan.
     *
     * @param dietPlanId    De ID van het dieetplan.
     * @param sharedUserIds Lijst met gebruikers-ID's waarmee gedeeld moet worden (optioneel).
     * @param sharedEmails  Lijst met e-mails waarmee gedeeld moet worden (optioneel).
     * @param dietitianId   De ID van de diëtist die eigenaar is van het dieetplan.
     */
    void addSharedAccessToDietPlan(Long dietPlanId, List<Long> sharedUserIds, List<String> sharedEmails, Long dietitianId);

}
