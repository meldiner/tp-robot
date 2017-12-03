class Roomba {
  constructor(sendFunc) {
    this.sendFunc = sendFunc;
    this.straightVelocity = 100;
    this.turnVelocity = 50;

    this.MAX_VELOCITY = 500;
    this.STRAIGHT_RADIUS = this.decToHex(32768);
  }
  
  decToHex = dec => {
    let highByte = dec >> 8;
    let lowByte = dec & 255;

    highByte = (highByte < 0 ? highByte + 256 : highByte);
    lowByte = (lowByte < 0 ? lowByte + 256 : lowByte);

    return [highByte, lowByte];
  }
  
  send = val => {
    const separator = ';';
    this.sendFunc(val.join(separator) + separator);
  }
  
  start = () => {
    this.send([128]);
  }
  
  safeMode = () => {
    this.send([131]);
  }
  
  fullMode = () => {
    this.send([132]);
  }
  
  recordSong = (songNumber, notes) => {
    this.send([140].concat([songNumber], notes));
  }
  
  playSong = songNumber => {
    this.send([141, songNumber]);
  }
  
  takeControl = () => {
    this.start();
    this.safeMode();
    this.recordSong(0, [3, 60, 32, 65, 32, 62, 32]);
    this.playSong(0);
  }
  
  seekDock = () => {
    this.send([143]);
  }
  
  drive = (velocity, radius) => {
    const velocityHex = this.decToHex(velocity);
    const radiusHex = this.decToHex(radius);

    this.send([137, velocityHex[0], velocityHex[1], radiusHex[0], radiusHex[1]]);
  }
  
  setStraightVelocity = percentage => {
    this.straightVelocity = this.MAX_VELOCITY * percentage / 100;
  }
  
  setTurnVelocity = percentage => {
    this.turnVelocity = this.MAX_VELOCITY * percentage / 100;
  }
  
  forward = () => {
    this.drive(this.straightVelocity, this.STRAIGHT_RADIUS);
  }
  
  backward = () => {
    this.drive(-this.straightVelocity, this.STRAIGHT_RADIUS);
  }
  
  left = () => {
    this.drive(this.turnVelocity, 1);
  }
  
  right = () => {
    this.drive(this.turnVelocity, -1);
  }
  
  stop = () => {
    this.drive(0, 0);
  }
}

export default Roomba;