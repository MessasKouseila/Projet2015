package fr.univavignon.courbes.inter.simpleimpl.local;

/*
 * Courbes
 * Copyright 2015-16 L3 Info UAPV 2015-16
 * 
 * This file is part of Courbes.
 * 
 * Courbes is free software: you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 2 of the License, or (at your option) any later version.
 * 
 * Courbes is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Courbes. If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import fr.univavignon.courbes.common.Constants;
import fr.univavignon.courbes.common.Player;
import fr.univavignon.courbes.common.Profile;
import fr.univavignon.courbes.inter.simpleimpl.MainWindow;
import fr.univavignon.courbes.inter.simpleimpl.profiles.ProfileManager;

/**
 * Panel représentant un joueur en cours de configuration.
 * Utilisé par un autre panel chargé de la configuration d'une partie.
 * 
 * @author	L3 Info UAPV 2015-16
 */
public class LocalPlayerConfigPanel extends JPanel implements ActionListener, KeyListener
{	
	//Manque de commantaire
	JTextField  tfUser=new JTextField();
	JPasswordField pfPassword=new JPasswordField();
	JCheckBox pfcheckbox=new JCheckBox();
	Object[] list={"password",pfPassword,"se souvenir",pfcheckbox};
//Manque de commantaire
	/** Numéro de série de la classe */
	private static final long serialVersionUID = 1L;
	/** Ensemble de touches prédéfinies */
	private static final int[][] PREDEFINED_KEYS = 
	{	{KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT},
		{KeyEvent.VK_Q,KeyEvent.VK_S},
		{KeyEvent.VK_A,KeyEvent.VK_Z},
		{KeyEvent.VK_W,KeyEvent.VK_X},
		{KeyEvent.VK_R,KeyEvent.VK_T},
		{KeyEvent.VK_F,KeyEvent.VK_G}
	};
	
	/**
	 * Crée un panel chargé de représenter un joueur et
	 * sa configuration.
	 * 
	 * @param configPanel
	 * 		Panel de configuration contenant ce panel. 
	 */
	public LocalPlayerConfigPanel(AbstractLocalPlayerSelectionPanel configPanel)
	{	this.configPanel = configPanel;
		
		initPlayer();
		initPanel();
	}

	/** Panel contenant ce panel */
	private AbstractLocalPlayerSelectionPanel configPanel;
	/** Bouton utilisé pour configurer la commande "aller à gauche" */
	private JButton leftButton;
	/** Bouton utilisé pour configurer la commande "aller à droite" */
	private JButton rightButton;
	/** Combobox utilisée pour sélectionner le profil */
	private JComboBox<Profile> playerSelectorCombo;
	/** Joueur sélectionné */
	public Player player;
	/** Profils disponibles */
	private Vector<Profile> availableProfiles;
	
	/**
	 * Initialise les composants contenus dans ce panel.
	 */
	private void initPanel()
	{	BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		setLayout(layout);
		int height = 30;
		Dimension dim;

		playerSelectorCombo = new JComboBox<Profile>(availableProfiles);
		playerSelectorCombo.setSelectedItem(player.profile);
		playerSelectorCombo.addActionListener(this);
		dim = new Dimension(configPanel.nameWidth,height);
		playerSelectorCombo.setPreferredSize(dim);
		playerSelectorCombo.setMaximumSize(dim);
		playerSelectorCombo.setMinimumSize(dim);
		playerSelectorCombo.setBackground(Constants.PLAYER_COLORS[player.playerId]);
		add(playerSelectorCombo);
		
		add(Box.createHorizontalGlue());
		
		String leftText = KeyEvent.getKeyText(player.leftKey);
		leftButton = new JButton(leftText);
		leftButton.addActionListener(this);
		leftButton.addKeyListener(this);
		dim = new Dimension(configPanel.keyWidth,height);
		leftButton.setPreferredSize(dim);
		leftButton.setMaximumSize(dim);
		leftButton.setMinimumSize(dim);
		leftButton.setBackground(Constants.PLAYER_COLORS[player.playerId]);
		add(leftButton);
		
		add(Box.createHorizontalGlue());
		
		String rightText = KeyEvent.getKeyText(player.rightKey);
		rightButton = new JButton(rightText);
		rightButton.addActionListener(this);
		rightButton.addKeyListener(this);
		dim = new Dimension(configPanel.keyWidth,height);
		rightButton.setPreferredSize(dim);
		rightButton.setMaximumSize(dim);
		rightButton.setMinimumSize(dim);
		rightButton.setBackground(Constants.PLAYER_COLORS[player.playerId]);
		add(this.rightButton);
	}

	/**
	 * Initialise le joueur courant.
	 */
	private void initPlayer()
	{	player = new Player();
		
		availableProfiles = new Vector<Profile>(ProfileManager.getProfiles());
		int index = configPanel.selectedProfiles.size();
		if(!configPanel.mainWindow.serverCentralCom.isGamePublic() || !configPanel.mainWindow.serverCentralCom.isGameDirect())
			player.profile = availableProfiles.get(Math.min(index,availableProfiles.size()-1));
		
		player.playerId = index;
		
		player.local = true;
		
		player.totalScore = 0;
		player.roundScore = 0;
		
		player.leftKey = PREDEFINED_KEYS[index][0];
		player.rightKey = PREDEFINED_KEYS[index][1];
	}
	
	/**
	 * Enlève la couleur assignée aux composants de ce panel.
	 */
	public void removeColor()
	{	rightButton.setBackground(null);
		leftButton.setBackground(null);
		playerSelectorCombo.setBackground(null);

	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{	if(e.getSource()==playerSelectorCombo)
		{	Profile profile = (Profile)playerSelectorCombo.getSelectedItem();
			
			player.profile = profile;
			if(configPanel.mainWindow.serverCentralCom.isGamePublic() || configPanel.mainWindow.serverCentralCom.isGameDirect()){
				String password = ProfileManager.containsUserNamePassword(profile.userName);
				if(password.equals("null")){
					if(0==JOptionPane.showOptionDialog(null, list, "login", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Se connecter", "Annuler"},null)){
						Player connexion = configPanel.mainWindow.serverCentralCom.loginPlayer(profile.userName, pfPassword.getText());
							if(connexion != null){
								playerSelectorCombo.setSelectedItem(playerSelectorCombo.getSelectedItem());
								if(pfcheckbox.isSelected())
									ProfileManager.editProfiles(profile.userName, pfPassword.getText(),connexion.profile.eloRank);
								else
									ProfileManager.editProfiles(profile.userName, "null",connexion.profile.eloRank);
							}
							else{
								playerSelectorCombo.setSelectedItem(null);
							}
					
						
					}else{
						playerSelectorCombo.setSelectedItem(null);
					}
				}else{
					Player connexion = configPanel.mainWindow.serverCentralCom.loginPlayer(profile.userName, password);
					if(connexion != null){
						playerSelectorCombo.setSelectedItem(playerSelectorCombo.getSelectedItem());
						ProfileManager.editProfiles(profile.userName, "null",connexion.profile.eloRank);
					}else{
						playerSelectorCombo.setSelectedItem(null);
						if(0==JOptionPane.showOptionDialog(null, list, "login", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Se connecter", "Annuler"},null)){
							connexion = configPanel.mainWindow.serverCentralCom.loginPlayer(profile.userName, pfPassword.getText());
								if(connexion != null){
									playerSelectorCombo.setSelectedItem(playerSelectorCombo.getSelectedItem());
									if(pfcheckbox.isSelected())
										ProfileManager.editProfiles(profile.userName, pfPassword.getText(),connexion.profile.eloRank);
									else
										ProfileManager.editProfiles(profile.userName, "null",connexion.profile.eloRank);
								}
								else{
									playerSelectorCombo.setSelectedItem(null);
								}
						
							
						}else{
							playerSelectorCombo.setSelectedItem(null);
						}
						
					}
				}
				/*	
				
				
				
				
				if(0==JOptionPane.showOptionDialog(null, list, "login", JOptionPane.OK_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,new String[]{"Se connecter", "Annuler"},null)){
					Player connexion = configPanel.mainWindow.serverCentralCom.loginPlayer(profile.userName, pfPassword.getText());
						if(connexion != null){
							playerSelectorCombo.setSelectedItem(playerSelectorCombo.getSelectedItem());
							if(pfcheckbox.isSelected())
								ProfileManager.editProfiles(profile.userName, pfPassword.getText());
						}
						else{
							playerSelectorCombo.setSelectedItem(null);
						}
				
					
				}else{
					playerSelectorCombo.setSelectedItem(null);
				}*/
				
				
			}
		}
	
		else if(e.getSource()==leftButton)
		{	leftButton.setText("?");
		}
		else if(e.getSource()==rightButton)
		{	rightButton.setText("?");
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{	//
	}

	@Override
	public void keyPressed(KeyEvent e)
	{	//
	}

	@Override
	public void keyReleased(KeyEvent e)
	{	int keyCode = e.getKeyCode();
		String keyText = KeyEvent.getKeyText(keyCode);
		
		if(e.getSource()==leftButton)
		{	leftButton.setText(keyText);
			player.leftKey = keyCode;
		}
		else if(e.getSource()==rightButton)
		{	rightButton.setText(keyText);
			player.rightKey = keyCode;
		}
	}
}
