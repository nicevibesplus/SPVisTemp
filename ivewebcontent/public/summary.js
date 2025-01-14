async function updateInfotextDisplay() {
    try {
        const response = await fetch('/api/summary');
        const data = await response.json();

        const summary = data.summary;

        document.getElementById("summary").innerHTML = summary
    } catch (error) {
        console.error('Error fetching summary:', error);
    }
}

// Aktualisiere die Anzeige regelmäßig
setInterval(updateInfotextDisplay, 1000);
updateInfotextDisplay()