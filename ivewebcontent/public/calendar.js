const day = document.querySelector(".calendar-dates");

const currdate = document
    .querySelector(".calendar-current-date");

const prenexIcons = document
    .querySelectorAll(".calendar-navigation span");

// Array of month names
const months = [
    "Januar",
    "Februar",
    "März",
    "April",
    "Mai",
    "Juni",
    "Juli",
    "August",
    "September",
    "Oktober",
    "November",
    "Dezember"
];

// Function to generate the calendar
const updateCalendarDisplay = async () => {
    try {
        const response = await fetch('/api/calendar');
        const data = await response.json();

        const heatDays = data.heatDays;
        const date = data.date;

        // Update the calendar display
        updateVisuals(heatDays, date);
    } catch (error) {
        console.error('Error fetching calendar data:', error);
    }
}



const updateVisuals = (heatDays, datum) => {

    let date = new Date(datum);
    let year = date.getFullYear();
    let month = date.getMonth();

    // Get the first day of the month
    let dayone = new Date(year, month, 1).getDay() - 1;
    if (dayone == -1) dayone = 6

    // Get the last date of the month
    let lastdate = new Date(year, month + 1, 0).getDate();

    // Get the day of the last date of the month
    let dayend = new Date(year, month, lastdate).getDay() - 1;
    if (dayend == -1) dayend = 6

    // Get the last date of the previous month
    let monthlastdate = new Date(year, month, 0).getDate();

    // Variable to store the generated calendar HTML
    let lit = "";

    // Loop to add the last dates of the previous month
    for (let i = dayone; i > 0; i--) {
        lit +=
            `<li class="inactive">${monthlastdate - i + 1}</li>`;
    }

    // Loop to add the dates of the current month
    for (let i = 1; i <= lastdate; i++) {

        // Check if the current date is today
        if (heatDays.includes(i)) {
            lit += `<li style="color: red; font-weight: bold;">${i}</li>`
        } else {
            lit += `<li>${i}</li>`;
        }
    }

    // Loop to add the first dates of the next month
    for (let i = dayend; i < 6; i++) {
        lit += `<li class="inactive">${i - dayend + 1}</li>`
    }

    // Update the text of the current date element 
    // with the formatted current month and year
    currdate.innerText = `${months[month]} ${year}`;

    // update the HTML of the dates element 
    // with the generated calendar
    day.innerHTML = lit;
}

// Aktualisiere die Anzeige regelmäßig
setInterval(updateCalendarDisplay, 1000);
updateCalendarDisplay();