# **Remote-Control-App für die IVE-Visualisierung**  

## **Übersicht**  
Diese App steuert unsere **lokale Temperaturvisualisierung**, durch Interaktion mit:  
1. Einem **Webserver** für die Bereitstellung von Webinhalten  
2. Der **IVE** (Immersive Visualization Environment)  


Die App ermöglicht es den Nutzern, durch verschiedene Jahre (**2020–2100**) in unterschiedlichen **Klimaszenarien und Orten** zu navigieren und **Umweltmaßnahmen zu aktivieren**.  

---

## **Installation und Einrichtung**  

### **1. Voraussetzungen**  
- **Android 8.0 (API Level 26) oder höher**  
- **Entwicklermodus & USB-Debugging** müssen auf dem Gerät aktiviert sein (falls Installation per USB)  
- **Verbindung zum gleichen Netzwerk wie die Webserver** (siehe unten für Details)  

### **2. Installation der App**  

#### **Ausführung über Android Studio (für Entwickler)**  
1. Klone das Repository und öffne es in **Android Studio**.  
2. Verbinde ein **Android-Gerät** oder starte einen **Emulator**.  
3. Klicke auf **Run ▶**, um die App zu installieren und zu starten.  

---

## **Netzwerkverbindung erforderlich**  
Vor dem Start der App muss das Gerät mit demselben Netzwerk wie die beiden Webserver verbunden sein:  

### **Verwendung der Sitcomdev-Server**  
- Eine Verbindung mit dem **Uni Münster VPN** ist erforderlich.  
- Die App kommuniziert mit folgenden Servern:  
  - **[http://giv-sitcomdev.uni-muenster.de:2000](http://giv-sitcomdev.uni-muenster.de:2000)**  
  - **[http://giv-sitcomdev.uni-muenster.de:5000](http://giv-sitcomdev.uni-muenster.de:5000)**  
- **Alle Assets (Videos, Bilder, Overlays und Daten) sind auf den Sitcomdev-Servern gespeichert.**  

### **Lokale Ausführung**  
Falls die App **lokal ausgeführt** werden soll, sind folgende Schritte erforderlich:  
1. **Lokale Installation der IVE einrichten** ([Installationsanleitung](https://sitcomlab.github.io/IVE/install/))  
2. **Lokale Version des zweiten Webservers einrichten** (Code befindet sich im Ordner `ivewebcontent`).  
3. **Videos und Overlays von den Sitcomdev-Servern auf die lokale IVE-Installation hochladen**.  

---

## **Fehlersuche**  
- **Verbindungsprobleme?**  
  - Stelle sicher, dass dein Gerät mit dem **richtigen Netzwerk** verbunden ist.  
  - Falls du die **Sitcomdev-Server** nutzt, verbinde dich mit dem **Uni Münster VPN**.  

- **Overlays werden nicht angezeigt?**  
  - Stelle sicher, dass der **richtige Standort und das richtige Szenario** ausgewählt sind (setup-screen)
 

- **Fehlersuche & Debugging**  
  - Nutze **Logcat in Android Studio** oder überprüfe die **Toast-Nachrichten**, die in der App angezeigt werden.  