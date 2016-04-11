'use strict';

var Application = {
    mainUrl : "http://127.0.1.1:1555/chat",
    messages : [],
    token : "TN11EN",
    author : ""
};

var editing = null;

function run() {
    document.getElementById("input-text").focus();
    loadMessages(function(){
            render(Application);
        });
    loadAuthor();
    window.setInterval(function() {
        if (!editing) {
            loadMessages(function() {
                render(Application);
            });
        }
    }, 1000);
}

function newMessage(text) {
    return {
        "author": Application.author,
        "text": text,
        "removed": false,
        "edited": false,
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

function removeMsg(id) {
    var url = Application.mainUrl + "?msgId=" + id;
    ajax('DELETE', url, null, function(){
        findMessageById(id).removed = true;
        render(Application);
    });
}

function editMsg(id) {
    editing = id;
    render(Application);
}

function recoverMsg(id) {
    var url = Application.mainUrl + "?msgId=" + id;
    ajax('DELETE', url, null, function(){
        findMessageById(id).removed = false;
        render(Application);
    });
}

function submitEditing(id) {
    var message = findMessageById(id);
    message.edited = true;
    message.text = document.getElementById("editing-area").value;

    ajax('PUT', Application.mainUrl, JSON.stringify(message), function(){
        editing = null;
        render(Application);
    });
}

function cancelEditing(id) {
    editing = null;
    render(Application);
}

function loadMessages(done) {
    var url = Application.mainUrl + "?token=" + Application.token;

    ajax('GET', url, null, function(responseText){
        var response = JSON.parse(responseText);
        Application.messages = response.messages;
        //Application.token = response.token;
        done();
    });
}

function saveAuthor(newName) {
    if (typeof(Storage) == "undefined") {
        output('localStorage is not accessible');
        return;
    }

    localStorage.setItem("current-author-name", newName);
}

function loadAuthor() {
    if (typeof(Storage) == "undefined") {
        output('localStorage is not accessible');
        return;
    }

    Application.author = localStorage.getItem("current-author-name") || "Guest";
    document.getElementById('author').innerText = Application.author;
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
    render(Application);
}

function sendMessage() {
    var textBox = document.getElementById('input-text');
    var text = textBox.value;
    if (text == '' || text == null) {
        return;
    }
    var msg = newMessage(text);

    ajax('POST', Application.mainUrl, JSON.stringify(msg), function() {
        textBox.value = "";
        Application.messages.push(msg);
        render(Application);
    });
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
    document.getElementById("connection").innerHTML = "<i class='fa fa-spinner fa-spin' title='Connection failed'></i>";
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