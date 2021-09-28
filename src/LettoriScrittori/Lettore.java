package LettoriScrittori;

import java.util.concurrent.Semaphore;

public class Lettore implements Runnable {
	private int massimo, tempo, vita = 0;
	private Semaphore scrittore, lettore;

	public Lettore(int massimo, Semaphore scrittore, Semaphore lettore) {
		this.massimo = massimo;
		this.scrittore = scrittore;
		this.lettore = lettore;
	}

	public void run() {

		while (LettoreScrittore.finito) {
			try {
				// tempo sleep random tra un minimo di 500 e un
				// massimo passato dal costruttore
				tempo = 500 + (int) (Math.random() * (massimo - 500));
				vita += tempo;
				LettoreScrittore.txtArea.append("Ciao sono il lettore " + Thread.currentThread().getName()
						+ " e attendo " + tempo + " ms prima di leggere\n");
				Thread.sleep(tempo);

				// acquisizione del semaforo lettori in modo da modificare il valore della
				// variabile che tiene traccia del numero di lettori in lettura
				lettore.acquire();
				LettoreScrittore.numLettori++;

				// se è il primo lettore acquisisce il semaforo di scrittura
				if (LettoreScrittore.numLettori == 1) {
					scrittore.acquire();
				}
				lettore.release();

				// controlla che la stringa contenga qualcosa altrimenti stampa un messaggio di
				// errore
				if (LettoreScrittore.stringaCondivisa != null) {
					Thread.sleep(LettoreScrittore.tLettoreFisso);
					LettoreScrittore.txtArea.append("Ciao sono il lettore " + Thread.currentThread().getName()
							+ " e sto leggendo " + LettoreScrittore.stringaCondivisa + ". Ci sono "
							+ LettoreScrittore.numLettori + " lettori in lettura\n");
				} else {
					LettoreScrittore.txtArea.append("Ciao sono il lettore " + Thread.currentThread().getName()
							+ " e non c'è scritto ancora niente nella stringa!\n");
				}

				lettore.acquire();
				LettoreScrittore.numLettori--;

				// se è l'ultimo lettore rilascia il semaforo di scrittura per permettere agli
				// scrittori di scrivere
				if (LettoreScrittore.numLettori == 0) {
					scrittore.release();
				}

				lettore.release();

			} catch (InterruptedException e) {

			}
		}

		// messaggio per stampare la durata in ms della vita
		LettoreScrittore.txtArea
				.append("La vita del lettore " + Thread.currentThread().getName() + " è stata di " + vita + "\n");
	}
}
