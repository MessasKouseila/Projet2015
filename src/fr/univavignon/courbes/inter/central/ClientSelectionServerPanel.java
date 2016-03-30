package fr.univavignon.courbes.inter.central;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.univavignon.courbes.inter.ClientConnectionHandler;
import fr.univavignon.courbes.inter.simpleimpl.MainWindow;
import fr.univavignon.courbes.inter.simpleimpl.SettingsManager;
import fr.univavignon.courbes.inter.simpleimpl.MainWindow.PanelName;
import fr.univavignon.courbes.network.ClientCommunication;
import fr.univavignon.courbes.network.simpleimpl.client.ClientCommunicationImpl;

public class ClientSelectionServerPanel extends JPanel implements ActionListener, ClientConnectionHandler{
	/**
	 * dvgg
	 * CrÃ©e un nouveau panel destinÃ© Ã  afficher .
	 * 
	 * @param mainWindow
	 * 		FenÃªtre principale contenant ce panel.
	 */
	public ClientSelectionServerPanel(MainWindow mainWindow)
	{	super();
		this.mainWindow = mainWindow;
		init();
	}
	
	/** FenÃªtre contenant ce panel */
	private MainWindow mainWindow;
	/** Bouton pour revenir au menu principal */
	private JButton backButton;
	/** Label affichant un message pendant l'attente d'un serveur **/
	private JLabel lblWaitServer;
	/**Thread de recherche d'un serveur*/
	Thread searchServer = new Thread(new Runnable() {
		@Override
		public void run() {
			int comptPts = 0;
			while(true){
				String server = mainWindow.serverCentralCom.joinServer();
				if(server !=  null){
						if(connect())
							searchServer.stop();
						else{
							mainWindow.serverCentralCom.serverRejected.put(server, "9999");
							System.out.println(mainWindow.serverCentralCom.getIpServer());
						}
				}else
				try {
					Thread.sleep(2000);
					if(comptPts < 3){
						lblWaitServer.setText(lblWaitServer.getText() + ".");
						comptPts++;
					} else {
						lblWaitServer.setText("Attente d'un serveur");
						comptPts = 0;
					}
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	});
	
	/**
	 * MÃ©thode principale d'initialisation du panel.
	 */
	private void init()
	{	BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
		initLabelPanel();
		add(Box.createVerticalGlue());
		initButtonsPanel();
		searchServer.start();
	}
	
	private void initButtonsPanel()
	{	JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
		panel.setLayout(layout);
		backButton = new JButton("Retour");
		backButton.addActionListener(this);
		panel.add(backButton);
		panel.add(Box.createHorizontalGlue());
		add(panel);
	}
	
	private void initLabelPanel(){
		JPanel panel = new JPanel();
		lblWaitServer = new JLabel("Attente d'un serveur");
		lblWaitServer.setFont(new Font(getName(), Font.PLAIN, 30));
		panel.add(Box.createVerticalStrut((int) (SettingsManager.getWindowHeight() * 0.9)));
		panel.add(lblWaitServer);
		add(panel);
	}
	
	private boolean connect()
	{	// on initialise le Moteur RÃ©seau
		ClientCommunication clientCom = new ClientCommunicationImpl();
		mainWindow.clientCom = clientCom;
		clientCom.setErrorHandler(mainWindow);
		clientCom.setConnectionHandler(this);
		
		String ipStr = mainWindow.serverCentralCom.getIpServer();
		clientCom.setIp(ipStr);
		SettingsManager.setLastServerIp(ipStr);
		int port = mainWindow.serverCentralCom.getPort();
		clientCom.setPort(port);
		SettingsManager.setLastServerPort(port);
		// puis on se connecte
		boolean result = clientCom.launchClient();
		System.out.println(result);
		return result;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==backButton){
			//mainWindow.displayPanel(PanelName.CLIENT_GAME_CONNECTION);
			mainWindow.displayPanel(PanelName.CLIENT_GAME_PLAYER_SELECTION);
			searchServer.stop();
		}
	}

	@Override
	public void gotRefused() {
		SwingUtilities.invokeLater(new Runnable()
		{	@Override
			public void run()
			{	JOptionPane.showMessageDialog(mainWindow, 
					"<html>Le serveur a rejetÃ© votre candidature, car il ne reste "
					+ "<br/>pas de place dans la partie en cours de configuration.</html>");
			}
	    });
		
	}

	@Override
	public void gotAccepted() {
		SwingUtilities.invokeLater(new Runnable()
		{	@Override
			public void run()
			{	mainWindow.clientCom.setConnectionHandler(null);
				if(!mainWindow.serverCentralCom.isGameDirect())
					mainWindow.serverCentralCom.playerServer();
				mainWindow.displayPanel(PanelName.CLIENT_GAME_WAIT);
			}
	    });
	}
	
	
}
