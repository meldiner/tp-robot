"use strict";

var MAX_VELOCITY = 500;
var STRAIGHT_RADIUS = decToHex(32768);

var straightVelocity = 100;
var turnVelocity = 50;

var socket = new WebSocket("ws://localhost:3000");

function send(val) {
  var separator = ';';
  socket.send(val.join(separator) + separator);
}

function decToHex(dec) {
  var highByte = dec >> 8;
  var lowByte = dec & 255;

  highByte = (highByte < 0 ? highByte + 256 : highByte);
  lowByte = (lowByte < 0 ? lowByte + 256 : lowByte);

  return [highByte, lowByte];
}

function start() {
  send([128]);
}

function safeMode() {
  send([131]);
}

function fullMode() {
  send([132]);
}

function recordSong(songNumber, notes) {
  send([140].concat([songNumber], notes));
}

function playSong(songNumber) {
  send([141, songNumber]);
}

function takeControl() {
  start();
  safeMode();
  recordSong(0,[3,60,32,65,32,62,32]);
  playSong(0);
}

function seekDock() {
  send([143]);
}

function drive(velocity, radius) {
  var velocityHex = decToHex(velocity);
  var radiusHex = decToHex(radius);

  send([137, velocityHex[0], velocityHex[1], radiusHex[0], radiusHex[1]]);
}

function setStraightVelocity(percentage) {
  straightVelocity = MAX_VELOCITY * percentage / 100;
}

function setTurnVelocity(percentage) {
  turnVelocity = MAX_VELOCITY * percentage / 100;
}

function forward() {
  drive(straightVelocity, STRAIGHT_RADIUS);
}

function backward() {
  drive(-straightVelocity, STRAIGHT_RADIUS);
}

function left() {
  drive(turnVelocity, 1);
}

function right() {
  drive(turnVelocity, -1);
}

function stop() {
  drive(0,0);
}