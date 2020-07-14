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

//Submit video data to the data servlet.
async function submitVideoData() {
    //Save video ID so that we know the video we are analyzing.
    let videoId = document.getElementById("videoId").value;
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
    //Otherwise, the response text is the key and we fetch the video data.
    await fetchVideoData(responseText);
    //Set hidden dummy ID field in analysis form.
    document.getElementById("analysisVideoId").value = videoId;
    //Show analysis button.
    document.getElementById("analysisSubmit").style.display = "inline-block";
    
}

//This function is a GET request to our database to populate our mainpage elemetns with video information
async function fetchVideoData(key) {
    const response = await fetch('/data?videoKey=' + key);
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