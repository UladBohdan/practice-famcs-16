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
    if (message.removed) {
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
        if (message.edited) {
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
    timeStr += timeInMs.getDate() + '.' + (timeInMs.getMonth()+1) + '.' + timeInMs.getFullYear();
    timeStr += ' ' + timeInMs.getHours() + ':' + timeInMs.getMinutes();
    return timeStr;
}

function getRemoveLabel(id) {
    if (id == null)
        return "<a class='remLabel' onclick='removeMsg("+id+")'>remove</a>";
    else
        return "<a class='remLabel' onclick='removeMsg("+id+")'>remove</a>";
}

function getEditLabel(id) {
    if (id == null)
        return "<a class='editLabel' onclick='editMsg("+id+")'>edit</a>"
    else
        return "<a class='editLabel' onclick='editMsg("+id+")'>edit</a>"
}

function removeMsg(id) {
    for (var i = 0; i < messages.length; i++) {
        if (messages[i].id == id) {
            messages[i].removed = true;
        }
    }
    saveMessages(messages);
    render(messages);
}

function editMsg(id) {
    var msg = document.getElementById('mymsg'+id);
    msg.removeChild(msg.childNodes[1]);
    var text = msg.innerText;
    msg.innerHTML = "";
    var area = document.createElement('textarea');
    area.classList.add('msg-edit');
    area.id = "area" + id;
    area.innerHTML = text;
    msg.appendChild(area);
    msg.innerHTML += "<button class='btn-edit' onclick='submitEditing("+id+")'>Edit</button>"
    msg.innerHTML += '<br>&nbsp;';
    msg.appendChild(getMsgOptions(false));
}

function submitEditing(id) {
    var msg = document.getElementById("mymsg" + id);

    var newText = document.getElementById("area" + id).value;
    msg.innerHTML = "";
    msg.appendChild(document.createTextNode(newText));
    msg.appendChild(getMsgOptions(true, true, id));
}



function saveMessages(messages) {
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }
    localStorage.setItem("Message History", JSON.stringify(messages));
}

function loadMessages() {
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }

    var item = localStorage.getItem("Message History");

    return item && JSON.parse(item);
}

function saveUsername(username) {
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }
    localStorage.setItem("Current Username", username);
}

function loadUsername() {
    if(typeof(Storage) == "undefined") {
        alert('localStorage is not accessible');
        return;
    }

    var item = localStorage.getItem("Current Username");

    return item;
}

function updateUsername() {
    var newName = document.getElementById('new-username-textfield').value;
    if (!newName)
        return;
    var username = document.getElementById('username');
    username.innerText = newName;
    saveUsername(newName);
    newName.value = '';
}

function sendMessage() {
    var msg = newMessage(currentUsername, document.getElementById('input-text').value)
    messages.push(msg);
    saveMessages(messages);
    renderMessage(msg);
}