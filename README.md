Questo semplice programma permette di realizzare uno scambio di messaggi tra endpoint: abbiamo due entità che possono scrivere
del contenuto e riceverne a loro volta.\
Per farlo, ci avvaliamo delle annotation del protocollo WebSocket utilizzato. Tramite esse infatti, possiamo designare quale metodo
è opportuno per eseguire quale azione. Le richieste, a seconda di chi le invia, verranno ricevute e processate dal metodo opportuno.\
Qui di seguito la spiegazione.

Funzionamento
-
I componenti principali per il funzionamento sono i seguenti:\
1- Un controller\
2- Una pagina html utile per inviare le richieste

_NOTA: Per comunicare con un qualunque enpoit differente dalla pagina html qui presente, sarà sufficiente utilizzare il controller._

Illustriamo dunque il Controller.\
**ChatController.kt** è dunque la classe che funge da controller e gestisce le comunicazioni.\
Il main serve ovviamente per avviare il tutto, ma oltre a questo, per settare la porta e, in questo caso, il path a cui il nostro
servizio risponderà. Quando si definisce il path, viene anche designata la classe la quale sarà in grado di gestire le richieste.
Tale classe è etichettata con la annotation **@WebSocket**.\
Il controller vero e proprio è quindi la classe _ChatWSHandler_. Quest'ultima ha un metodo per ogni richiesta che può ricevere:\
1- connected, etichettato con la annotation **@OnWebSocketConnect**\
2- message, etichettato con la annotation **@OnWebSocketMessage**\
3- disconnect, etichettato con la annotation **@OnWebSocketClose**

Il funzionamento di _connected_ e _disconnect_ è immediato. All'avvio di una connessione viene stampato in console l'avvenuta
connessione. Alla disconnessione di un cliente viene stampato a console il nome dell'utente disconnesso.\
Quello che non è immediato è invece il funzionamento della gestione di
arrivo dei messaggi.\
Il metodo infatti accetta un JSON come dato in ingresso, il quale però deve essere ben costruito. Una delle sue voci deve essere
infatti la stringa "type" che differenzia la richiesta di un cliente in 2: una richiesta di join, ovvero di unirsi alla comunicazione
(che equivale ad unirsi ad un gruppo di persone che chattano), oppure una richiesta di invio di messaggio (scrivere appunto un
messaggio in una chat).\
Se siamo nel primo caso, l'utente viene aggiunto all'elenco degli utenti, tramite sessione e nome dell'utente connesso, con annessa 
stampa di avvenuta connessione. Se siamo nel secondo caso, il controller scompone il messaggio per estrapolarne il contenuto e
stamparlo a console.

Signature
-
**ChatController.kt**

--> class User(val id: Long, val name: String)\
Definisce un utente, il quale vuole come parametro l'identificativo univoco dell'utente, e come secondo parametro il nome dello stesso

--> class Message(val msgType: String, val data: Any)\
Definisce un messaggio, che come tale è composto da un tipo ("say"/"join") e dal contenuto dello stesso

-->class Invio(val nome: String, val messaggio: String)\
Definisce un Invio, quindi il nome di chi invia il messaggio e il contenuto dello stesso, come secondo parametro

-->fun connected(session: Session) = println("Session connected")\
Tale funzione richiede solo la sessione dell'utente che si è connesso

-->fun message(session: Session, message: String)\
Richiede la sessione dell'utente che si è connesso, tramite cui è possibile identificarlo, e la stringa indicante il messaggio. Il
messaggio ricevuto è di tipo JSON perciò va processato. Il primo valore da ottente è il type per poter eseguire il seguente controllo:\
when (json.get("type").asText()) {\
            "join" -> {\
            ...\
            }\
      "say" -> {\
                ...\
            }\
Si può dunque capire che tipo di messaggio è stato ricevuto analizzando la voce "type" del JSON in ingresso.

-->fun disconnect(session: Session, code: Int, reason: String?)\
Conferma la disconnessione dell'utente, e come tale richiede la ragione della disconnessione come stringa, ma può essere Null, e 
richiede la sessione, ovvero l'identificativo attraverso il quale identifichiamo, appunto, l'utente disconnesso.


Al termine del controller ci sono diverse funzioni per simulare una Chat simil Whatsapp, dove possiamo vedere, tramite il file html
allegato nel progetto, quali utenti sono connessi, che messaggi hanno scritto, chi si è disconnesso, ecc...

Qui di seguito sono illustrate le signature di tali metodi.\
-->private fun emit(session: Session, message: Message)\
Tale funzione invia un messaggio, Message (secondo parametro), ad una sessione, ovvero ad uno specifico utente (Session)

-->private fun broadcast(message: Message)
Avvalendosi della funzione di cui sopra, è immediato eseguire una send in broadcast, inviando a tutte le session che ci siamo 
salvati, il messaggio definito come Message

-->private fun broadcastToOthers(session: Session, message: Message)
Notificare solo gli altri del nostro messaggio è quello che avviene in una usuale Chat. Tramite questo metodo si esegue un
broadcast evitando di inviarlo all'utente che ha come Sessio la nostra, ovvero evitando di venire notificati dell'invio del nostro
stesso messaggio

Il Client, sender e receiver di tali messaggi, è utilizzabile accedendo tramite la pagina HTML annessa al progetto, che è già
pronta ad essere utilizzata.
