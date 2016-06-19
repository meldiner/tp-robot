"use strict";

require("file?name=hangouts-button.html!./hangouts-button.html");
require("file?name=hangouts-app.xml!./hangouts-app.xml");
require("./style.css");

window.roomba = require("./roomba.js")(new WebSocket(`wss://${process.env.SERVER_URL}`));
