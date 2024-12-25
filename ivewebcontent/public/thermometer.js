const minTemp = 0;
const maxTemp = 40;
const temperatureElement = document.getElementById("temperature");

// Funktion, um die Temperaturanzeige zu aktualisieren
async function updateTemperatureDisplay() {
    try {
        const response = await fetch('/api/temperature');
        const data = await response.json();

        const currentTemperature = data.temperature;

        // Berechne die Höhe des Temperatur-Balkens als Prozentsatz
        const fillPercentage = ((currentTemperature - minTemp) / (maxTemp - minTemp)) * 100;
        temperatureElement.style.height = fillPercentage + '%';
        temperatureElement.dataset.value = currentTemperature + '°C'; // Anzeige der Temperatur im Balken
    } catch (error) {
        console.error('Error fetching temperature:', error);
    }
}

// Aktualisiere die Anzeige regelmäßig
setInterval(updateTemperatureDisplay, 1000);
updateTemperatureDisplay();