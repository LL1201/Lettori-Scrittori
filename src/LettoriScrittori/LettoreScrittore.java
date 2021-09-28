package LettoriScrittori;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.Semaphore;

public class LettoreScrittore {
	final int nLettori = 3;
	final int nScrittori = 2;
	private String[] vNomi = new String[5];
	private int[] vTempi = new int[vNomi.length];
	public static int numLettori = 0;
	public static int tScrittoreFisso = 400;
	public static int tLettoreFisso = 200;
	public static JTextArea txtArea = new JTextArea(10, 20);
	public static String stringaCondivisa;
	public static boolean finito = true;
	private Thread[] vLettoriScrittori = new Thread[nLettori + nScrittori];
	Frame f = new Frame("LONER -- Lettori-Scrittori");
	Panel p = new Panel();
	JLabel lbl = new JLabel();
	JScrollPane scrollpane = new JScrollPane(txtArea);

	// dichiarazione di tutti gli elementi grafici
	Label lblNomeScrittore1 = new Label("Scrittore 1: ");
	Label lblNomeScrittore2 = new Label("Scrittore 2: ");
	Label lblNomeLettore1 = new Label("Lettore 1: ");
	Label lblNomeLettore2 = new Label("Lettore 2: ");
	Label lblNomeLettore3 = new Label("Lettore 3: ");

	TextField txtNomeScrittore1 = new TextField();
	TextField txtNomeScrittore2 = new TextField();
	TextField txtNomeLettore1 = new TextField();
	TextField txtNomeLettore2 = new TextField();
	TextField txtNomeLettore3 = new TextField();
	TextField txtTempoScrittore1 = new TextField();
	TextField txtTempoScrittore2 = new TextField();
	TextField txtTempoLettore1 = new TextField();
	TextField txtTempoLettore2 = new TextField();
	TextField txtTempoLettore3 = new TextField();

	Label lblVuota = new Label("");
	Label lblVuota1 = new Label("");
	Label lblNome = new Label("Nome");
	Label lblTempo = new Label("Tempo");

	Button btnInizia = new Button("Inizia");
	Button btnCancella = new Button("Cancella tutto");
	Button btnLeggiTutto = new Button("Leggi tutto");
	Button btnStop = new Button("Stop");

	LettoreScrittore() {
		// aggiunta di tutti gli elementi grafici al frame
		txtArea.setBounds(10, 10, 10, 10);
		p.setLayout(new GridLayout(8, 3));
		btnInizia.addActionListener(new Inizia());
		btnInizia.setEnabled(false);
		btnCancella.addActionListener(new CancellaTutto());
		btnLeggiTutto.addActionListener(new LeggiTutto());
		btnStop.addActionListener(new Stop());

		f.setSize(600, 600);
		f.setLocation(100, 100);
		f.setLayout(new BorderLayout());

		p.add(lblVuota);
		p.add(lblNome);
		p.add(lblTempo);

		p.add(lblNomeScrittore1);
		p.add(txtNomeScrittore1);
		p.add(txtTempoScrittore1);

		p.add(lblNomeScrittore2);
		p.add(txtNomeScrittore2);
		p.add(txtTempoScrittore2);

		p.add(lblNomeLettore1);
		p.add(txtNomeLettore1);
		p.add(txtTempoLettore1);

		p.add(lblNomeLettore2);
		p.add(txtNomeLettore2);
		p.add(txtTempoLettore2);

		p.add(lblNomeLettore3);
		p.add(txtNomeLettore3);
		p.add(txtTempoLettore3);

		p.add(btnCancella);
		p.add(btnInizia);
		p.add(btnLeggiTutto);
		p.add(lblVuota1);
		p.add(btnStop);

		f.add("South", scrollpane);
		f.addWindowListener(new Chiudi());
		f.add("North", p);

		p.setVisible(true);
		f.setVisible(true);
		btnStop.setEnabled(false);
	}

	class Inizia implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Semaphore scrittura = new Semaphore(1);
			Semaphore lettura = new Semaphore(1);

			txtTempoLettore1.setEnabled(false);
			txtTempoLettore2.setEnabled(false);
			txtTempoLettore3.setEnabled(false);
			txtTempoScrittore1.setEnabled(false);
			txtTempoScrittore2.setEnabled(false);
			btnInizia.setEnabled(false);
			btnStop.setEnabled(true);
			btnLeggiTutto.setEnabled(false);

			// assegnazione dei thread al vettore con tutti i parametri di tempi e nomi
			for (int i = 0; i < nLettori; i++) {
				vLettoriScrittori[i] = new Thread(new Lettore(vTempi[i], scrittura, lettura), vNomi[i]);
				vLettoriScrittori[i].start();
			}

			for (int i = nLettori; i < nScrittori + nLettori; i++) {
				vLettoriScrittori[i] = new Thread(new Scrittore(vTempi[i], scrittura), vNomi[i]);
				vLettoriScrittori[i].start();
			}
		}
	}

	class Stop implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// per terminare run dei thread
			finito = false;
		}
	}

	class CancellaTutto implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// imposta a vuoto tutte le caselle di testo
			txtNomeLettore1.setText("");
			txtNomeLettore2.setText("");
			txtNomeLettore3.setText("");
			txtNomeScrittore1.setText("");
			txtNomeScrittore2.setText("");
			txtTempoLettore1.setText("");
			txtTempoLettore2.setText("");
			txtTempoLettore3.setText("");
			txtTempoScrittore1.setText("");
			txtTempoScrittore2.setText("");
			txtNomeLettore1.setEnabled(true);
			txtNomeLettore2.setEnabled(true);
			txtNomeLettore3.setEnabled(true);
			txtNomeScrittore1.setEnabled(true);
			txtNomeScrittore2.setEnabled(true);
		}
	}

	class LeggiTutto implements ActionListener {
		private String stringaErrore = ""; // stringa che server per memorizzare i dettagli degli errori
		private boolean flagNomi = false, flagTempi = false; // verificano se i controlli sui nomi e i tempi sono stati
																// effettuati o meno

		// warning pane per comunicare gli errori all'utente
		private class WarningPane {
			JFrame f;

			WarningPane(String messaggio) {
				f = new JFrame();
				JOptionPane.showMessageDialog(f, messaggio, "Errore", JOptionPane.WARNING_MESSAGE);
			}
		}

		// funzione per cercare i doppioni sul vettore di nomi
		private boolean ValidaNomiDoppi() {
			boolean flag = false;
			int i = 0;
			int j = 0;

			do {
				j = i + 1;
				while (j < vNomi.length) {
					if (vNomi[i].compareTo(vNomi[j]) == 0) {
						flag = true;
					}
					j++;
				}
				i++;
			} while (i < vNomi.length - 1 && flag == false);

			return flag; // ritorna true se ha trovato doppioni
		}

		// funzione per verificare caselle di testo senza nomi inseriti
		private boolean ValidaNomiVuoti() {
			boolean flag = false;
			for (int i = 0; i < vNomi.length; i++) {
				if (vNomi[i].equals("")) {
					flag = true;
				}
			}
			return flag;
		}

		// funzione per verificare la validità dei tempi inseriti
		private void ValidaTempi(TextField nome, TextField tempo) {
			try {
				// i blocchi if verificano se non è stato inserito un numero, se è minore di 500
				// o maggiore di 5000
				// il try catch cattura un inserimento errato del valore (ad esempio lettere)
				if (tempo.getText().length() == 0) {
					stringaErrore += nome.getText() + " non ha un tempo impostato\n";
				} else if (Integer.parseInt(tempo.getText()) < 500) {
					stringaErrore += nome.getText() + " ha un tempo troppo basso o negativo\n";
				} else if (Integer.parseInt(tempo.getText()) > 5000) {
					stringaErrore += nome.getText() + " ha un tempo troppo alto\n";
				}
			} catch (NumberFormatException e) {
				stringaErrore += nome.getText() + " ha un tempo con un formato non consentito\n";
			}
		}

		public void actionPerformed(ActionEvent e) {

			// se il controllo dei nomi non è stato effettuato o ha dato risultati negativi
			// viene riempito il vettore con i nomi delle caselle di testo
			if (flagNomi == false) {
				vNomi[0] = txtNomeLettore1.getText();
				vNomi[1] = txtNomeLettore2.getText();
				vNomi[2] = txtNomeLettore3.getText();
				vNomi[3] = txtNomeScrittore1.getText();
				vNomi[4] = txtNomeScrittore2.getText();

				// controlla cosa restituisce la funzione che controlla i nomi
				if (ValidaNomiVuoti()) {
					new WarningPane("Alcune caselle dei nomi sono vuote, correggi!");
				} else if (ValidaNomiDoppi()) {
					new WarningPane("Ci sono nomi doppi, correggi!");
				} else {
					// se i controlli vanno a buon fine si bloccano tutte le caselle di testo in
					// modo da evitare modifiche e si imposta il flag di avvenuto controllo a true
					flagNomi = true;
					txtNomeLettore1.setEnabled(false);
					txtNomeLettore2.setEnabled(false);
					txtNomeLettore3.setEnabled(false);
					txtNomeScrittore1.setEnabled(false);
					txtNomeScrittore2.setEnabled(false);
				}
			}

			// verifica se il controllo sui tempi è stato effettuato
			if (flagNomi && !flagTempi) {
				stringaErrore = "";
				ValidaTempi(txtNomeLettore1, txtTempoLettore1);
				ValidaTempi(txtNomeLettore2, txtTempoLettore2);
				ValidaTempi(txtNomeLettore3, txtTempoLettore3);
				ValidaTempi(txtNomeScrittore1, txtTempoScrittore1);
				ValidaTempi(txtNomeScrittore2, txtTempoScrittore2);

				if (stringaErrore.length() != 0)
					new WarningPane(stringaErrore);
				else
					flagTempi = true;
			}

			// se tutti i controlli danno esito positivo si bloccano tutte le caselle di
			// testo e i pulsanti non più necessari
			// viene abilitato il pulsante inizia
			if (flagNomi && flagTempi) {
				vTempi[0] = Integer.parseInt(txtTempoLettore1.getText());
				vTempi[1] = Integer.parseInt(txtTempoLettore2.getText());
				vTempi[2] = Integer.parseInt(txtTempoLettore3.getText());
				vTempi[3] = Integer.parseInt(txtTempoScrittore1.getText());
				vTempi[4] = Integer.parseInt(txtTempoScrittore2.getText());
				btnInizia.setEnabled(true);
				btnCancella.setEnabled(false);

				for (int i = 0; i < nLettori; i++) {
					txtArea.append("Il Lettore " + vNomi[i] + " ha un tempo max di: " + vTempi[i] + "\n");
				}
				for (int i = nLettori; i < nScrittori + nLettori; i++) {
					txtArea.append("Lo scrittore " + vNomi[i] + " ha un tempo max di: " + vTempi[i] + "\n");
				}
			}
		}
	}

	// permette la chiusura della finestra cliccando sulla X
	class Chiudi extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

	}

	public static void main(String[] args) {
		new LettoreScrittore();
	}
}
