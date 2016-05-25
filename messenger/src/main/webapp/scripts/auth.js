'use strict';

var Application = {
    rootUrl : "http://localhost:8888",
    messages : [],
    token : "TN11EN",
    author : "",
    uid: "",
    updAuthor : "",
    updUid : ""
};

function userAuthorized() {
    if (localStorageIsAvailable()) {
        return localStorage.getItem("current-uid") != null;
    } else {
        return false;
    }
}

function setAuthor() {
    if (Application.updAuthor != "" && localStorageIsAvailable()) {
        localStorage.setItem("current-author-name", Application.updAuthor);
        localStorage.setItem("current-uid", Application.updUid);
    }
    if (localStorageIsAvailable()) {
        Application.author = localStorage.getItem("current-author-name");
        Application.uid = localStorage.getItem("current-uid");
    }
}

function clearLocalData() {
    if (localStorageIsAvailable()) {
        localStorage.clear();
    }
}

function logOut() {
    clearLocalData();
    window.location = Application.rootUrl;
}

function localStorageIsAvailable() {
    if (typeof(Storage) == "undefined") {
        console.error('localStorage is not accessible');
        return false;
    }
    return true;
}
