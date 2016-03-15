var nextId = 0;

function sendMessage() {
    var msg = document.createElement('div');
    msg.classList.add('message', 'msg-my');
    msg.id = 'mymsg' + nextId;
    var textField = document.getElementById('input-text');
    if (! textField.value)
        return;
    msg.appendChild(document.createTextNode(textField.value));
    msg.appendChild(getMsgOptions(true, false));
    document.getElementById('left-column').appendChild(msg);
  //  autoAnswer('usual');
    textField.value = '';
    nextId++;
}

function autoAnswer(type) {
    var msg = document.createElement('div');
    msg.classList.add('message', 'msg-friends');
    msg.appendChild(getUserImage());
    msg.appendChild(getUsername());
    if (type != 'removed') {
        msg.appendChild(document.createTextNode('Yes, I agree with you! [AUTO ANSWERED]'));
    } else if (type == 'removed') {
        msg.appendChild(document.createTextNode('removed his/her message'));
    }
    msg.appendChild(getMsgOptions(false));
    document.getElementById('left-column').appendChild(msg);
}

function getMsgOptions(me, edited, id) {
    var msgInfo = document.createElement('div');
    msgInfo.classList.add('message-options');
    if (me)
        if (!edited)
            msgInfo.innerHTML = getTime() + ' | ' + getRemoveLabel(null) + ' | ' + getEditLabel(null);
        else
            msgInfo.innerHTML = getTime() + ' | ' + getRemoveLabel(id) + ' | ' + getEditLabel(id)
                + ' | <b>was edited</b>';
    else
        msgInfo.appendChild(document.createTextNode(getTime()));
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
        return "<a class='remLabel' onclick='removeMsg("+nextId+")'>remove</a>";
    else
        return "<a class='remLabel' onclick='removeMsg("+id+")'>remove</a>";
}

function getEditLabel(id) {
    if (id == null)
        return "<a class='editLabel' onclick='editMsg("+nextId+")'>edit</a>"
    else
        return "<a class='editLabel' onclick='editMsg("+id+")'>edit</a>"
}

function getUserImage() {
    var image = document.createElement('img');
    image.setAttribute('src', 'images/mindouh.jpg');
    image.classList.add('message-image');
    return image;
}

function getUsername() {
    var name = document.createElement('div');
    name.classList.add('message-username');
    name.appendChild(document.createTextNode('Mindouh'));
    return name;
}

function updateUsername() {
    var newName = document.getElementById('new-username-textfield');
    if (!newName.value)
        return;
    var username = document.getElementById('username');
    username.innerText = newName.value;
    newName.value = '';
}

function removeMsg(id) {
    var msg = document.getElementById('mymsg'+id);
    msg.classList.add('removed');
    msg.innerHTML = "";
    msg.appendChild(document.createTextNode("you've removed this message"));
    msg.appendChild(getMsgOptions(false));
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