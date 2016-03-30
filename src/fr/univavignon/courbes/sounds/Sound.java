package fr.univavignon.courbes.sounds;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import fr.univavignon.courbes.inter.ErrorHandler;


/**
 * Classe permettant le chargement des sons et les opérations liés
 * à ceux ci
 *
 */
public final class Sound implements SoundEngine, ErrorHandler {

	/** Musique lancé et rebouclé pendant toute une partie **/
	public static final Sound MusicInGame = new Sound("res/sounds/MusicInGame.wav");
	/** Son lancé lors d'une collision d'un serpent entrainant sa mort **/
	public static final Sound DeathCollision = new Sound("res/sounds/DeathCollision.wav");
	/** Son lancé lorsqu'un serpent ramasse un item **/
	public static final Sound ItemCollision = new Sound("res/sounds/ItemCollision.wav");
	/** Son lancé lorsqu'un item apparaît sur le plateau **/
	public static final Sound ItemSpawn = new Sound("res/sounds/ItemSpawn.wav");
	/** Son lancé lorsque l'utilsateur clique sur un bouton dans le menu du jeu **/
	public static final Sound ClickButton = new Sound("res/sounds/ClickButton.wav");
	/** Son lancé lorsqu'un effet d'item sur un snake se termine **/
	public static final Sound EffectEnded = new Sound("res/sounds/EffectEnded.wav");

	/**
	 * Instance permettant le chargement d'un fichier son en mémoire puis
	 * de le jouer, le stopper..
	 */
	private Clip clip;

	/**
	 * Charge le son en mémoire
	 * @param path Chemin vers le son
	 */
	private Sound(String path) {
		try {
			InputStream is = new FileInputStream(path); 
			InputStream bufferedIn = new BufferedInputStream(is); 
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
			clip = AudioSystem.getClip(null); // Obtient une resource lié à un Clip
			clip.open(audioIn); // Ouvre le clip audio et charge le son depuis le flux du fichier
		} catch (Exception e) {
			e.printStackTrace();
			displayError("Erreur lors du chargement du son, verifiez le chemin : " + path);
		}
	}

	/**
	 * Lance la lecture d'une instance Sound dans un nouveau Thread
	 * via l'instance de Clip
	 */
	@Override
	public void play() {
		try {
			new Thread() {
				@Override
				public void run() {
					// Met en écoute du changement d'état du son
					clip.addLineListener(new LineListener() {
						@Override
						public void update(LineEvent event) {
							if (event.getType() == LineEvent.Type.STOP) {	
								Sound.this.stop(true); // Stoppe le son (pour pouvoir être redemarré plus tard)
							}
						}
					});
					clip.start(); // Lance le son
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playLoop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		try {
			new Thread() {
				@Override
				public void run() {
					clip.start();
				}
			}.start();
			clip.start(); // Lance le son
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isPlaying() {
		if(clip.isRunning())
			return true;
		else
			return false;
	}

	@Override
	public void stop(boolean restart) {
		clip.stop();
		if(restart) {
			clip.setFramePosition(0);
		}
	}

	@Override
	public void displayError(String errorMessage) {
		System.out.println(errorMessage);
	}
}