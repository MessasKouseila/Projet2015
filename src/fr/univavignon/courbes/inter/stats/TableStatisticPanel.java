package fr.univavignon.courbes.inter.stats;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.data.xy.XYDataset;

import fr.univavignon.courbes.inter.simpleimpl.MainWindow;
import fr.univavignon.courbes.inter.simpleimpl.SettingsManager;
import fr.univavignon.courbes.inter.simpleimpl.MainWindow.PanelName;

/**
 * Classe permettant l'affichage sur la fenêtre des statistiques de tout les joueurs, elle 
 * est représenté par un ensemble de deux parties principales, l'une étant un tableau général des
 * statistiques de tout les joueurs, et un deuxiéme a un ensemble de graphique affichant pour chaque 
 * statistique un graphique pour les joueurs selectionnées sur le premier tableau
 * @author 
 *
 */
public class TableStatisticPanel extends JPanel implements ActionListener {
	
	/** Numéro de série de la classe */
	private static final long serialVersionUID = 1L;

	public TableStatisticPanel(MainWindow mainWindow) {
		super();
		this.mainWindow = mainWindow;
		
		init();
	}
	
	/** Table affichée par ce panel */
	private JTable statsTable;
	/** Bouton pour revenir au menu principal */
	private JButton backButton;
	/** Bouton pour ajouter le nouveau profil */
	private JButton nextButton;
	/** Fenêtre contenant ce panel */
	private MainWindow mainWindow;
	/** Scrollpane contenu dans ce panel pour afficher la table */
	private JScrollPane scrollPane; 

	private void init()
	{	BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);
	
		
		initTablePanel();
		add(Box.createVerticalGlue());
		initButtonsPanel();
		initProfileList();
	}
	
	private void initProfileList() {
		// TODO Auto-generated method stub
		
	}

	public void initTablePanel() {
		statsTable = new JTable();
		statsTable.setAutoCreateRowSorter(true);
		statsTable.setModel(new ProfileStatsTableModel(mainWindow));

		scrollPane = new JScrollPane
		(	statsTable,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension frameDim = mainWindow.getPreferredSize();
		int boardHeight = SettingsManager.getBoardHeight();
		Dimension dim = new Dimension(frameDim.width,(int)(boardHeight*0.90));
		scrollPane.setPreferredSize(dim);
		scrollPane.setMaximumSize(dim);
		scrollPane.setMinimumSize(dim);
		add(scrollPane);
	}
	
	/**
	 * Initialisation des boutons contenus dans ce panel.
	 */
	private void initButtonsPanel()
	{	JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
		panel.setLayout(layout);
		
		backButton = new JButton("Retour");
		backButton.addActionListener(this);
		panel.add(backButton);
		
		panel.add(Box.createHorizontalGlue());
		
		nextButton = new JButton("Suivant");
		nextButton.addActionListener(this);
		panel.add(nextButton);
		
		add(panel);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == backButton) {
			mainWindow.displayPanel(PanelName.MAIN_MENU);
		}
		else if(e.getSource() == nextButton) {
			mainWindow.rowsChecked = ((ProfileStatsTableModel)statsTable.getModel()).getRowsChecked(); // Sauvegarde les données cochées
			mainWindow.displayPanel(PanelName.CHARTS_PROFILES);
		}
	}


}
