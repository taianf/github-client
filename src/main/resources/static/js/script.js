/**
 * Copyright 2018, Google LLC
 * Licensed under the Apache License, Version 2.0 (the `License`);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an `AS IS` BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

window.addEventListener('load', function () {
  var firebaseConfig = {
    apiKey: "AIzaSyAU0EMBpxyD6UAzMYe3dwichHX3Z1TW01M",
    authDomain: "github-client-289022.firebaseapp.com",
    databaseURL: "https://github-client-289022.firebaseio.com",
    projectId: "github-client-289022",
    storageBucket: "github-client-289022.appspot.com",
    messagingSenderId: "684668644537",
    appId: "1:684668644537:web:c2830e4468d99888b17f68",
    measurementId: "G-GC5BR13C9L"
  };
  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);
  //  firebase.analytics();
  var provider = new firebase.auth.GithubAuthProvider();
  provider.addScope('repo');
  var signInButton = document.getElementById("sign-in");
  var loadingDiv = document.getElementById("loading-div");
  if (signInButton !== null) {
    signInButton.onclick = function () {
      firebase.auth().signInWithRedirect(provider);
    };
  }
  firebase.auth().getRedirectResult().then(function (result) {
    console.log("result");
    console.log(result);
    var githubToken;
    if (result.credential) {
      // This gives you a GitHub Access Token. You can use it to access the GitHub API.
      githubToken = result.credential.accessToken;
      console.log("githubToken:");
      console.log(githubToken);
      // ...
    }
    // The signed-in user info.
    var user = result.user;
    console.log("user:");
    console.log(user);
    if (user) {
      user.getIdToken(true).then(function (firebaseToken) {
        console.log("firebaseToken:");
        console.log(firebaseToken);
        document.cookie = "githubToken=" + githubToken;
        document.cookie = "firebaseToken=" + firebaseToken;
        location.reload();
      });
    } else {
      if (signInButton !== null) {
        signInButton.hidden = false;
      }
      if (loadingDiv !== null) {
        loadingDiv.hidden = true;
      }
    }
  }).catch(function (error) {
    console.log(error);
  });
  var signOutButton = document.getElementById("sign-out");
  if (signOutButton !== null) {
    signOutButton.onclick = function () {
      firebase.auth().signOut();
      location.href = "/logout";
    };
  }

  if (typeof firebase === 'undefined') {
    const msg = "Please paste the Firebase initialization snippet into index.html. See https://console.firebase.google.com > Overview > Add Firebase to your web app.";
    console.log(msg);
    alert(msg);
  }

});