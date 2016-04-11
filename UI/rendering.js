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
            msgInfo.innerHTML = getTime(message) + ' | ' + getRecoverLabel(id);
        } else if (message.edited) {
            msgInfo.innerHTML = getTime(message) + ' | ' + getRemoveLabel(id) + ' | ' + getEditLabel(id)
                + ' | <b>was edited</b>';
        } else {
            msgInfo.innerHTML = getTime(message) + ' | ' + getRemoveLabel(id) + ' | ' + getEditLabel(id);
        }
    } else {
        if (message.edited) {
            msgInfo.innerHTML = getTime(message) + ' | <b>was edited</b>';
        } else {
            msgInfo.innerHTML = getTime(message);
        }
    }
    return msgInfo;
}

function getTime(message) {
    var timeInMs = new Date(message.timestamp);
    var timeStr = ' ';
    timeStr += timeInMs.getDate() + '.' + (timeInMs.getMonth() + 1) + '.' + timeInMs.getFullYear();
    timeStr += ' ' + timeInMs.getHours() + ':' + (timeInMs.getMinutes() < 10 ? "0" : "") + timeInMs.getMinutes();
    return timeStr;
}

function getRemoveLabel(id) {
    return "<a onclick='removeMsg(" + ('' + id) + ")' style='cursor: pointer;'>remove</a>";
}

function getEditLabel(id) {
    return "<a onclick='editMsg(" + ('' + id) + ")' style='cursor: pointer;'>edit</a>"
}

function getRecoverLabel(id) {
    return "<a style='color: black; cursor: pointer;' onclick='recoverMsg(" + ('' + id) + ")'>recover</a>";
}
