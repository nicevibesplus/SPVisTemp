const express = require('express');
const app = express();
const port = 3000;

let temperature = 20; // Ausgangstemperatur

// API-Endpunkt, um die Temperatur abzurufen
app.get('/api/temperature', (req, res) => {
  res.json({ temperature });
});

// API-Endpunkt, um die Temperatur zu ändern
app.post('/api/temperature', (req, res) => {
  const newTemperature = parseFloat(req.query.value);
  if (!isNaN(newTemperature)) {
    temperature = newTemperature;
    res.json({ success: true, temperature });
  } else {
    res.status(400).json({ success: false, message: "Invalid temperature value" });
  }
});

// Statische Dateien bereitstellen
app.use(express.static('public'));

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});