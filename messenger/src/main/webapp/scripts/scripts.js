'use strict';

var Application = {
    mainUrl : "http://localhost:8888/chat",
    messages : [],
    token : "TN11EN",
    author : ""
};

var editing = null;

function run() {
    document.getElementById("input-text").focus();
    updateState();
    loadAuthor();
    /*window.setInterval(function() {
        if (!editing) {
            updateState();
        }
    }, 1000);*/
}

function newMessage(text) {
    return {
        "author": Application.author,
        "text": text,
        "timestamp": new Date().getTime(),
        "id": "" + uniqueId()
    };
}

function uniqueId() {
    var date = Date.now();
    var random = Math.random() * Math.random();
    return Math.floor(date * random);
}

function findMessageById(id) {
    for (var i = 0; i < Application.messages.length; i++) {
        if (Application.messages[i].id == id)
            return Application.messages[i];
    }
}

function sendMessage() {
    var textBox = document.getElementById('input-text');
    var text = textBox.value;
    if (text == '' || text == null) {
        return;
    }
    var msg = newMessage(text);

    ajax('POST', getUrlWithToken(), JSON.stringify(msg), function(responseText) {
        textBox.value = "";
        handleResponse(responseText);
    });
}

function removeOrRecoverMessage(id) {
    var url = getUrlWithToken() + "&msgId=" + id;
    ajax('DELETE', url, null, function(responseText) {
        handleResponse(responseText);
    });
}

function editMessage(id) {
    editing = id;
    renderHistory();
}

function cancelEditing() {
    editing = null;
    renderHistory();
}

function submitEditing() {
    var message = findMessageById(editing);
    message.text = document.getElementById("editing-area").value;

    ajax('PUT', getUrlWithToken(), JSON.stringify(message), function(responseText) {
        editing = null;
        handleResponse(responseText);
    });
}

function updateState() {
    ajax('GET', getUrlWithToken(), null, function(responseText) {
        handleResponse(responseText);
    });
}

function getUrlWithToken() {
    return Application.mainUrl + "?token=" + Application.token + "&uid=1";
}

function saveAuthor(newName) {
    if (localStorageIsAvailable()) {
        localStorage.setItem("current-author-name", newName);
    }
}

function loadAuthor() {
    if (localStorageIsAvailable()) {
        Application.author = localStorage.getItem("current-author-name") || "Guest";
        document.getElementById('author').innerText = Application.author;
    }
}

function localStorageIsAvailable() {
    if (typeof(Storage) == "undefined") {
        console.error('localStorage is not accessible');
        return false;
    }
    return true;
}

function updateAuthorName() {
    var newName = document.getElementById('new-author-textfield');
    if (newName.value == '' || newName == null) {
        return;
    }
    var authorName = document.getElementById('author');
    authorName.innerHTML = newName.value;
    Application.author = newName.value;
    saveAuthor(newName.value);
    newName.value = '';
    renderHistory();
}

function scrollMessageHistoryDown() {
    window.scrollTo(0,document.body.scrollHeight);
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;

        if(xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if(isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }

        document.getElementById("connection").innerHTML = "<i class='fa fa-check' title='Connected'></i>";
        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        continueWithError('Server timed out !');
    };

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n'+
            '\n' +
            'Check if \n'+
            '- server is active\n'+
            '- server sends header "Access-Control-Allow-Origin:*"\n'+
            '- server sends header "Access-Control-Allow-Methods: PUT, DELETE, POST, GET, OPTIONS"\n';

        continueWithError(errMsg);
    };

    xhr.send(data);
}

function defaultErrorHandler(message) {
    console.error(message);
    document.getElementById("connection").innerHTML = "<i class='fa fa-refresh fa-spin' title='Connection failed'></i>";
}

function isError(text) {
    if(text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch(ex) {
        return true;
    }

    return !!obj.error;
}