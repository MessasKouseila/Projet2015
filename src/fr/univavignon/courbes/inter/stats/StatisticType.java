package fr.univavignon.courbes.inter.stats;

/**
 * Définit tout les types de statistiques utilisés dans le jeu
 */
public enum StatisticType {
	  
	/** Nombres de points ELO **/
	ELO("Points ELO"),
	/** Classement entre tout les joueurs **/
	ClassementELO("Classement ELO"),
	/** Nombres de parties gagnées **/
	PartiesGagnees("Parties gagnées"),
	/** Nombres de parties perdues **/
	PartiesPerdues("Parties perdues"),
	/** Nombres de parties jouées **/
	PartiesJouees("Parties jouées"),
	/** Classement moyen obtenu à la fin des parties **/
	ClassementMoyen("Classement moyen");
	
	
    private final String display;
    private StatisticType(String s) {
        display = s;
    }
    @Override
    public String toString() {
        return display;
    }
}
