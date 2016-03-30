package fr.univavignon.courbes.network.central;

import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.text.Document;

import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.univavignon.courbes.common.Player;
import fr.univavignon.courbes.common.Profile;
import fr.univavignon.courbes.inter.ErrorHandler;

public class ServerCentralCommunication extends AbstractServerCommunication{
	
	//COnstrucuteur
	public ServerCentralCommunication(){
		super();
	}
	//COnstrucuteur paramétré
	public ServerCentralCommunication(ErrorHandler error){
		super(error);
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
	
	/**
	 * @param username est de type {@link String}
	 * @param password est de type {@link String}
	 * @return <code>true</code> si l'utilisateur est bien connécté , 
	 *         si non retourne <code>false</code> 
	 */
	public Player loginPlayer(String username,String password){
		Map<String, String> data = new HashMap<String, String>();
		data.put("username", username);data.put("password", password);
		try {
			Connection.Response request = sendRequest(data, "login");
			if(request.parse().select(".login").html().equals("true")){
				Profile profile = new Profile();
					profile.profileId = Integer.parseInt(request.parse().select(".idPlayer").html());
					profile.userName = request.parse().select(".username").html();
					profile.password = request.parse().select(".pass").html();
					profile.email = request.parse().select(".email").html();
					profile.eloRank = Integer.parseInt(request.parse().select(".elo").html());
					profile.country = request.parse().select(".country").html();
				setPlayer(new Player());
					getPlayer().profile = profile;
					getPlayer().local = false;
					
			}
			else{
				setPlayer(null);
				this.getErrorHandler().displayError("Le pseudo ou le mot de passe est incorrecte");
			}
		} catch (IOException e1) {
	        this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
	        setPlayer(null);
		}
		return this.getPlayer();
	}
	

	public boolean signUpServer(int playersNumber,int completed){
		Boolean bool;
		Map<String, String> data = new HashMap<String, String>();
		data.put("id",getIpServer());
		data.put("port",Integer.toString(this.getPort()));
		data.put("playernumber",Integer.toString(playersNumber));
		data.put("completed",Integer.toString(completed));
		
		try {
			Connection.Response request = sendRequest(data, "signUpServer");
			bool = true;
		} catch (IOException e1) {
	        this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			bool = false;
		}
		return bool;
	}
	
	public boolean deleteServer(){
		Boolean bool;
		Map<String, String> data = new HashMap<String, String>();
		data.put("ipServer", getIpServer());
		try {
			Connection.Response request = sendRequest(data, "deleteServer");
			bool = true;
		} catch (IOException e1) {
	        this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			bool = false;
		}
		return bool;
	}
	public String joinServer(){
		int compteur = 0;
		Connection.Response request;
		try {
			request = Jsoup.connect(this.getIp()+"?action=joinServer")
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			        .method(Connection.Method.GET)
			        .execute();
			if(request.parse().select(".waitting").html().equals("0")){
				Elements ipServers = request.parse().getElementsByClass("ipServer");
				for (Element serverIp : ipServers) {
					if(serverRejected.get(serverIp.html())== null){
						setIpServer(serverIp.html());
						break;
					}
					compteur++;
				}
				if(compteur == ipServers.size())
					setIpServer(null);
				
			}else
				setIpServer(null);
			
		} catch (IOException e1) {
			setIpServer(null);
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
		}
		return getIpServer();
	}
	
	public boolean playerServer(){
		Map<String, String> dataRequest = new HashMap<>();
		dataRequest.put("ipServer", getIpServer());
		dataRequest.put("username", getPlayer().profile.userName);
		Connection.Response request;
		try {
			request = Jsoup.connect(this.getIp()+"?action=playerServer")
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.data(dataRequest)
			        .method(Connection.Method.GET)
			        .execute();
			return true;
		} catch (IOException e1) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			return false;
		}
	}
	
	public boolean deletePlayerServer(){
		Map<String, String> dataRequest = new HashMap<>();
		dataRequest.put("ipServer", getIpServer());
		dataRequest.put("username", getPlayer().profile.userName);
		Connection.Response request;
		try {
			request = Jsoup.connect(this.getIp()+"?action=deletePlayerServer")
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.data(dataRequest)
			        .method(Connection.Method.GET)
			        .execute();
			return true;
		} catch (IOException e1) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			return false;
		}
	}
	
	
	
	public boolean createProfile(Profile profile){
		boolean b;
		Map<String, String> profileRequest = new HashMap<>();
		profileRequest.put("username", profile.userName);
		profileRequest.put("password", profile.password);
		profileRequest.put("country", profile.country);
		profileRequest.put("elo", Integer.toString(profile.eloRank));
		profileRequest.put("email", profile.email);
		
		Connection.Response request;
		try {
			request = Jsoup.connect(this.getIp()+"?action=createProfile")
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.data(profileRequest)
			        .method(Connection.Method.GET)
			        .execute();
			if(request.parse().select(".exist").html().equals("1"))
				b = true;
			else
				b = false;
			
			
		} catch (IOException e1) {
			b = false;
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
		}
		
		return b;
	}
	
	public boolean deletePlayer(String username){
		Map<String, String> dataRequest = new HashMap<>();
		dataRequest.put("profile", username);
		Connection.Response request;
		try {
			request = Jsoup.connect(this.getIp()+"?action=deleteProfile")
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.data(dataRequest)
			        .method(Connection.Method.GET)
			        .execute();
			if(request.parse().select(".delete").html().equals("1"))
				return true;
			else
				return false;
		} catch (IOException e1) {
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			return false;
		}
	}
	/*
	public TreeSet<Profile> getProfiles(){
		TreeSet<Profile> profiles = new TreeSet<Profile>();
		Connection.Response request;
		try {
			request = Jsoup.connect(this.getIp()+"?action=displayProfile")
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			        .method(Connection.Method.POST)
			        .execute();
				Elements tr = request.parse().select("tr");
				int profileId= 0;
				for(Element td : tr){
					Profile profile = new Profile();
					profile.profileId = profileId;
					profile.userName = td.select(".username").html();
					profile.country = td.select(".country").html();
					profile.eloRank = Integer.parseInt(td.select(".elo").html());
					profile.email = td.select(".email").html();
					profile.password = td.select(".password").html();
					profiles.add(profile);
					profileId++;
				}
		} catch (IOException e1) {
			e1.printStackTrace();
			profiles = null;
			this.getErrorHandler().displayError("un probléme est survenue lors de la connection au serveur");
			
		}
		return profiles;
	}*/
	
	public static void main(String[] args) {
		ServerCentralCommunication server = new ServerCentralCommunication();
		
		//server.
		//server.playerServer();
				//System.out.println(server.loginPlayer("aghiles", "aghiles"));
		//System.out.println(server.isConnected());
		//server.signUpServer(3);
		/*for(Profile p : server.getProfiles())
			System.out.println(p.userName);*/
		//server.setPassword("groupe09");
		
		
		Profile p = new  Profile();
		p.userName = "alaoui";p.password="hicham";p.country="orroc";p.email="dd";p.eloRank=5;
		System.out.println(server.createProfile(p));
	}
	@Override
	public Response sendRequest(String action) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}