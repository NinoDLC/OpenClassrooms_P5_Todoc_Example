package fr.delcey.todoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.delcey.todoc.utils.Generated;

@Generated
public class Mock {

    private Mock() {
        // Utils only
    }

    private static final List<String> tasks = new ArrayList<String>() {{
        add("Nettoyer les vitres");
        add("Vider le lave-vaisselle");
        add("Passer l'aspirateur");
        add("Arroser les plantes");
        add("Nettoyer les toilettes");
        add("Jouer avec le chat");
        add("Préparer un diner romantique");
        add("Étendre le linge");
        add("Sortir le chien");
    }};

    public static String getRandomTaskDescription() {
        return tasks.get(new Random().nextInt(tasks.size()));
    }
}
