package fr.univavignon.courbes.inter.stats;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import fr.univavignon.courbes.inter.simpleimpl.MainWindow;
import fr.univavignon.courbes.inter.simpleimpl.SettingsManager;
import fr.univavignon.courbes.network.central.StatCentral;
import fr.univavignon.courbes.stats.UserStats;
import fr.univavignon.courbes.inter.simpleimpl.MainWindow.PanelName;

/**
 * Panel présentant les statistiques de chaque joueur sélectionné présenté en graphiques, cette classe
 * gére aussi le chargement des données de chaque utilisateur selectionné pour différentes dates et gére
 * son affichage dans un graphique
 */
public class ChartProfilesPanel extends JPanel implements ActionListener{

	/** Numéro de série de la classe */
	private static final long serialVersionUID = 1L;
	/** Panel contenant les graphiques de statistiques **/
	private final ChartPanel chartPanel;
	/** Format de date utilisé **/
	private final SimpleDateFormat dateGraphFormat = new SimpleDateFormat("dd-MM-YY");	

	/**
	 * @param mainWindow
	 */
	public ChartProfilesPanel(MainWindow mainWindow) {
		super();
		this.mainWindow = mainWindow;
		chartPanel = new ChartPanel(null);
		listUS = new ArrayList<UserStats>();
		init();
	}


	/** Bouton pour retourner aux statistiques des profils **/
	private JButton backButton;
	/** ComboBox servant à choisir le type de statistique à afficher sur le diagramme **/
	private JComboBox<StatisticType> cbStatType;
	/** Fenêtre contenant ce panel */
	private MainWindow mainWindow;
	/** Liste de données des utilisateurs selectionnées, permet de n'avoir a récuperer les données statistiques 
	 * qu'une seule fois depuis le serveur**/
	private List<UserStats> listUS;

	private void init() {
		BoxLayout layout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(layout);

		initChartsPanel();

		add(Box.createVerticalGlue());
		initComboBoxPanel();

		add(Box.createVerticalGlue());
		initButtonsPanel();	

		loadUserStats();
		
		// Initialise le graphique pour la premiére fois
		loadChart((StatisticType)cbStatType.getSelectedItem());
	}

	private void initChartsPanel() {
		Dimension frameDim = mainWindow.getPreferredSize();
		int boardHeight = SettingsManager.getBoardHeight();
		Dimension dim = new Dimension(frameDim.width,(int)(boardHeight*0.88));
		chartPanel.setPreferredSize(dim);
		chartPanel.setMaximumSize(dim);
		chartPanel.setMinimumSize(dim);

		add(chartPanel);
	}

	private void initComboBoxPanel() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
		panel.setLayout(layout);

		panel.add(Box.createHorizontalGlue());

		cbStatType = new JComboBox<StatisticType>();
		cbStatType.setModel(new DefaultComboBoxModel<StatisticType>(StatisticType.values()));
		cbStatType.addActionListener(this);
		panel.add(cbStatType);

		panel.add(Box.createHorizontalGlue());

		add(panel);
	}

	private void initButtonsPanel() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.LINE_AXIS);
		panel.setLayout(layout);

		backButton = new JButton("Retour");
		backButton.addActionListener(this);
		panel.add(backButton);

		panel.add(Box.createHorizontalGlue());

		add(panel);
	}

	public void loadChart(StatisticType statType) {
		// Charger le dataset
		TimeSeriesCollection dataset = loadStatisticDataset(statType);
		// Initialise le graphique
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				statType.toString() + " en fonction du temps",      // Nom graphique
				"Temps",                     					   // label axe x
				statType.toString(),                               // label axe y
				dataset,               							   // données
				true,                  							   // inclure legende
				true,                  							   // tooltips
				false                  							   // urls
				);
		XYPlot plot = chart.getXYPlot();
		DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
		dateAxis.setDateFormatOverride(dateGraphFormat); // Format de date sur l'axe x
		NumberAxis valAxis = (NumberAxis)plot.getRangeAxis();
		if(statType == StatisticType.ClassementELO || statType == StatisticType.ClassementMoyen)
			valAxis.setInverted(true);
		valAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Valeurs entiéres sur l'axe y
		chartPanel.setChart(chart);
	}

	/**
	 * Charge une collection de données statistiques pour la statistique demandée de tout les joueurs selectionnés 
	 * pour être affiché sur le graphique, chaque donnée statistique est lié à une date
	 * @param statType Type de statistique à charger dans les données du graphique
	 * @return Une collection de données sur 2 axes
	 */
	public TimeSeriesCollection loadStatisticDataset(StatisticType statType) {
		final TimeSeriesCollection  dataset = new TimeSeriesCollection();

		for(UserStats user : listUS) {
			TimeSeries ts = new TimeSeries(user.getUserName());
			Calendar dateCal = new GregorianCalendar();
			for(Map.Entry<String, Map<StatisticType, Float>> statsEntry : user.getMapDateStats().entrySet()) {
				String dateEntry = statsEntry.getKey();
				try {
					dateCal.setTime(StatCentral.dateFormatCentral.parse(dateEntry));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Map<StatisticType, Float> mapStats = statsEntry.getValue();
				int statNeeded = Math.round(mapStats.get(statType));
				Day dayStat = new Day(dateCal.get(Calendar.DAY_OF_MONTH)
						,dateCal.get(Calendar.MONTH)+1  // Calendar.Month begin at index 0
						,dateCal.get(Calendar.YEAR));
				ts.addOrUpdate(dayStat, statNeeded);
			}
			dataset.addSeries(ts);
		}
		return dataset;
	}

	/**
	 * Charge dans chaque objet les statistiques de chaque user pour différentes dates
	 */
	public void loadUserStats() {
		for(List<Object> row : mainWindow.rowsChecked) {
			UserStats us = new UserStats((String)row.get(0));
			String strOldestStat  = mainWindow.serverCentralStat.mostOlderStat(us.getUserName());
			if(strOldestStat != null) {
				Calendar oldestStat = new GregorianCalendar(); // Date de la plus ancienne statistique enregistrée pour ce joueur
				try {
					oldestStat.setTime(StatCentral.dateFormatCentral.parse(strOldestStat));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Calendar today = new GregorianCalendar();
				List<Calendar> listDates = DateHelper.dateInInterval(oldestStat, today, 8); // Recupére une liste de différentes dates entre ces deux dates
				// Ajout de la plus ancienne date et de la date d'aujourd'hui
				listDates.add(oldestStat);
				boolean sameDay = (today.get(Calendar.YEAR) == oldestStat.get(Calendar.YEAR)
								&&  today.get(Calendar.DAY_OF_YEAR) == oldestStat.get(Calendar.DAY_OF_YEAR));
				if(!sameDay)
					listDates.add(today); // Si la plus vieille stat n'est pas enregistré aujourd'hui
				for(Calendar date : listDates) {
					date.set(Calendar.HOUR, 23); // Obtenir l'ELO à la fin de cette journée
					date.set(Calendar.MINUTE, 59);
					String dateCentral = StatCentral.dateFormatCentral.format(date.getTime()); 
					//System.out.println(dateCentral);
					Map<StatisticType, Float> mapStats;
					mapStats = mainWindow.serverCentralStat.getStatsAt(us.getUserName(), dateCentral);
					if(mapStats != null) {
						us.setStats(dateCentral, mapStats);
					}
				}
				listUS.add(us);
			}
		}
	}
	   
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == backButton) 
			mainWindow.displayPanel(PanelName.STATISTICS);
		if(e.getSource() == cbStatType) 
			loadChart((StatisticType)cbStatType.getSelectedItem());
	}
}
