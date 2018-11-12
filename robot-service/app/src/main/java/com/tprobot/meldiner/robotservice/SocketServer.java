package com.tprobot.meldiner.robotservice;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


public class SocketServer extends WebSocketServer
{
    IMessageHandler handler;

    public SocketServer(InetSocketAddress address, IMessageHandler handler) {
        super(address);

        this.handler = handler;
    }

    @Override
    public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
    }

    @Override
    public void onError(WebSocket arg0, Exception arg1) {
        System.out.println(arg1.getStackTrace());
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onMessage(WebSocket arg0, String arg1) {
        System.out.println("new message: " + arg1);

        this.handler.handle(arg1);
    }

    @Override
    public void onOpen(WebSocket arg0, ClientHandshake arg1) {
        System.out.println("new connection to " + arg0.getRemoteSocketAddress());
    }
}
