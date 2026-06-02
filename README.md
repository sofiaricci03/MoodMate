# MoodMate

## Scopo del progetto
L’obiettivo del progetto è la realizzazione di un’applicazione mobile sviluppata in Android, denominata Moodmate, che aiuta l’utente a monitorare il proprio stato d’animo nel tempo e a migliorare il proprio benessere personale attraverso suggerimenti basati su condizioni meteo, frasi motivazionali e consigli di attività salutari. L’applicazione permetterà all’utente di registrare quotidianamente il proprio umore e di visualizzarne l’andamento attraverso grafici e statistiche intuitive. In base ai dati inseriti e alle condizioni meteorologiche del momento, l’app fornirà messaggi motivazionali e suggerimenti utili per migliorare la giornata.

---
## Struttura del Progetto
L'applicazione è suddivisa in 4 moduli indipendenti:
- app: modulo Applicazione (Entry Point)
- ui: modulo Presentazione (UI Layer)
- domain: modulo Dominio (Business Logic)
- data: modulo Dati (Data Layer)

### 1. Modulo App
Contiene le entry point dell'applicazione. AndroidManifest.xml: Definisce 3 Activity principali:
- MainActivity (Login) - Activity principale lanciata all'avvio
- RegisterActivity - Pagina di registrazione
- HomeActivity - Home page principale
Moodmate.kt è la Classe Application che inizializza il Service Locator per l'iniezione delle dipendenze

### 2. Modulo Domain
Rachhiude la logic a di buisness e non ha dipendenze da Android SDK. Contiene solo interfacce repository e modelli di dominio e la sua struttura si suddivide in:
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

Le tecnologie adottate sono state ViewModel + LiveData (per la gestione dello stato UI) e Kotlin Coroutines (per le operazioni asincrone)

### 4. Modulo Data
Il seguente modulo si occupa del recupero, gestione dati ed è suddiviso in:
data/src/main/java/com/corsolp/data/
- di: dependency injection (RepositoryProviderImpl)
- local: database room (persistenza locale)
- remote: API REST (Retrofit + Moshi)
- repository: implementazioni concrete dei repository
- worker: background tasks (WorkManager)


