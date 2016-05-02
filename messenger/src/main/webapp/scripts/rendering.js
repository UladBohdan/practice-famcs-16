'use strict';

var MessageCodes = {
    REGULAR_MESSAGE_CODE : 0,
    EDITED_MESSAGE_CODE : 1,
    REMOVED_MESSAGE_CODE : 2
};

function handleResponse(responseText) {
    var response = JSON.parse(responseText);
    if (Application.token != response.token) {
        Application.token = response.token;
        var newMessagesReceived = false;
        for (var i = 0; i < response.messages.length; i++) {
            newMessagesReceived = handleMessage(response.messages[i]) || newMessagesReceived;
        }
        renderHistory();
        if (newMessagesReceived) {
            scrollMessageHistoryDown();
        }
    }
}

function handleMessage(message) {
    switch(message.code) {
        case MessageCodes.REGULAR_MESSAGE_CODE:
            message['removed'] = false;
            message['edited'] = false;
            Application.messages.push(message);
            return true;
        case MessageCodes.EDITED_MESSAGE_CODE:
            var msg = findMessageById(message.id);
            msg['edited'] = true;
            msg['text'] = message.text;
            return false;
        case MessageCodes.REMOVED_MESSAGE_CODE:
            var msg = findMessageById(message.id);
            msg['removed'] = !msg['removed'];
            return false;
    }
}

function renderHistory() {
    document.getElementById('message-history').innerHTML = "";
    //alert("Handled! in application: " + Application.messages.length);
    for (var j = 0; j < Application.messages.length; j++) {
        renderMessage(Application.messages[j]);
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
        msg.innerHTML += "<button class='btn-edit' onclick='submitEditing()'>Edit</button>"
        msg.innerHTML += "<button class='btn-edit' onclick='cancelEditing()'>Cancel</button>"
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
    return "<a onclick='removeOrRecoverMessage(" + ('' + id) + ")' style='cursor: pointer;'>remove</a>";
}

function getEditLabel(id) {
    return "<a onclick='editMessage(" + ('' + id) + ")' style='cursor: pointer;'>edit</a>"
}

function getRecoverLabel(id) {
    return "<a style='color: black; cursor: pointer;' onclick='removeOrRecoverMessage(" + ('' + id) + ")'>recover</a>";
}
