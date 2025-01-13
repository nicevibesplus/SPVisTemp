async function updateInfotextDisplay() {
    try {
        const response = await fetch('/api/infotext');
        const data = await response.json();

        const infotext = data.infotext;

        document.getElementById("infotext").innerHTML = infotext
    } catch (error) {
        console.error('Error fetching infotext:', error);
    }
}

// Aktualisiere die Anzeige regelmäßig
setInterval(updateInfotextDisplay, 1000);
updateInfotextDisplay()