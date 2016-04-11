'use strict';

function render(root) {
    document.getElementById('message-history').innerHTML = "";
    for (var i = 0; i < Application.messages.length; i++) {
        renderMessage(Application.messages[i]);
    }
}

function renderMessage(message) {
    var me = (message.author == Application.author);

    var msg = document.createElement('div');
    msg.id = message.id;
    msg.classList.add('message');
    if (me) {
        msg.classList.add('msg-my');
    } else {
        msg.classList.add('msg-friends');
        var nameDiv = document.createElement('div');
        nameDiv.classList.add('message-author');
        nameDiv.appendChild(document.createTextNode(message.author));
        msg.appendChild(nameDiv);
    }
    if (message.id == editing) {
        var area = document.createElement('textarea');
        area.classList.add('msg-edit');
        area.id = "editing-area";
        area.innerHTML = message.text;
        msg.appendChild(area);
        msg.innerHTML += "<button class='btn-edit' onclick='submitEditing(" + ('' + message.id) + ")'>Edit</button>"
        msg.innerHTML += "<button class='btn-edit' onclick='cancelEditing(" + ('' + message.id) + ")'>Cancel</button>"
        msg.innerHTML += '<br>&nbsp;';
    } else if (message.removed) {
        msg.classList.add('removed');
        msg.appendChild(document.createTextNode("message was removed"));
    } else {
        msg.appendChild(document.createTextNode(message.text));
    }
    msg.appendChild(getMsgOptions(message));
    document.getElementById('message-history').appendChild(msg);
}

function getMsgOptions(message) {
    var me = (message.author == Application.author);
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
