const express = require('express');
const app = express();
app.use(express.json())
const port = 2000;

let temperature = 20; // Ausgangstemperatur
let calendar = { date: new Date(), frequency: 0, heatDays: [] }; // Ausgangswert für Hitzetage im Monat
let infotext = "empty infotext";

const generateRandomIntegers = (amount, min, max, randomIntegers = []) => {
  for (let i = 0; i < amount; i++) {
    const randomNum = Math.floor(Math.random() * (max - min + 1)) + min;
    randomIntegers.push(randomNum);
  }
  return randomIntegers;
}

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

app.get('/api/calendar', (req, res) => {
  res.json(calendar);
});

app.post('/api/calendar', (req, res) => {
  const newDate = req.query.date || null;
  const newFrequency = req.query.frequency || null;
  try {
    if (newDate) {
      calendar.date = newDate
    }
    if (newFrequency) {
      calendar.frequency = newFrequency
      calendar.heatDays = generateRandomIntegers(calendar.frequency, 1, 31)
    }
    res.json({ success: true, calendar })
  } catch (error) {
    res.status(400).json(error);
  }
})

app.get('/api/summary', (req, res) => {
  res.json({ summary });
})

app.post('/api/summary', (req, res) => {
  const newSummary = req.body.summary || null;
  console.log(req.body)
  console.log(newSummary)
  try {
    if (newSummary) {
      summary = newSummary
    }
    res.json({ success: true, summary })
  } catch (error) {
    res.status(400).json(error);
  }
})

app.get('/api/infotext', (req, res) => {
  res.json({ infotext });
})

app.post('/api/infotext', (req, res) => {
  const newInfotext = req.body.infotext || null;
  console.log(req.body)
  console.log(newInfotext)
  try {
    if (newInfotext) {
      infotext = newInfotext
    }
    res.json({ success: true, infotext })
  } catch (error) {
    res.status(400).json(error);
  }
})

// Statische Dateien bereitstellen
app.use(express.static('public'));

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
