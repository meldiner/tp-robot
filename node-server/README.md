# Run

```bash
npm start
```

# Environment Variables

Variable              | Description                   | Default Value
--------------------- | ------------------------------| ----------------
WS_PORT               | web socket server port number | `3000`
SERIAL_PORT_PATH      | serial port path              | `''`
NO_IP_HOSTNAME        | noip.com hostname             | `''`
NO_IP_USER            | noip.com user                 | `''`
NO_IP_PASS            | noip.com pass                 | `''`
PRIVATE_KEY_FILE_PATH | path to ssl private key file  | `../sslcert/key.pem`
CERTIFICATE_FILE_PATH | path to ssl certificate file  | `../sslcert/cert.pem`