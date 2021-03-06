import React, { Component } from 'react';
import './App.css';

// import PubNub from 'pubnub';
import Roomba from './Roomba';

class App extends Component {
  constructor() {
    super();

    this.state = {
      straightVelocity: 20,
      turnVelocity: 10
    };

    // this.pubnub = new PubNub({
    //   ssl: true,  // <- enable TLS Tunneling over TCP
    //   subscribe_key: process.env.REACT_APP_SUBSCRIBE_KEY,
    //   publish_key: process.env.REACT_APP_PUBLISH_KEY
    // });

    const ip = "192.168.29.127:38301";
    this.ws = new WebSocket("ws://" + ip);
    this.ws.onopen = function()
    {
         alert("web socket connected!");
    };


    this.roomba = new Roomba(message => {
      console.log('message', message);
      // this.pubnub.publish({
      //   channel: 'robotControl',
      //   message
      // });
      this.ws.send(message);
    });
  }

  handleStraightVelocityChange = event => {
    this.setState({straightVelocity: event.target.value});
    this.roomba.setStraightVelocity(event.target.value);
  }

  handleTurnVelocityChange = event => {
    this.setState({turnVelocity: event.target.value});
    this.roomba.setTurnVelocity(event.target.value);
  }

  render() {
    return (
      <div className="App">
        <div className="Video">
          <iframe src="https://appear.in/tp-ro" width="800" height="640" frameborder="0"></iframe>
        </div>
        <div className="RemoteControl">
          <div>
              <button onClick={this.roomba.takeControl}>Take Control</button>
              <button onClick={this.roomba.seekDock}>Seek Dock</button>
          </div>

          <div>
              <p>Straight Velocity:</p><input type="range" value={this.state.straightVelocity} onChange={this.handleStraightVelocityChange}></input>
              <p>Turn Velocity:</p><input type="range" value={this.state.turnVelocity} onChange={this.handleTurnVelocityChange}></input>
          </div>

          <div>
              <button onMouseDown={this.roomba.forward} onMouseUp={this.roomba.stop}>Forward</button>
              <br />
              <button onMouseDown={this.roomba.left} onMouseUp={this.roomba.stop}>Left</button>
              <button onMouseDown={this.roomba.stop}>Stop</button>
              <button onMouseDown={this.roomba.right} onMouseUp={this.roomba.stop}>Right</button>
              <br />
              <button onMouseDown={this.roomba.backward} onMouseUp={this.roomba.stop}>Backward</button>
          </div>
        </div>
      </div>
    );
  }
}

export default App;
