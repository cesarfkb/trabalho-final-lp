package org.example;

import java.util.Locale;
import java.util.ResourceBundle;

public class Idioma {
    static String[] idiomas = { "Português", "Inglês", "Italiano", "Francês", "Espanhol" };
    static ResourceBundle[] idiomasBundles = {
            ResourceBundle.getBundle("arquivo", new Locale("pt", "BR")),
            ResourceBundle.getBundle("arquivo", Locale.US),
            ResourceBundle.getBundle("arquivo", Locale.ITALY),
            ResourceBundle.getBundle("arquivo", Locale.FRANCE),
            ResourceBundle.getBundle("arquivo", new Locale("es", "ES")),
    };
}
