// Extract parameters from the URL (may use a Servlet to fetch details later).
const urlParams = new URLSearchParams(window.location.search);
const paramNames = {
    queryText: "q",
    videoTitle: "title",
    sentimentScore: "score"
}

// Set the title of the page and sentiment based on parameters when the page loads.
function initialize() {
    // Set title text and display title.
    const titleText = "Analysis of " + urlParams.get(paramNames.videoTitle);
    const titleElement = document.getElementById("analysis-title");
    titleElement.textContent = titleText;
    titleElement.style.display = "block";

    // Set meter value and display meter.
    const meterElement = document.getElementById("happy-meter");
    meterElement.value = urlParams.get(paramNames.sentimentScore);
    document.getElementById("meter-container").style.display = "flex";
}
document.addEventListener("DOMContentLoaded", function(event) {
    initialize();
});