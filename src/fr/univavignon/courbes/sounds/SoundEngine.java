package fr.univavignon.courbes.sounds;

/**
 * Interface utile à l'implémentation de la Classe gérant le son dans le jeu
 *
 */
public interface SoundEngine {

	/**
	 * Méthode d'interface permettant le lancement d'un son
	 */
	public void play();
	
	/**
	 * Méthode d'interface permettant le bouclage d'un son jusqu'à
	 * l'arrêt manuel de celui-ci
	 */
	public void playLoop();
	
	/**
	 * Méthode d'interface permettant de stopper un son lancé
	 * @param restart true si l'on veut que le son redemarre depuis le début, sinon false
	 */
	public void stop(boolean restart);
	
	/**
	 * Méthode d'interface permettant de savoir si un son en question est en train
	 * d'être joué
	 * @return true si le son est en train d'être joué, sinon false
	 */
	public boolean isPlaying();
	
	
}
