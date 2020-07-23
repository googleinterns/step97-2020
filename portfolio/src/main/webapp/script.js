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

function TestVideoObject(){
    fetch("VideoTesting").then(response => response.text()).then((testValue) => {
        console.log(testValue);
    })
}

//CONSTS
const FormIDs = {
title: "video-title",
description: "video-description"
}

//The id for the most recently loaded video.
let videoId = null;

//Most recently loaded playlist.
let playlistId = null;
let playlist = null;

//Playlist analysis and index within it.
let playlistAnalyses = null;
let analysisIndex = null;

//Submit video data to the data servlet.
async function submitVideoData() {
    //Hide the old analysis.
    document.getElementById(elements.analysisContainer).style.display = "none";
    document.getElementById(elements.happyMeter).style.display="none";
    document.getElementById(elements.searchFlexbox).style.display="none";
    document.getElementById(elements.playlistControls).style.display="none";

    //Send post request with form data.
    videoId = document.getElementById(elements.videoId).value;
    const videoForm = document.getElementById(elements.videoDataForm);
    const queryString = new URLSearchParams(new FormData(videoForm)).toString();
    const request = new Request("/data?" + queryString, {method: "POST"});
    const response = await fetch(request);
    const responseText = await response.text()
    //Alert the user if any errors occurred.
    if (response.status >= 400) {
        alert(responseText);
        return;
    }
    await fetchVideoData(videoId);
    document.getElementById(elements.analyzeButton).style.display = "inline-block";
}

//This function is a GET request to our database to populate our mainpage elemetns with video information
async function fetchVideoData(videoId) {
    const response = await fetch('/data?videoId=' + videoId);
    //Alert the user and exit if errors occurred.
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
    //Otherwise, update the page with the video data.
    const videoJson = await response.json();
    document.getElementById(FormIDs.title).innerText = videoJson.title;
    document.getElementById(FormIDs.description).innerText = videoJson.description;
}

async function analyze() {
    //Check if video id is properly initialized.
    if (videoId === null) {
        alert("No video selected.");
        return;
    }
    //Post the video for analysis.
    let request = new Request("/analysis?videoId=" + videoId, {method: "POST"});
    let response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
    //Get the results of the analysis.
    request = new Request("/analysis?videoId=" + videoId, {method: "GET"});
    response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }

    //Update and show the analysis with the response fields.
    response = await response.json();
    updateAnalysis(response);
    document.getElementById(elements.analysisContainer).style.display = "block";
    document.getElementById(elements.happyMeter).style.display="inline";
    document.getElementById(elements.searchFlexbox).style.display="flex";
}

async function loadPlaylist() {
    // Hide the old analysis.
    document.getElementById(elements.titlesContainer).innerHTML = "";
    document.getElementById(elements.analysisContainer).style.display = "none";
    document.getElementById(elements.happyMeter).style.display="none";
    document.getElementById(elements.searchFlexbox).style.display="none";
    document.getElementById(elements.playlistControls).style.display="none";

    // Make a post request to store playlist videos.
    playlistId = document.getElementById(elements.playlistId).value;
    const responseJSON = await fetch("/playlist?playlistId=" + playlistId, {method: "POST"});
    const response = await responseJSON.json();
    // Check if any errors occurred.
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }

    // List titles of videos in the playlist.
    const titleContainer = document.getElementById(elements.titlesContainer);
    let count = 0;
    for (var video of response) {
        titleContainer.innerHTML += "<li>" + video.title + "</li>";
        if (++count == 5) {
            break;
        }
    }
    if (response.length > 5) {
        titleContainer.innerHTML += "<li>...</li>";
    }

    // Show analysis button.
    document.getElementById(elements.analyzePlaylistButton).style.display = "inline-block";

    // Update global playlist variables.
    playlist = response;
    playlistAnalyses = null;
    analysisIndex = null;
}

async function analyzePlaylist() {
    // Make a post request to store video analyses.
    const requests = playlist.map(video => fetch("/analysis?videoId=" + video.videoId, {method: "POST"}));
    const responses = await Promise.all(requests);
    // Fetch the analysis results.
    const analysisRequests = playlist.map(video => fetch("analysis?videoId=" + video.videoId, {method: "GET"}));
    const analysisResponses = await Promise.all(analysisRequests);
    // Update global variables so that the user scroll through analyses.
    playlistAnalyses = await Promise.all(analysisResponses.map(response => response.json()));
    analysisIndex = 0;

    // Update page elements with analysis results.
    const curAnalysis = playlistAnalyses[analysisIndex];
    updateAnalysis(curAnalysis);

    // Display the analysis results.
    document.getElementById(elements.analysisContainer).style.display = "block";
    document.getElementById(elements.happyMeter).style.display="inline";
    document.getElementById(elements.searchFlexbox).style.display="flex";
    document.getElementById(elements.playlistControls).style.display="flex";
}

function nextVideo() {
    // Check if there is a next video.
    if (analysisIndex == null) {
        alert("No playlist selected.");
        return;
    }
    if (analysisIndex == playlist.length - 1) {
        alert("No more videos.");
        return;
    }
    // Increment the analysis index and update the analysis page elements.
    const curAnalysis = playlistAnalyses[++analysisIndex];
    updateAnalysis(curAnalysis);
}

function prevVideo() {
    // Make sure that there is a previous video.
    if (analysisIndex == null) {
        alert("No playlist selected.");
        return;
    }
    if (analysisIndex == 0) {
        alert("No previous videos.");
        return;
    }
    // Decrement the analysis index and update analysis page elements.
    const curAnalysis = playlistAnalyses[--analysisIndex];
    updateAnalysis(curAnalysis);
}

// Update the analysis container with a new video analysis.
function updateAnalysis(curAnalysis) {
    document.getElementById(elements.analysisTitle).textContent = curAnalysis.videoTitle;
    document.getElementById(elements.happyMeter).value = curAnalysis.sentimentScore;
    google.search.cse.element.getElement(elements.analysisSearch).execute(curAnalysis.searchQueryString);
}