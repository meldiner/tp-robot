"use strict";

module.exports = function(ws) {
  var ws = ws;

  var decToHex = function(dec) {
    var highByte = dec >> 8;
    var lowByte = dec & 255;

    highByte = (highByte < 0 ? highByte + 256 : highByte);
    lowByte = (lowByte < 0 ? lowByte + 256 : lowByte);

    return [highByte, lowByte];
  };

  var MAX_VELOCITY = 500;
  var STRAIGHT_RADIUS = decToHex(32768);

  var straightVelocity = 100;
  var turnVelocity = 50;

  var send = function(val) {
    var separator = ';';
    ws.send(val.join(separator) + separator);
  };

  var start = function() {
    send([128]);
  };

  var safeMode = function() {
    send([131]);
  };

  var fullMode = function() {
    send([132]);
  };

  var recordSong = function(songNumber, notes) {
    send([140].concat([songNumber], notes));
  };

  var playSong = function(songNumber) {
    send([141, songNumber]);
  };

  var takeControl = function() {
    start();
    safeMode();
    recordSong(0, [3, 60, 32, 65, 32, 62, 32]);
    playSong(0);
  };

  var seekDock = function() {
    send([143]);
  };

  var drive = function(velocity, radius) {
    var velocityHex = decToHex(velocity);
    var radiusHex = decToHex(radius);

    send([137, velocityHex[0], velocityHex[1], radiusHex[0], radiusHex[1]]);
  };

  var setStraightVelocity = function(percentage) {
    straightVelocity = MAX_VELOCITY * percentage / 100;
  };

  var setTurnVelocity = function(percentage) {
    turnVelocity = MAX_VELOCITY * percentage / 100;
  };

  var forward = function() {
    drive(straightVelocity, STRAIGHT_RADIUS);
  };

  var backward = function() {
    drive(-straightVelocity, STRAIGHT_RADIUS);
  };

  var left = function() {
    drive(turnVelocity, 1);
  };

  var right = function() {
    drive(turnVelocity, -1);
  };

  var stop = function() {
    drive(0, 0);
  };

  return {
    takeControl: takeControl,
    seekDock: seekDock,
    setStraightVelocity: setStraightVelocity,
    setTurnVelocity: setTurnVelocity,
    forward: forward,
    stop: stop,
    left: left,
    right: right,
    backward: backward
  }
};