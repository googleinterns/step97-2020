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

//CONSTS
const FormIDs = {
  title: "video-title",
  description: "video-description",
  videoKeyHolder: "video-key-holder"
}

// Save video key for the current video the user has previewed.
let videoKey = null;

//Submit video data to the data servlet.
async function submitVideoData() {

    //Hide the old analysis.
    document.getElementById("analysis-container").style.display = "none";
    document.getElementById("happy-meter").style.display="none";
    document.getElementById("search-flexbox").style.display="none";
    //Send post request with form data.
    const videoForm = document.getElementById("video-data-form");
    const queryString = new URLSearchParams(new FormData(videoForm)).toString();
    const request = new Request("/data?" + queryString, {method: "POST"});
    const response = await fetch(request);
    const responseText = await response.text()
    //Alert the user if any errors occurred.
    if (response.status >= 400) {
        alert(responseText);
        return;
    }
<<<<<<< HEAD
    //Otherwise, the response text is the key and we fetch the video data.
    videoKey = responseText;
    await fetchVideoData(videoKey);
    document.getElementById("analyze-button").style.display = "inline-block";

async function submitVideoForAnalysis() {
    //get the video key from our URL
    let videoKey = document.getElementById(FormIDs.videoKeyHolder).value;
    const request = new Request("/analysis?videoKey=" + videoKey, {method: "POST"});
    const response = await fetch(request);
    const responseText = await response.text()
    //Alert the user if any errors occurred.
    if (response.status >= 400) {
        alert(responseText);
        return;
    }
    //Otherwise, the response text is the key and we fetch the video data.
    await fetchVideoData(responseText);
}

=======
    //Otherwise, the response text is the key and we fetch the video data.
    await fetchVideoData(responseText);
    //Set hidden dummy ID field in analysis form.
    document.getElementById("analysisVideoId").value = videoId;
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
    //Otherwise, update the page with the video data.
    const videoJson = await response.json();
    document.getElementById(FormIDs.videoKeyHolder).value = key;
    document.getElementById(FormIDs.title).innerText = videoJson.title;
    document.getElementById(FormIDs.description).innerText = videoJson.description;
}

<<<<<<< HEAD
async function analyze() {
    //Check if video key is properly initialized.
    if (videoKey === null) {
        alert("No video selected.");
        return;
    }
    //Post the video for analysis.
    let request = new Request("/analysis?videoKey=" + videoKey, {method: "POST"});
    let response = await fetch(request);
=======
async function fetchAnalysisResults(key) {
    const response = await fetch('/analysis?videoKey=' + key);
    //Alert the user and exit if errors occurred.
>>>>>>> 0936079314871ba93240b947639c2f0fcb12a129
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }
<<<<<<< HEAD
    //Get the results of the analysis.
    request = new Request("/analysis?videoKey=" + videoKey, {method: "GET"});
    response = await fetch(request);
    if (response.status >= 400) {
        alert(await response.text());
        return;
    }

    //Update and show the analysis with the response fields.
    const responseJson = await response.json();
    document.getElementById("happy-meter").value = responseJson.sentimentScore;
    google.search.cse.element.getElement("analysis-search").execute(responseJson.searchQueryString);
    document.getElementById("analysis-container").style.display = "block";
    document.getElementById("happy-meter").style.display="inline";
    document.getElementById("search-flexbox").style.display="flex";
=======
    //Otherwise, update the page with the video data.
    const analysisJson = await response.json();
>>>>>>> 0936079314871ba93240b947639c2f0fcb12a129
}