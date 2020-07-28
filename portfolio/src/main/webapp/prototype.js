// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// The ID of the object currently being analyzed.
let objectId = null;
// Is the object a playlist?
let isPlaylist = false;
// List of videos for current playlist.
let playlistVideos = null;

// Listener for submitting a video.
function formListener(e) {
    if (document.getElementById(elements.isPlaylist).checked) {
        isPlaylist = true;
        submitPlaylist();
    } else {
        isPlaylist = false;
        submitVideo();
    }
    e.preventDefault();
};

document.getElementById(elements.searchForm).addEventListener('submit', formListener, false);

// Submit video so that data can be stored by DataServlet.
async function submitVideo() {
    clearAnalysis();
    // Get the id value from the id field.
    let curId = document.getElementById(elements.idField).value;
    // Request to store the video in the database.
    const request = new Request("/data?videoId=" + curId, {method: "POST"});
    const response = await fetch(request);
    const responseText = await response.text()
    // Alert the user if any errors occurred.
    if (response.status >= 400) {
        alert(responseText);
        return;
    }
    // If request is successful, set global id variable.
    objectId = curId;
    isPlaylist = false;
    playlistVideos = null;
    // ADD SOME PREVIEW HERE.
    document.getElementById(elements.analyzeButton).style.display = "block";
}

// Submit playlist so that data can be stored by PlaylistServlet.
async function submitPlaylist() {
    clearAnalysis();
    // Make a request for the PlaylistServlet to store the playlist videos.
    curId = document.getElementById(elements.idField).value;
    const response = await fetch("/playlist?playlistId=" + curId, {method: "POST"});
    // Check if any errors occurred.
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
    // If no errors occurred, set global object fields.
    objectId = curId;
    isPlaylist = true;
    playlistVideos = await response.json();
    // SOME PREVIEW HERE.
    document.getElementById(elements.analyzeButton).style.display = "block";
}

// Analyze the currently selected video object.
async function analyzeVideo() {
    if (isPlaylist) {
        alert("The selected item is not a video.");
        return;
    }
    // Ensure that global id variable is set.
    if (objectId === null) {
        alert("No video selected.");
        return;
    }
    // Post the video for analysis.
    let request = new Request("/analysis?videoId=" + objectId, {method: "POST"});
    let response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
    // Get the results of the analysis.
    request = new Request("/analysis?videoId=" + objectId, {method: "GET"});
    response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
    // Update and show the analysis with the response fields.
    videoAnalysis = await response.json();
    showVideoAnalysis(videoAnalysis);
    document.getElementById(elements.listPane).style.display = "none";
    document.getElementById(elements.analysisSection).style.display = "flex";
    // Jump to anlaysis section.
    window.location.hash="";
    window.location.hash = "analysis-section";
    document.getElementById(elements.idField).value = "";
    document.getElementById(elements.analyzeButton).style.display = "none";
}

async function analyzePlaylist() {
    if (!isPlaylist) {
        alert("The selected item is not a playlist.");
        return;
    }
    // Ensure that global id variable is set.
    if (objectId === null) {
        alert("No playlist selected.");
        return;
    }
    // Make a post request to store video analyses.
    const requests = playlistVideos.map(video => fetch("/analysis?videoId=" + video.videoId, {method: "POST"}));
    const responses = await Promise.all(requests);
    // Fetch the analysis results.
    const analysisRequests = playlistVideos.map(video => fetch("analysis?videoId=" + video.videoId, {method: "GET"}));
    const analysisResponses = await Promise.all(analysisRequests);
    // Save analyses in global variable so that the user can scroll through them.
    playlistAnalyses = await Promise.all(analysisResponses.map(response => response.json()));
    // Show analyses.
    showListPane(playlistAnalyses);
    showVideoAnalysis(playlistAnalyses[0]);
    document.getElementById(elements.listPane).style.display = "block";
    document.getElementById(elements.analysisSection).style.display = "flex";
    // Jump to analysis section.
    window.location.hash="";
    window.location.hash = "analysis-section";
    document.getElementById(elements.idField).value = "";
    document.getElementById(elements.analyzeButton).style.display = "none";
}

// Show the list pane given the analyses for a playlist.
function showListPane(playlistAnalyses) {
    // Add playlist entries to list pane.
    let entryCount = 0;
    for (let analysis of playlistAnalyses) {
        document.getElementById(elements.playlistEntries).innerHTML += `
        <li class="video-entry" id="entry-` + entryCount +`">
            <p>` + analysis.videoTitle + `</p>
            <img src="` + analysis.thumbnailUrl + `">
        </li>`;
        entryCount ++;
    }
    // Add onclick functions so that clicking on an entry updates the analysis pane.
    // Had to do this in a separate loop so that the onclick function would be saved.
    entryCount = 0;
    for (let analysis of playlistAnalyses) {
        document.getElementById("entry-" + entryCount).onclick = function() {
            showVideoAnalysis(analysis);
        };
        entryCount ++;
    }
}

// Take a VideoAnalysis object and display the results in the analysis pane.
function showVideoAnalysis(videoAnalysis) {
    document.getElementById(elements.analysisIframe).src = 
        "https://www.youtube.com/embed/" + videoAnalysis.videoId;
    document.getElementById(elements.happyMeter).value = videoAnalysis.sentimentScore;
    google.search.cse.element.getElement(elements.analysisSearch).execute(videoAnalysis.searchQueryString);
}

// Hides the analysis section and resets global variables.
function clearAnalysis() {
    objectId = null;
    isPlaylist = null;
    playlistVideos = null;
    document.getElementById(elements.analyzeButton).style.display = "none";
    document.getElementById(elements.analysisSection).style.display = "none";
}