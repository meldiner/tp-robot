"use strict";

const SerialPort = require('serialport').SerialPort;

const portName = '/dev/cu.usbmodem1411';

let port = new SerialPort(portName, {
  baudRate: 115200
});

let start = '128;';
let setupSong0 = '132;140;0;3;62;32;63;32;62;32;';
let setupSong1 = '132;140;1;3;61;64;63;32;62;32;';
let playSong0 = '141;0;';
let playSong1 = '141;1;';

port.on('open', function() {
  setTimeout(function() {
    port.write(start + setupSong0 + setupSong1 + playSong0 + playSong1, function() {
      console.log("done writing to port");
    });
  }, 4000);
});
