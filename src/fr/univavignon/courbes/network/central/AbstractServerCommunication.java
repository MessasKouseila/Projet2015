package fr.univavignon.courbes.network.central;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Request;

import fr.univavignon.courbes.common.Player;
import fr.univavignon.courbes.inter.ErrorHandler;

public abstract class AbstractServerCommunication {
	private String ipServerCentral;
	private ErrorHandler errorHandler;
	public  int port = 9999;
	private Player player;
	private String ipServer;
	private boolean gamePublic = false;
	private boolean gameDirect = true;
	public Map<String, String> serverRejected = new HashMap<>();
	
	
	//COnstrucuteur
	public AbstractServerCommunication(){
		this.setIp("http://pedago02a.univ-avignon.fr/~uapv1600543/ProjetIL/");
	}
	//COnstrucuteur paramétré
	public AbstractServerCommunication(ErrorHandler error){
		setErrorHandler(error);
		this.setIp("http://pedago02a.univ-avignon.fr/~uapv1600543/ProjetIL/");
	}
		
	//getter et setter
	public String getIp() {
		return ipServerCentral;
	}
	public void setIp(String ip) {
		this.ipServerCentral = ip;
	}
	public int getPort() {
		return port;
	}
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public boolean isGamePublic() {
		return gamePublic;
	}
	public void setGamePublic(boolean gamePublic) {
		this.gamePublic = gamePublic;
	}
	public String getIpServerCentral() {
		return ipServerCentral;
	}
	public void setIpServerCentral(String ipServerCentral) {
		this.ipServerCentral = ipServerCentral;
	}
	public String getIpServer() {
		return ipServer;
	}
	public void setIpServer(String ipServer) {
		this.ipServer = ipServer;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ErrorHandler error) {
		this.errorHandler = error;
	}
	
	public boolean isGameDirect() {
		return gameDirect;
	}
	public void setGameDirect(boolean gameDirect) {
		this.gameDirect = gameDirect;
	}
	
	public abstract Connection.Response sendRequest(Map<String, String> data, String action) throws IOException;
	public abstract Connection.Response sendRequest( String action) throws IOException;
}
