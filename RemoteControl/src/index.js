"use strict";

require("./style.css");

window.pubnub = require("pubnub")({
  ssl           : true,  // <- enable TLS Tunneling over TCP
  publish_key   : process.env.PUBLISH_KEY,
  subscribe_key : process.env.SUBSCRIBE_KEY
});

function send(message) {
  window.pubnub.publish({
    channel: 'roomba',
    message: message
  });
}

window.roomba = require("./roomba.js")(send);