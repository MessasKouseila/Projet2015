package fr.univavignon.courbes.inter.stats;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import fr.univavignon.courbes.inter.simpleimpl.MainWindow;


/**
 * Modéle de données lié à l'affichages de statistiques
 * pour chaque joueur
 */
public class ProfileStatsTableModel extends AbstractTableModel
{	/** Numéro de série de la classe */
	private static final long serialVersionUID = 1L;
	/** Numéro de la colonne ou se trouve la checkbox */
	private static final int COL_CB = 7;

	/**
	 * Initialise le modèle de table.
	 */
	public ProfileStatsTableModel(MainWindow mainWindow)
	{	rowdata = new ArrayList<List<Object>>();
		this.mainWindow = mainWindow;
	
		// On définit les titres des colonnes et la placement de chaque statistique
		columnNames = new String[8];
		columnNames[0] = "Nom utilisateur";
		columnNames[1] = StatisticType.ClassementELO.toString();
		columnNames[2] = StatisticType.ELO.toString();
		columnNames[3] = StatisticType.ClassementMoyen.toString();
		columnNames[4] = StatisticType.PartiesJouees.toString();
		columnNames[5] = StatisticType.PartiesGagnees.toString();
		columnNames[6] = StatisticType.PartiesPerdues.toString();
		columnNames[COL_CB] = "Affichage graphique";
	
		addProfilesStats(); // Ajout des données stats
	}

	/** Données */
	private List<List<Object>> rowdata;
	/** En-têtes */
	private String[] columnNames;
	/** Fenêtre contenant ce panel */
	private MainWindow mainWindow;

	/**
	 * Ajoute les données importés par le serveur central dans la table de données,
	 * chaque ligne représente un utilisateur et ses statistiques
	 */
	public void addProfilesStats() {
		// Récupére les statistiques actuelles des utiilsateurs
		Map<String, Map<StatisticType, Float>> mapUsersStats = mainWindow.serverCentralStat.allUsersActualsStats(); 
		if(mapUsersStats != null) {
			for(Map.Entry<String, Map<StatisticType, Float>> userEntry : mapUsersStats.entrySet()) {
				List<Object> rowUserStat = new ArrayList<Object>();
				String userName = userEntry.getKey();
				rowUserStat.add(userName); // Ajout du nom utilisateur dans la premiére colonne
				Map<StatisticType, Float> mapStats = userEntry.getValue();
				// Ajout de chaque stat dans l'ordre défini des colonnes et conversion en entier
				rowUserStat.add(Math.round(mapStats.get(StatisticType.ClassementELO)));
				rowUserStat.add(Math.round(mapStats.get(StatisticType.ELO)));			
				rowUserStat.add(Math.round(mapStats.get(StatisticType.ClassementMoyen)));
				rowUserStat.add(Math.round(mapStats.get(StatisticType.PartiesJouees)));
				rowUserStat.add(Math.round(mapStats.get(StatisticType.PartiesGagnees)));
				rowUserStat.add(Math.round(mapStats.get(StatisticType.PartiesPerdues)));
				rowUserStat.add(false); // Ajout de la CheckBox dans le derniére colonne
				rowdata.add(rowUserStat); // Ajoute une ligne dans la table de données
			}
		}
		else
			System.out.println("map usersStats null");
	}
	@Override
	public int getRowCount()
	{	return rowdata.size();
	}

	@Override
	public int getColumnCount()
	{	if (rowdata != null && rowdata.size() > 0)
		return rowdata.get(0).size();
	else
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{	return rowdata.get(rowIndex).get(columnIndex);
	}

	@Override
	public String getColumnName(int c)
	{	return columnNames[c];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
	        switch (columnIndex) {
	        case 0:
	            return String.class;
	        case COL_CB:
	            return Boolean.class;
	        default:
	        	return int.class;
	    }
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == COL_CB;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (aValue instanceof Boolean && column == COL_CB) { // Checkbox qui a changé de valeur
			List<Object> elem = new ArrayList<Object>(); 
			elem = rowdata.get(row);
			elem.set(COL_CB, (boolean)aValue);
			rowdata.set(row, elem);
			fireTableCellUpdated(row, column);
		}
	}

	/**
	 * @return Retourne une liste des données cochés sur la table
	 */
	public List<List<Object>> getRowsChecked() {
		List<List<Object>> data = new ArrayList<List<Object>>();
		for(List<Object> row : rowdata) {
			if((boolean)row.get(COL_CB) == true) {
				row.remove(COL_CB);
				data.add(row);
			}
		}
		return data;
	}

}
