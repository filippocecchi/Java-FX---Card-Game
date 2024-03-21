**PROGETTO SPACCA** 

APPELLO 13-09-2023 

FILIPPO CECCHI 0001070879  -  FILIPPO BRIGATI 0001070130 

Il progetto è stato realizzato su Eclipse, il progetto è un progetto JAVAFX nativo. L’applicazione è composta da più scene ed implementa la possibilità di giocare in partite singole o tornei.  

**ISTRUZIONI PER AVVIO PROGRAMMA:** 

All’interno della cartella .zip sono presenti più sottocartelle necessarie all’esecuzione del programma. All’interno della cartella “assets” sono presenti tutti i file digitali necessari per la grafica del gioco; All’interno della carta “data” sono presenti tutti i file di tipo .txt necessari per il salvataggio delle partite, degli utenti e dei loro punteggi. 

ESEGUIRE IL PROGRAMMA IN ECLIPSE: 

**NOTA:** Se Eclipse non riconosce il progetto potrebbe essere necessario installare dal marketplace di Eclipse il plug-in e(xf)clipse 3.8.0

Affinchè il programma possa funzionare è necessario installare una versione di java (Il programma è stato sviluppato in JavaSE-17) relativo al ModulePath e la libreria javaFX relativa al Classpath.  

Importante è aggiungere al Run Configurations del progetto il VM Arguments relativo a javafx SDK (il programma è stato sviluppato con javafx SDK 20.0.1). 

Potrebbe essere necessario aggiungere la cartella assets al buildpath del programma, per farlo: 

tasto destro sulla cartella assets -> Build Path -> Use as Source Folder. 

ESEGUIRE IL PROGRAMMA DA TERMINALE: 

Per eseguire il programma da terminale è necessario scaricare il file.jar e la cartella data contenente i file delle partite, è necessario che entrembe siano collocate nella stessa cartella. 

Dopo aver scaricato entrambi i file ed essersi assicurati che essi siano entrambi nella stessa cartella è necessario per poter eseguire il file.jar da terminale aggiungere il VM Arguments (Selezionare progetto -> in alto nel menu click su Run -> Run Configurations ->Arguments->copiare il VM Arguments) inserendo su terminale questo comando al momento del lancio del programma: 

Posizionare il terminale nella cartella in cui sono presenti i due file e poi eseguire questo comando: 

java (VMArguments ModulePath) -jar (./file .jar) esempio: 

java --module-path "/Users/filippocecchi/Desktop/Università/Unibo/laboratorio programmazione internet/javafx-sdk-20.0.1/lib" --add-modules javafx.controls,javafx.fxml -jar ./spacca.jar

**REGOLE DEL GIOCO:** 

**Numero di giocatori:** Da 2 a 4 giocatori (umani o robot) per partita singola. 

**Scopo del gioco:** Il giocatore che finisce per primo tutte le sue carte vince la partita oppure se le carte presenti nel mazzo terminano il giocatore che ha meno carte in mano vince la partita. **Punteggio:** I giocatori ricevono un punteggio calcolato come segue: 100 - 10 \* (numero di carte che hanno in mano) punti, il punteggio minimo è 0. 

**Regole del gioco:**  

**Inizio del gioco:** 

- Il gioco inizia il mazzo contiene carte dal valore numerico da 1->7 + due carte speciali. Il 

mazzo è composto da 36 carte. 

- Ogni giocatore riceve 5 carte all'inizio della partita. 

**Turno di gioco:** 

- I giocatori giocano con l’ordine inserito dall’admin. 
- La partita viene giocata un turno per volta alternando i giocatori. 

`    `Durante il suo turno, un giocatore può fare una delle seguenti azioni: 

- Scartare una carta: Il giocatore può scartare una carta che abbia un valore consecutivo 

rispetto a quella attualmente presente sul tavolo e con un valore superiore. Ad esempio, se sul tavolo c'è una '3', il giocatore può scartare solo un '4'. 

- Se il giocatore ha in mano due o più carte uguali scartandone una, il programma scarta 

in automatico anche tutte le altre carte dello stesso tipo. 

- Pescare una carta: Se un giocatore non può scartare una carta valida, deve pescare una 

carta dal mazzo, cliccando su una carta a caso tra quelle che ha in mano. Dopo aver pescato, il suo turno termina e passa immediatamente al giocatore successivo. 

- Carta Jolly: la carta ‘Jolly’ è una carta giocabile ogni turno e può assumere un qualsiasi 

valore numerico.  

- Carta Stop: Se un giocatore gioca una carta 'Stop', il prossimo avversario salta il proprio 

turno.  

**Fine della partita:** 

- Il gioco termina quando il mazzo termina o un giocatore vince. 

`  `-Vengono assegnati i punteggi e viene aggiornata la Leaderboard. 

La Leaderboard è una classifica generale di tutti gli utenti che hanno giocato (utenti che nel nome differiscono per sole maiuscole verranno considerati come lo stesso giocatore). 

**TORNEI:** 

La modalità tornei suddivide i giocatori in coppie, facendoli scontrare uno contro l'altro in partite singole, il vincitore della partita verrà accoppiato col vincitore di un’altra partita. La finale è tra i due giocatori rimanenti e si decide in una partita singola. Il punteggio viene assegnato ad ogni partita singola. 

**ADMIN:** 

L’admin è in grado di creare partite e tornei, aggiungere giocatori e bot alle partite, modificare il nome ed il punteggio di un utente, eliminare le partite salvate. L’ordine con cui l’admin aggiunge i giocatori alle partite sarà l’ordine con cui i giocatori si troveranno a giocare.  

**NOTA:** Per aggiornare i dati in User Edit è necessario premere invio da tastiera dopo ogni modifica.** 

**NOTA:** Per aggiungere bot alla partita l’admin deve inserire nel nome del giocatore la stringa “BOT” ed in automatico il gioco riconoscerà che quell’utente deve essere un bot. 

**NOTA:** Per accedere alla pagina dell’Admin, è necessario inserire nella schermata di login 

**Username:** admin  **Password:** admin 

Storico Partite: Ogni partita è identificata da un codice partita ed è sempre possibile sospendere una partita e continuarla inserendo il codice nella schermata di Login. Ogni partita viene memorizzata su un file.txt generato appositamente nominato esattamente come il codice partita.
