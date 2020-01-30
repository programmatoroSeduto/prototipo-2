# Prototipo - Propagazione valori in una matrice

## per avviare il progetto:
- scarica il jar, "es.txt" e il batch "start.bat"
- regola il simulatore dal file es.txt
- avvia la simulazione da start.bat

## configurazione simulatore
valori sulla prima riga:
- range x matrice
- range y matrice
- coefficiente di propagazione(attenzione: usare la virgola, non il punto, per il decimale)
valori sulle righe restanti:
- posizione x
- posizione y
- valore da sommare all'attuale della cella
### esempio:
 - 10 10 0.9
 - 0 0 50
 - 0 1 25
 - 1 0 12,5
 - 1 1 6,25

## Utilizzo del simulatore
- per aggiornare e stampare a video i valori al ciclo di clock successivo, digita "y"
- per chiudere il simulatore, digita qualunque altra cosa eccetto "y"

## note varie
- i valori visualizzati dal simulatore sono approssimati per poter essere contenuti nella schermata
- per cambiare le impostazioni di visualizzazione (attualmente) è necessario cambiare le impostazioni dal file Main.java; richiede ricompilazione
- per valori di propagazione prossimi a 1 e con grandi quantitativi di risorsa da propagare, la variazione della densità locale presenta oscillazioni, che si riducono di intensità man mano che il coefficiente diventa sempre più piccolo

una cagata
