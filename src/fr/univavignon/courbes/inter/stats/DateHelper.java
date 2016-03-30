package fr.univavignon.courbes.inter.stats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Méthodes d'aide à la manipulation de dates
 */
public final class DateHelper {

	private DateHelper() {};

	/**
	 * @return Le nombre de jour entre deux dates
	 */
	public static int daysBetween(Calendar day1, Calendar day2){
		Calendar dayOne = (Calendar) day1.clone(),
				dayTwo = (Calendar) day2.clone();

		if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
			return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
		} else {
			if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
				//swap them
				Calendar temp = dayOne;
				dayOne = dayTwo;
				dayTwo = temp;
			}
			int extraDays = 0;

			int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

			while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
				dayOne.add(Calendar.YEAR, -1);
				extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
			}
			return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
		}
	}

	/**
	 * 
	 * @param number Nombre de dates souhaité entre les deux dates
	 * @return List de 'number' dates 
	 */
	public static List<Calendar> dateInInterval(Calendar dateA, Calendar dateB, int number)
	{
		Calendar d1,d2;
		if(dateA.before(dateB)){
			d1 = (Calendar) dateA.clone();
			d2 = (Calendar) dateB.clone();
		} else {
			d1 = (Calendar) dateB.clone();
			d2 = (Calendar) dateA.clone();
		}	
		
		List<Calendar> ld = new ArrayList<Calendar>();
		int daysBetween = daysBetween(d1,d2);
		
		if(daysBetween < number) {
			number = daysBetween;
			for(int i=0; i<number; i++) {
				d1.add(Calendar.DAY_OF_MONTH, 1);
				ld.add((Calendar) d1.clone());
			}
			return ld;
		}
		else if(number != 0 && daysBetween != 0) {
			int interval = (int) ((daysBetween-(daysBetween / number * 0.5)) / number);
			for(int i=0; i<number; i++) {
				d1.add(Calendar.DAY_OF_MONTH, interval);
				ld.add((Calendar) d1.clone());
			}
		}
		return ld;
	}
	
   public static void main (String[] args){
	   Calendar dateA = new GregorianCalendar(2016,0,30);
	   Calendar dateB = new GregorianCalendar(2016,0,30);
	   List<Calendar > list = dateInInterval(dateA, dateB, 10);
	   for(Calendar c : list) {
		   System.out.println(new SimpleDateFormat("dd/MM/YY").format(c.getTime()));
	   }
   }
}
