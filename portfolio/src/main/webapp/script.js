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
  description: "video-description"
}

//This function is a GET request to our database to populate our mainpage elemetns with video information
async function fetchVideoData(id) {
  fetch('/data?videoId=' + id) 
    .then(response => response.json()) // parses the response as JSON
    .then((videoJson) => { // now we can reference the fields in myObject!
    document.getElementById(FormIDs.title).innerText = videoJson.title
    document.getElementById(FormIDs.description).innerText = videoJson.description;
  });
}

//This event listener will listen for when the page loads
window.addEventListener("load", myInit, true); function myInit(){
  //When the page loads check for URL parameters
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const id = urlParams.get('videoId')
    //If the id exists
    if (id !== null) {
        //If the id is empty, alert and redirect to main page.
        if (id === "") {
            alert("No ID Selected!");
            window.location.replace('/');
            return;
        } else {
            //if the ID is valid we do a GET request to our database
            fetchVideoData(id)
        }
    }
}