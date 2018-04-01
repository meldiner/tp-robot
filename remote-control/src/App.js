import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

import PubNub from 'pubnub';
import Roomba from './Roomba';

class App extends Component {
  constructor() {
    super();

    this.state = {
      straightVelocity: 20,
      turnVelocity: 10
    };

    this.pubnub = new PubNub({
      ssl: true,  // <- enable TLS Tunneling over TCP
      subscribe_key: process.env.REACT_APP_SUBSCRIBE_KEY,
      publish_key: process.env.REACT_APP_PUBLISH_KEY
    });

    this.roomba = new Roomba(message => {
      console.log('message', message);
      this.pubnub.publish({
        channel: 'robotControl',
        message
      });
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
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">TelePresence Robot Remote Control</h1>
        </header>
        <p className="App-intro">
          Use the controls below to control your robot
        </p>
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
    );
  }
}

export default App;
