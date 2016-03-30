package fr.univavignon.courbes.network.central;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import fr.univavignon.courbes.inter.stats.StatisticType;

public interface StatCentral {
	
	/**
	 * Pour toutes les dates de ces méthodes, le format de la date est le suivant
	 */
	public static final SimpleDateFormat dateFormatCentral = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Renvoit une map dont chaque clé est un nom utilisateur et la valeur
	 * liée a cette clé est une map représentant chaque statistique actuelle
	 */
	public Map<String, Map<StatisticType, Float>> allUsersActualsStats();
	
	
	/** retourne la plus vieille date d'une statistique pour cet utilisateur **/
	public String mostOlderStat(String username);
	/** Retourne la date de la stat la plus recente pour cet user **/
	public String mostRecentStat(String username);
	
	/** Récupére les statistiques à une date donnée pour un utilisateur **/
	public Map<StatisticType, Float> getStatsAt(String username ,String date);
	
	
	/**
	 * Envoie les resultats de la fin de partie au serveur central pour que celui ci
	 * puisse insérer les statistiques dans la base de données (Elo, parties gagnées..)
	 * @param gameResults Tableau a 2 dimensions contenant le résultat de partie
	 * ex : gr[1][1] : "icham"  gr[1][2] : "15"
	 * 	    gr[2][1] : "mikael"  gr[2][2] : "13"
	 *      gr[3][1] : "aghiles"  gr[3][2] : "10"
	 */     
	public void sendGameStatistics(String[][] gameResults);


}
