package fr.univavignon.courbes.stats;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import fr.univavignon.courbes.inter.stats.StatisticType;

/**
 * Modéle de donnée représentant un utilisateur et toute les statistiques qui lui sont liées à différentes dates.
 */
public class UserStats {
	
	/** Identifiant unique d'un joueur  **/
	private String userName; 
	/** Map liant une date unique à une autre map
	 *  liant un type de statistique à une donnée statistique entiére**/
	private Map<String, Map<StatisticType, Float>> mapDateStats;
	
	public UserStats(String uName) {
		userName = uName;
		mapDateStats = new HashMap<String, Map<StatisticType, Float>>();
	}
	
	public void getStats(String date) {
		mapDateStats.get(date);
	}
	
	public void setStats(String date, Map<StatisticType, Float> mapStats) {
		mapDateStats.put(date, mapStats);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public Map<String, Map<StatisticType, Float>> getMapDateStats() {
		return mapDateStats;
	}
}
