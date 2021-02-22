/**
 * @author Aimé
 * @email 
 * @create date 2021-01-27 03:30:43
 * @modify date 2021-01-27 03:30:43
 * @desc [description]
 */
let speakersCheckbox = document.getElementById('speakersCheckbox');
let lightSwitchCheckbox = document.getElementById('lightSwitchCheckbox');
let resourcePrefix = "/hab"
function concatOnRootPath(resourcePath) {
    return resourcePrefix.concat(resourcePath);
}
//source: https://coderwall.com/p/flonoa/simple-string-format-in-javascript
//date: 20200830
String.prototype.format = function () {
    let a = this;
    for (const key in arguments) {
        if (arguments.hasOwnProperty(key)) {
            const element = arguments[key];
            a = a.replace("{" + key + "}", element);
        }
    }
    return a
}
function GET(apiPath, options) {
    return fetch(concatOnRootPath(apiPath), options)
        //source: https://github.com/github/fetch/issues/203#issuecomment-335786498
        .then(response => Promise.all([response.ok, response.json(), response.headers.entries(), response]))
        .then(([responseOK, body, responseHeaderEntries, responseRaw]) => {
            return [responseOK, body, responseHeaderEntries, responseRaw];
        });
}
function setState(stateMap) {
    speakersCheckbox.checked = stateMap.speakers;
    //why !stateMap.lights?
    //because the relay is set to default ON mode
    //so when relay is on the circuit is broken
    lightSwitchCheckbox.checked = !stateMap.lights;
}
let sseOptions = {};
sseOptions.headers = {};
sseOptions.headers["Last-Event-ID"] = -1;
//why not the js built in sse lib?
//built in js sse was finicky at the time of implementation. very primitive.
let sseSource = new SSE(concatOnRootPath("/api/sse/subscribe"), sseOptions);
sseSource.addEventListener('stateMap', function (e) {
    try {
        sseOptions.headers["Last-Event-ID"] = e.id;
        let stateMap = JSON.parse(e.data);
        setState(stateMap);
    } catch (ee) {
        console.log("exception " + e.data + "\n" + ee);
    }
});
sseSource.stream();
let apiPath = "/api/state";
let options = {
    method: "GET",
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
    }
};
GET(apiPath, options)
    .then(([responseOK, body, responseHeaderEntries, responseRaw]) => {
        if (responseOK) {
            let stateMap = body;
            setState(stateMap);
        } else {
            console.log(responseRaw);
        }
    }
    );
let logoutRequest = new XMLHttpRequest();
logoutRequest.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
        window.location.href = concatOnRootPath('/index.html');
    }
};
function logout() {
    logoutRequest.open('GET', concatOnRootPath('/api/logout'));
    logoutRequest.send();
} 
let httpRequest = new XMLHttpRequest();

function speakers() {
    if (speakersCheckbox.checked === true) {
        httpRequest.open('GET', concatOnRootPath('/api/speaker/on'));
    } else {
        httpRequest.open('GET', concatOnRootPath('/api/speaker/off'));
    }
    httpRequest.send();
}
function lightSwitch() {
    if (lightSwitchCheckbox.checked === true) {
        httpRequest.open('GET', concatOnRootPath('/api/lights/off'));
    } else {
        httpRequest.open('GET', concatOnRootPath('/api/lights/on'));
    }
    httpRequest.send();
}