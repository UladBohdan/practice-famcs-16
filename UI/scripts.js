function sendMessage() {
    var msg = document.createElement('div');
    msg.classList.add('message', 'msg-my');
    var textField = document.getElementById('input-text');
    if (! textField.value)
        return;
    msg.appendChild(document.createTextNode(textField.value));
    msg.appendChild(getMsgOptions(true));
    document.getElementById('left-column').appendChild(msg);
    autoAnswer('usual');
    textField.value = '';
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

function getMsgOptions(me) {
    var msgInfo = document.createElement('div');
    msgInfo.classList.add('message-options');
    if (me)
        msgInfo.innerHTML = getTime() + ' | ' + getRemoveLabel() + ' | ' + getEditLabel();
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

function getRemoveLabel() {
    return "<a class='remLabel' onclick='removeMsg()'>remove</a>";
}

function getEditLabel() {
    return "<a class='editLabel' onclick='editMsg()'>edit</a>"
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

function removeMsg() {
    alert("removing!");
}

function editMsg() {
    alert("editing!");
}

function runDemo() {
    $.ajax({
        url : "_demo.html",
        success : function(result){
            document.getElementById('left-column').innerHTML = result;
        }
    });
}