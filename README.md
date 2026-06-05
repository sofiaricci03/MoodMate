# MoodMate

## Scopo del progetto
L’obiettivo del progetto è la realizzazione di un’applicazione mobile sviluppata in Android, denominata Moodmate, che aiuta l’utente a monitorare il proprio stato d’animo nel tempo e a migliorare il proprio benessere personale attraverso suggerimenti basati su condizioni meteo, frasi motivazionali e consigli di attività salutari. L’applicazione permetterà all’utente di registrare quotidianamente il proprio umore e di visualizzarne l’andamento attraverso grafici e statistiche intuitive. In base ai dati inseriti e alle condizioni meteorologiche del momento, l’app fornirà messaggi motivazionali e suggerimenti utili per migliorare la giornata.

---
## Struttura del Progetto
Prima dello sviluppo dell'applicazione, è stato implementato un mockup in figma 
 **[Link Mockup Figma](https://www.figma.com/design/0G1lLEznQgFjJybXxowbYl/Android-Mockup--Community-?node-id=1-126&t=u2IQWHWFHLeNNg4h-1)**

L'applicazione è suddivisa in 4 moduli indipendenti:
- app: modulo Applicazione (Entry Point)
- ui: modulo Presentazione (UI Layer)
- domain: modulo Dominio (Business Logic)
- data: modulo Dati (Data Layer)

### 1. Modulo App
Contiene le entry point dell'applicazione. AndroidManifest.xml: Definisce 4 Activity principali:
- LoginActivity - Activity principale lanciata all'avvio
- RegisterActivity - Pagina di registrazione
- MainActivity - Home page principale
- EditProfileActivity - Pagina di modifica dei dati personali
Moodmate.kt è la Classe Application che inizializza il Service Locator per l'iniezione delle dipendenze

### 2. Modulo Domain
Racchiude la logica di business e non ha dipendenze da Android SDK. Contiene solo interfacce repository e modelli di dominio e la sua struttura si suddivide in:
domain/src/main/java/com/corsolp/domain/
- di: Service Locator e Dependency Injection
- models: data classes di dominio
- repository: interfacce dei repository (astrazioni)

### 3. Modulo UI
Comprende la parte di interfaccia utente e si suddivide così:
ui/src/main/java/com/corsolp/ui/
- login: autenticazione
- registration: registrazione utente
- home: schermata principale
- calendar: visualizzazione calendario umore 
- moodInput: input dello stato d'umore
- statistics: tatistiche e analitiche
- profile: profilo utente
- editeProfile: modifica dati profilo utente

Le tecnologie adottate sono state ViewModel + LiveData (per la gestione dello stato UI) e Kotlin Coroutines (per le operazioni asincrone)

### 4. Modulo Data
Il seguente modulo si occupa del recupero, gestione dati ed è suddiviso in:
data/src/main/java/com/corsolp/data/
- di: dependency injection (RepositoryProviderImpl)
- local: database room (persistenza locale)
- remote: Chiamate API (Retrofit + Moshi)
- repository: implementazioni concrete dei repository
- worker: background tasks (WorkManager)

---
## Punti di forza del progetto MoodMate
Il progetto è ben organizzato in 4 moduli indipendenti (app, ui, domain, data), seguendo il pattern Clean Architecture. Questo consente:
- Facilità di manutenzione e debugging
- Riusabilità del codice
- Testabilità indipendente dei moduli

L'applicazione è stata progettata seguendo i principi delle moderne architetture software Android, con particolare attenzione alla modularità, alla manutenibilità del codice e alla scalabilità del progetto. La separazione delle responsabilità tra i diversi livelli dell'applicazione consente di ottenere una struttura chiara e ben organizzata, facilitando sia lo sviluppo che le future attività di manutenzione.

Dal punto di vista tecnologico, il progetto adotta uno stack moderno e consolidato. L'intera applicazione è sviluppata in Kotlin scelto per la sua efficienza per la programmazione in sistemi mobile Android. La gestione dello stato dell'interfaccia utente è affidata all'architettura ViewModel associata a LiveData per la maggior parte delle activity e fragment, che consente un aggiornamento efficiente e reattivo delle schermate. Le operazioni asincrone vengono invece gestite tramite Kotlin Coroutines migliorando l'esperienza utente. 
Per la persistenza locale dei dati è stata utilizzata la libreria Room, che fornisce un livello di astrazione robusto e sicuro. Le attività pianificate e le operazioni eseguite in background sono invece gestite tramite WorkManager, assicurando affidabilità.

Un ulteriore elemento di qualità architetturale è rappresentato dalla gestione centralizzata delle dipendenze attraverso un Service Locator implementato nella classe Moodmate.kt. Questo approccio permette di mantenere una configurazione coerente dei componenti applicativi, facilita l'introduzione di oggetti simulati (mock) durante i test e riduce l'accoppiamento tra i diversi moduli dell'applicazione.

Le funzionalità implementate sono state progettate con un focus chiaro e ben definito. L'applicazione offre un sistema di autenticazione utente, il monitoraggio dell'umore nel tempo, la visualizzazione dei dati tramite un calendario interattivo, strumenti statistici per l'analisi dell'andamento emotivo settimnale e una sezione dedicata alla gestione del profilo personale. 

---
## Implementazioni future
Nonostante il progetto soddisfi pienamente i requisiti funzionali e architetturali, si possono prevedere le seguenti estensioni future:
- possibilità di una visione mensile delle statistiche
- cambiamento dell'umore giornaliero anche se già inserito e riprogrammazione dei consigli per quella giornata
- consigli salutari presi da un'API
- frasi motivazionali disponibili in italiano e dipendenti dall'umore inserito dall'utente
