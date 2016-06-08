"use strict";

const WebSocketServer = require('ws').Server;
const SerialPort = require('serialport').SerialPort;

const portName = '/dev/cu.usbmodem1411';

let port = new SerialPort(portName, {
  baudRate: 115200
});

port.on('open', portOpen);
port.on('data', portData);
port.on('close', portClose);
port.on('error', portError);

function portOpen() {
  console.log('port open');
}

function portData(data) {
  console.log('data received: ' + data);

  if (data == 'ready') {
    console.log('port is ready');
  }
}

function portClose() {
  console.log('port close');
}

function portError(error) {
  console.log('port error:' + error);
}

let ws = new WebSocketServer({port: 3000});
let connections = new Array;

ws.on('connection', wsConnection);

function wsConnection(client) {
  console.log("new socket connection");

  connections.push(client);

  client.on('message', wsMessage);
  client.on('close', wsClose);
}

function wsMessage(msg) {
  console.log('socket message received: ' + msg);

  port.write(msg);
}

function wsClose() {
  console.log("socket connection closed");

  let position = connections.indexOf(this);
  connections.splice(position, 1); // delete connection from the array
}