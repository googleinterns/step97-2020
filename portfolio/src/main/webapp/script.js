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

/**
 * Adds a random greeting to the page.
 */
async function fetchVideoData(id) {
  fetch('/data?videoId=' + id)  // sends a request to /data URl with a cursor and whether we want next page or not
.then(response => response.json()) // parses the response as JSON
.then((videoJson) => { // now we can reference the fields in myObject!
    document.getElementById("video-title").innerText = videoJson.title
    document.getElementById("video-description").innerText = videoJson.description;
});
}

window.addEventListener("load", myInit, true); function myInit(){
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const id = urlParams.get('videoId')
  if(id === "") {
    alert("No ID Selected!");
    window.location.replace('/');
    return;
  } else {
    fetchVideoData(id)
  }
}