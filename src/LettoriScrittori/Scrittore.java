package LettoriScrittori;

import java.util.concurrent.Semaphore;

public class Scrittore implements Runnable {
	private int massimo, tempo, vita = 0;
	private Semaphore scrittore;

	public Scrittore(int massimo, Semaphore scrittore) {
		this.massimo = massimo;
		this.scrittore = scrittore;
	}

	public void run() {
		while (LettoreScrittore.finito) {
			try {
				// tempo sleep random tra un minimo di 500 e un
				// massimo passato dal costruttore
				tempo = 500 + (int) (Math.random() * (massimo - 500));
				vita += tempo;
				LettoreScrittore.txtArea.append("Ciao sono lo scrittore " + Thread.currentThread().getName()
						+ " e attendo " + tempo + " ms prima di scrivere\n");
				Thread.sleep(tempo);

				scrittore.acquire();
				// aspetta il tempo di scrittura preimpostato
				Thread.sleep(LettoreScrittore.tScrittoreFisso);
				vita += LettoreScrittore.tScrittoreFisso;
				// stampa il proprio nome sulla stringa
				LettoreScrittore.stringaCondivisa = Thread.currentThread().getName();
				LettoreScrittore.txtArea.append("Ciao sono lo scrittore " + Thread.currentThread().getName()
						+ " e ho un scritto '" + LettoreScrittore.stringaCondivisa + "'\n");

				scrittore.release();
			} catch (InterruptedException e) {

			}
		}

		// messaggio per stampare la durata in ms della vita
		LettoreScrittore.txtArea
				.append("La vita dello scrittore " + Thread.currentThread().getName() + " è stata di " + vita + "\n");
	}
}
