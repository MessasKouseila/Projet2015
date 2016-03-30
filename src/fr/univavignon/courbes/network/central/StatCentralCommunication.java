package fr.univavignon.courbes.network.central;

import java.awt.datatransfer.FlavorListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.esotericsoftware.kryonet.Server;

import org.jsoup.Connection.Request;
import org.jsoup.Connection.Response;

import fr.univavignon.courbes.inter.ErrorHandler;
import fr.univavignon.courbes.inter.stats.StatisticType;
import fr.univavignon.courbes.stats.UserStats;

public class StatCentralCommunication extends AbstractServerCommunication implements StatCentral{
	
	
	/**
	 * Pour toutes les dates de ces méthodes, le format de la date
	 * sera le suivant : (dd-MM-YY)  ex: 16-01-94
	 */
	
	public StatCentralCommunication() {
		super();
	}
	
	public StatCentralCommunication(ErrorHandler error) {
		super(error);
	}
	/** retourne la plus vieille date d'une statistique pour cet
	 * utilisateur
	 **/
	
	@Override
	public String mostOlderStat(String username) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("username", username);
		try {
			Connection.Response request = sendRequest(data,"mostOlderStat");
			String date = request.parse().select(".date").html();
			if(date.equals("false"))
				return null;
			else
				return date;
			
		} catch (IOException e) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			e.printStackTrace();
			return null;
		}
	}
	/** Retourne la date de la stat la plus recente pour cet user **/
	@Override
	public String mostRecentStat(String username) {
		Map<String, String> data = new HashMap<String, String>();
		data.put("username", username);
		try {
			Connection.Response request = sendRequest(data,"mostRecentStat");
			String date = request.parse().select(".date").html();
			if(date.equals("false"))
				return null;
			else
				return date;
			
		} catch (IOException e) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			e.printStackTrace();
			return null;
		}
	}

	/** Récupére les statistiques a une date donnée
	 *  pour un utilisateur
	 **/
	@Override
	public Map<StatisticType, Float> getStatsAt(String username, String date) {
		Map<String, String> data = new HashMap<String, String>();
		Map<StatisticType, Float> statistic = new HashMap<>();
		data.put("username", username);data.put("date", date);
		try {
			Connection.Response request = sendRequest(data,"getStats");
			System.out.println(request.body());
			if(request.parse().select(".exist").html() == "false"){
				return null;
			}
			System.out.println(request.parse().select(".elo").html());
			statistic.put(StatisticType.ELO, Float.parseFloat(request.parse().select(".elo").html()));
			System.out.println();
			statistic.put(StatisticType.PartiesGagnees, Float.parseFloat(request.parse().select(".partieGagnee").html()));
			statistic.put(StatisticType.PartiesPerdues, Float.parseFloat(request.parse().select(".partiePerdu").html()));
			statistic.put(StatisticType.PartiesJouees, Float.parseFloat(request.parse().select(".partie").html()));
			statistic.put(StatisticType.ClassementMoyen, Float.parseFloat(request.parse().select(".classementM").html()));
			statistic.put(StatisticType.ClassementELO, Float.parseFloat(request.parse().select(".classementElo").html()));
			
		} catch (IOException e) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			e.printStackTrace();
			return null;
		}
		return statistic;
	}

	@Override
	public Connection.Response sendRequest(Map<String, String> data,String action) throws IOException{
		Connection.Response request = Jsoup.connect(this.getIp()+"?action="+action)
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				.data(data)
				.method(Connection.Method.GET)
		        .execute();
		return request;
	}
	

	@Override
	public Response sendRequest(String action) throws IOException {
		Connection.Response request = Jsoup.connect(this.getIp()+"?action="+action)
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				.method(Connection.Method.GET)
		        .execute();
		return request;
	}

	@Override
	public Map<String, Map<StatisticType, Float>> allUsersActualsStats() {
		Map<String, Map<StatisticType, Float>> stats = new HashMap<>();
		Map<StatisticType, Float> stat ;
		try {
			Connection.Response request = sendRequest("getUsersStats");
			
			Elements elements = request.parse().select("table").select("tr");
			String player = null;int c=0;
			for(Element tr : elements){
				player = tr.select(".player").html();
				stat = new HashMap<>();
				stat.put(StatisticType.ELO, Float.parseFloat(tr.select(".elo").html()));
				stat.put(StatisticType.PartiesGagnees, Float.parseFloat(tr.select(".gagnee").html()));
				stat.put(StatisticType.PartiesPerdues, Float.parseFloat(tr.select(".perdue").html()));
				stat.put(StatisticType.PartiesJouees, Float.parseFloat(tr.select(".nbrpartie").html()));
				stat.put(StatisticType.ClassementMoyen, Float.parseFloat(tr.select(".moyenne").html()));
				stat.put(StatisticType.ClassementELO, Float.parseFloat(tr.select(".classement").html()));
				Map<StatisticType,Float> q = stat;
				stats.put(player, q);
			}
			
		} catch (IOException e) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			e.printStackTrace();
			return null;
		}
		return stats;
	}
	

	@Override
	public void sendGameStatistics(String[][] gameResults) {
		int tailleTab = gameResults.length;
		Connection.Response request;
		switch (tailleTab) {
		case 2:
			try {
				request = Jsoup.connect(this.getIp()+"?action=insertStats")
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				.data("player1",gameResults[0][0]+" "+gameResults[0][1])
				.data("player2",gameResults[1][0]+" "+gameResults[1][1])
				.data("nbrPlayer",Integer.toString(tailleTab))
				.method(Connection.Method.GET)
				.execute();
				System.out.println(request.body());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;	
			
		case 3:
			try {
				request = Jsoup.connect(this.getIp()+"?action=insertStats")
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				.data("player1",gameResults[0][0]+" "+gameResults[0][1])
				.data("player2",gameResults[1][0]+" "+gameResults[1][1])
				.data("player3",gameResults[2][0]+" "+gameResults[2][1])
				.data("nbrPlayer",Integer.toString(tailleTab))
				.method(Connection.Method.GET)
				.execute();
				System.out.println(request.body());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			break;	
		case 4:
			
			try {
				request = Jsoup.connect(this.getIp()+"?action=insertStats")
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				.data("player1",gameResults[0][0]+" "+gameResults[0][1])
				.data("player2",gameResults[1][0]+" "+gameResults[1][1])
				.data("player3",gameResults[2][0]+" "+gameResults[2][1])
				.data("player4",gameResults[3][0]+" "+gameResults[3][1])
				.data("nbrPlayer",Integer.toString(tailleTab))
				.method(Connection.Method.GET)
				.execute();
				System.out.println(request.body());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			break;	
		case 5:
			try {
				request = Jsoup.connect(this.getIp()+"?action=insertStats")
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				
				.data("player1",gameResults[0][0]+" "+gameResults[0][1])
				.data("player2",gameResults[1][0]+" "+gameResults[1][1])
				.data("player3",gameResults[2][0]+" "+gameResults[2][1])
				.data("player4",gameResults[3][0]+" "+gameResults[3][1])
				.data("player5",gameResults[4][0]+" "+gameResults[4][1])
				.data("nbrPlayer",Integer.toString(tailleTab))
				.method(Connection.Method.GET)
				.execute();
				System.out.println(request.body());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 6:
			try {
				request = Jsoup.connect(this.getIp()+"?action=insertStats")
				.ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
				.data("player1",gameResults[0][0]+" "+gameResults[0][1])
				.data("player2",gameResults[1][0]+" "+gameResults[1][1])
				.data("player3",gameResults[2][0]+" "+gameResults[2][1])
				.data("player4",gameResults[3][0]+" "+gameResults[3][1])
				.data("player5",gameResults[4][0]+" "+gameResults[4][1])
				.data("player6",gameResults[5][0]+" "+gameResults[5][1])
				.data("nbrPlayer",Integer.toString(tailleTab))
				.method(Connection.Method.GET)
				.execute();
				System.out.println(request.body());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
	
		default:
			System.out.println("hfghf");
			break;
		}
	}

	

}
