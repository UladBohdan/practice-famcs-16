var messages = [];
var currentUsername = "";

function run() {
    messages = loadMessages() || setDefaultMessages();
    currentUsername = loadUsername() || "Username";
    document.getElementById('username').innerText = currentUsername;
    render(messages);
}

function newMessage(username, text) {
    return {
        "username": username,
        "text": text,
        "removed": false,
        "edited": false,
        "editing": false,
        "timestamp": new Date().getTime(),
        "id": "" + uniqueId()
    };
}

function uniqueId() {
    var date = Date.now();
    var random = Math.random() * Math.random();
    return Math.floor(date * random);
}

function setDefaultMessages() {
    return [
        newMessage("UladBohdan", "Message 1"),
        newMessage("UladBohdan", "Message 2"),
        newMessage("UladBohdan", "Message 3")
    ]
}

function findMessageById(id) {
    for (var i = 0; i < messages.length; i++) {
        if (messages[i].id == id)
            return messages[i];
    }
}

function render(messages) {
    while (document.getElementById('left-column').hasChildNodes()) {
        document.getElementById('left-column').removeChild(document.getElementById('left-column').firstChild);
    }
    for (var i = 0; i < messages.length; i++) {
        renderMessage(messages[i]);
    }
}

function renderMessage(message) {
    var me = (message.username == currentUsername);

    var msg = document.createElement('div');
    msg.id = message.id;
    msg.classList.add('message');
    if (me) {
        msg.classList.add('msg-my');
    } else {
        msg.classList.add('msg-friends');
        var nameDiv = document.createElement('div');
        nameDiv.classList.add('message-username');
        nameDiv.appendChild(document.createTextNode(message.username));
        msg.appendChild(nameDiv);
    }
    if (message.editing) {
        var area = document.createElement('textarea');
        area.classList.add('msg-edit');
        area.id = "area" + message.id;
        area.innerHTML = message.text;
        msg.appendChild(area);
        msg.innerHTML += "<button class='btn-edit' onclick='submitEditing(" + ('' + message.id) + ")'>Edit</button>"
        msg.innerHTML += "<button class='btn-edit' onclick='cancelEditing(" + ('' + message.id) + ")'>Cancel</button>"
        msg.innerHTML += '<br>&nbsp;';
    } else if (message.removed) {
        msg.classList.add('removed');
        msg.appendChild(document.createTextNode("you've removed this message"));
    } else {
        msg.appendChild(document.createTextNode(message.text));
    }
    msg.appendChild(getMsgOptions(message));
    document.getElementById('left-column').appendChild(msg);
}

function getMsgOptions(message) {
    var me = (message.username == currentUsername);
    var id = message.id;

    var msgInfo = document.createElement('div');
    msgInfo.classList.add('message-options');
    if (me) {
        if (message.removed) {
            msgInfo.innerHTML = getTime() + ' | ' + getRecoverLabel(id);
        } else if (message.edited) {
            msgInfo.innerHTML = getTime() + ' | ' + getRemoveLabel(id) + ' | ' + getEditLabel(id)
                + ' | <b>was edited</b>';
        } else {
            msgInfo.innerHTML = getTime() + ' | ' + getRemoveLabel(id) + ' | ' + getEditLabel(id);
        }
    } else {
        msgInfo.innerHTML = getTime();
    }
    return msgInfo;
}

function getTime() {
    var timeInMs = new Date();
    var timeStr = ' ';
    timeStr += timeInMs.getDate() + '.' + (timeInMs.getMonth() + 1) + '.' + timeInMs.getFullYear();
    timeStr += ' ' + timeInMs.getHours() + ':' + timeInMs.getMinutes();
    return timeStr;
}

function getRemoveLabel(id) {
    return "<a onclick='removeMsg(" + ('' + id) + ")'>remove</a>";
}

function getEditLabel(id) {
   return "<a onclick='editMsg(" + ('' + id) + ")'>edit</a>"
}

function getRecoverLabel(id) {
    return "<a style='color: black;' onclick='recoverMsg(" + ('' + id) + ")'>recover</a>";
}

function removeMsg(id) {
    findMessageById(id).removed = true;
    saveMessages(messages);
    render(messages);
}

function editMsg(id) {
    findMessageById(id).editing = true;
    render(messages);
}

function recoverMsg(id) {
    findMessageById(id).removed = false;
    saveMessages(messages);
    render(messages);
}

function submitEditing(id) {
    var message = findMessageById(id);
    message.editing = false;
    message.edited = true;
    message.text = document.getElementById("area" + id).value;
    render(messages);
}

function cancelEditing(id) {
    findMessageById(id).editing = false;
    render(messages);
}

function saveMessages(messages) {
    if (typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }
    localStorage.setItem("Message History", JSON.stringify(messages));
}

function loadMessages() {
    if (typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }

    var item = localStorage.getItem("Message History");

    return item && JSON.parse(item);
}

function saveUsername(username) {
    if (typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }
    localStorage.setItem("Current Username", username);
}

function loadUsername() {
    if (typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }

    return localStorage.getItem("Current Username");
}

function updateUsername() {
    var newName = document.getElementById('new-username-textfield');
    if (!newName.value)
        return;
    var username = document.getElementById('username');
    username.innerText = newName.value;
    currentUsername = newName.value;
    saveUsername(currentUsername);
    newName.value = '';
    render(messages);
}

function sendMessage() {
    var msg = newMessage(currentUsername, document.getElementById('input-text').value)
    messages.push(msg);
    saveMessages(messages);
    renderMessage(msg);
}