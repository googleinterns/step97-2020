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

function init(){
    document.getElementById(elements.searchResults).style.display = "none";
}

function TestVideoObject(){
    fetch("VideoTesting").then(response => response.text()).then((testValue) => {
        console.log(testValue);
    })
}

var videoIds = ["", "", "", "", ""];

async function submitSearchQuery(){
    document.getElementById(elements.searchResults).innerHTML = "";
    document.getElementById(elements.searchLoader).style.display = "";
    var searchQuery = document.getElementById(elements.searchQuery).value;
    var servlet = "search?searchQuery=" + searchQuery;
    // Wait until all results are loaded, then display them.
    await fetch(servlet).then(response => response.json()).then((results) => {
        var img;
        var link;
        var resultsDiv = document.getElementById(elements.searchResults);
        var br = document.createElement("br");
        //videos.length is 5
        for(let i = 0; i < results.videos.length; i++){
            resultsDiv.innerHTML +=
            `<div id="result-` + i + `" class="result-flexbox">
                <img class="result-thumbnail" src="` + results.videos[i].thumbnailUrl + `">
                <div>` + results.videos[i].title + `</div>
            </div>`;
            videoIds[i] = results.videos[i].videoId;
        }
        for (let i = 0; i < results.videos.length; i ++) {
            document.getElementById("result-" + i).onclick = function() {
                searchResultClicked(videoIds[i]);
            };
        }
    });
    document.getElementById(elements.searchLoader).style.display = "none";
    document.getElementById(elements.searchResults).style.display = "block";
}

async function searchResultClicked(videoId){
    document.getElementById(elements.idField).value = videoId;
    objectId = videoId;
    isPlaylist = false;
    await submitObject();
    window.location.hash = "";
    window.location.hash = "analyze-button";
}

// Listener for searching for a video.
async function searchListener(e) {
    e.preventDefault();
    await submitSearchQuery();
};
document.getElementById(elements.searchQueryForm).addEventListener('submit', searchListener, false);

// Listener for submitting an object (video or playlist).
async function submitListener(e) {
    e.preventDefault();
    await submitObject();
};

document.getElementById(elements.submitForm).addEventListener('submit', submitListener, false);

// Submit video so that data can be stored by DataServlet.
// Returns true if sucessful.
async function submitVideo() {
    clearAnalysis();
    // Get the id value from the id field.
    let curId = document.getElementById(elements.idField).value;
    // Request to store the video in the database.
    let request = new Request("/data?videoId=" + curId, {method: "POST"});
    let response = await fetch(request);
    const responseText = await response.text();
    // Alert the user if any errors occurred.
    if (response.status >= 400) {
        alert(responseText);
        return false;
    }
    // If request is successful, set global id variable.
    objectId = curId;
    isPlaylist = false;
    playlistVideos = null;
    // Request for preview information.
    request = new Request("/data?videoId=" + objectId, {method: "GET"});
    response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return false;
    }
    // Update and show the preview with the response fields.
    video = await response.json();
    displayVideoPreview(video)
    return true;
}

// Submit playlist so that data can be stored by PlaylistServlet.
// Returns true if successful.
async function submitPlaylist() {
    clearAnalysis();
    // Make a request for the PlaylistServlet to store the playlist videos.
    curId = document.getElementById(elements.idField).value;
    const response = await fetch("/playlist?playlistId=" + curId, {method: "POST"});
    // Check if any errors occurred.
    if (response.status >= 400) {
        alert(await response.text());
        return false;
    }
    // If no errors occurred, set global object fields.
    objectId = curId;
    isPlaylist = true;
    playlistVideos = await response.json();
    // Preivew the first video..
    displayVideoPreview(playlistVideos[0]);
    return true;
}

// Submit an object to be stored (either a playlist or video).
async function submitObject() {
    // Show loader icon
    document.getElementById(elements.submitLoader).style.display = "";
    // Check if the object is a playlist and run the appropriate function.
    isPlaylist = document.getElementById(elements.isPlaylist).checked;
    successful = isPlaylist ? await submitPlaylist() : await submitVideo();
    // Hide loader icon
    document.getElementById(elements.submitLoader).style.display = "none";
    // If the load succeeded, display the analysis button.
    if (successful) {
        document.getElementById(elements.analyzeButton).style = "display: block";
    }
}

    
// Take a Video object and preview it
function displayVideoPreview(video) {
    document.getElementById(elements.thumbnailImage).src = video.thumbnailUrl;
    document.getElementById(elements.videoTitle).innerHTML = video.title;
    document.getElementById(elements.previewContainer).style.display = "block";
}

// Analyze the currently selected video object.
// Returns true if successful.
async function analyzeVideo() {
    if (isPlaylist) {
        alert("The selected item is not a video.");
        return false;
    }
    // Ensure that global id variable is set.
    if (objectId === null) {
        alert("No video selected.");
        return false;
    }
    // Post the video for analysis.
    let request = new Request("/analysis?videoId=" + objectId, {method: "POST"});
    let response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return false;
    }
    // Get the results of the analysis.
    request = new Request("/analysis?videoId=" + objectId, {method: "GET"});
    response = await fetch(request);
    // Check for errors
    if (response.status >= 400) {
        alert(await response.text());
        return false;
    }
    // Update and show the analysis with the response fields.
    videoAnalysis = await response.json();
    showVideoAnalysis(videoAnalysis);
    // Don't need to display list pane for video analysis.
    document.getElementById(elements.listPane).style.display = "none";
    return true;
}

async function analyzePlaylist() {
    if (!isPlaylist) {
        alert("The selected item is not a playlist.");
        return false;
    }
    // Ensure that global id variable is set.
    if (objectId === null) {
        alert("No playlist selected.");
        return false;
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
    // Show list pane for playlist entries.
    document.getElementById(elements.listPane).style.display = "block";
    return true;
}

// Analyze the selected object (either a video or playlist).
async function analyzeObject() {
    // Show loader icon
    document.getElementById(elements.analyzeLoader).style.display = "";
    // Run the appropriate function and store whether it was successful.
    const successful = isPlaylist ? await analyzePlaylist() : await analyzeVideo();
    // Hide loader icon
    document.getElementById(elements.analyzeLoader).style.display = "none";
    // If successful, jump to analysis.
    if (successful) {
        document.getElementById(elements.analysisSection).style.display = "flex";
        document.getElementById(elements.analysisSection).style.justifyContent = isPlaylist ? "" : "center";
        window.location.hash="";
        window.location.hash = "analysis-section";
        // Reset old search elements to prepare for new search.
        clearSearch();
    }
}

// Show the list pane given the analyses for a playlist.
function showListPane(playlistAnalyses) {
    // Add playlist entries to list pane.
    let entryCount = 0;
    for (let analysis of playlistAnalyses) {
        document.getElementById(elements.playlistEntries).innerHTML += `
        <li class="video-entry" id="entry-` + entryCount +`">
            <p>` + analysis.videoTitle + `</p>
            <img id="entry-thumbnail-` + entryCount + `" src="` + analysis.thumbnailUrl + `">
        </li>`;
        entryCount ++;
    }
    // Add onclick functions and thumbnails for entries.
    // Had to do this in a separate loop so that the onclick function would be saved.
    entryCount = 0;
    for (let analysis of playlistAnalyses) {
        document.getElementById("entry-" + entryCount).onclick = function() {
            showVideoAnalysis(analysis);
        };
        document.getElementById("entry-thumbnail-" + entryCount).style.width = "18vw";
        entryCount ++;
    }
}

// Take a VideoAnalysis object and display the results in the analysis pane.
function showVideoAnalysis(videoAnalysis) {
    document.getElementById(elements.analysisTitle).textContent = videoAnalysis.videoTitle;
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
    document.getElementById(elements.playlistEntries).innerHTML = "";
    document.getElementById(elements.analyzeButton).style.display = "none";
    document.getElementById(elements.analysisSection).style.display = "none";
}

// Reset search section.
function clearSearch() {
    document.getElementById(elements.searchResults).style.display = "none";
    document.getElementById(elements.previewContainer).style.display = "none";
    document.getElementById(elements.analyzeButton).style.display = "none";
    document.getElementById(elements.searchQuery).value = "";
    document.getElementById(elements.searchResults).innerHTML = "";
    document.getElementById(elements.idField).value = "";
}

// Functions for a button to go top of page.
// Based on W3Schools tutorial: https://www.w3schools.com/howto/howto_js_scroll_to_top.asp
window.onscroll = function(){updateTopButton()};

function updateTopButton() {
    if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
        document.getElementById(elements.topButton).style.display = "block";
    } else {
        document.getElementById(elements.topButton).style.display = "none";
    }
}

// Go back to the top of the page for a new analysis.
function goToTop() {
  document.body.scrollTop = 0;
  document.documentElement.scrollTop = 0;
}