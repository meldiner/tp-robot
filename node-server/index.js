"use strict";

const fs = require('fs');
const https = require('https');
const express = require('express');

const WebSocketServer = require('ws').Server;
const SerialPort = require('serialport').SerialPort;
const NoIpUpdater = require('noip-updater');

const wsPort = process.env.WS_PORT || 3000;
const serialPortPath = process.env.SERIAL_PORT_PATH;
const noIpHostname = process.env.NO_IP_HOSTNAME;
const noIpUser = process.env.NO_IP_USER;
const noIpPass = process.env.NO_IP_PASS;
const privateKeyFilePath = process.env.PRIVATE_KEY_FILE_PATH || '../sslcert/key.pem';
const certificateFilePath = process.env.CERTIFICATE_FILE_PATH || '../sslcert/cert.pem';

const privateKey  = fs.readFileSync(privateKeyFilePath, 'utf8');
const certificate = fs.readFileSync(certificateFilePath, 'utf8');
const credentials = {key: privateKey, cert: certificate};

let app = express();
let httpsServer = https.createServer(credentials, app);
httpsServer.listen(wsPort);

let port = new SerialPort(serialPortPath, {baudRate: 115200});
let ws = new WebSocketServer({server: httpsServer});
let connections = new Array;

port.on('open', () => {
  console.log('port open');
});
port.on('close', () => {
  console.log('port close');
});
port.on('error', error => {
  console.log('port error:' + error);
});
port.on('data', data => {
  console.log('data received: ' + data);
  if (data == 'ready') {
    console.log('port is ready');
  }
});

ws.on('connection', client => {
  console.log("new socket connection");
  connections.push(client);

  client.on('message', msg => {
    console.log('socket message received: ' + msg);
    port.write(msg);
  });

  client.on('close', () => {
    console.log("socket connection closed");
    let position = connections.indexOf(this);
    connections.splice(position, 1); // delete connection from the array
  });
});

NoIpUpdater.getPublicIP(function(ip) {
  NoIpUpdater.updateNoIP(noIpUser, noIpPass, noIpHostname, ip, false, function(body, response, error) {
    console.log('response from no-ip: ' + body);
  });
});