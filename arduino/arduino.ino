//Arduino GND to Roomba ground (pin 6)
//Arduino TX (pin 1) to Roomba RX (pin 3)

bool cmdInQueue;
byte readByte;
int cmd;

void initQueue() {
  cmdInQueue = false;
  cmd = 0;
}

void sendToRoomba(int val) {
  Serial.write(val);
  delay(5);
}

void setup() {
  initQueue();       
  Serial.begin(115200);
}

void loop() {
  //check if data has been sent from the computer
  if (Serial.available()) {
    //read the next byte 
    readByte = Serial.read();

    // verify input
    if (('0' <= readByte && readByte <= '9') || readByte == ';') {
      //check if the read byte is ';'
      if (readByte == ';') {
        if (cmdInQueue) {
          sendToRoomba(cmd);
        }
        initQueue();
      } else {
        cmdInQueue = true;
        cmd = 10*cmd + (readByte - '0');
      }
    } else {
      // illegal input
      // initQueue and continue
      initQueue();
    }
  }
}
