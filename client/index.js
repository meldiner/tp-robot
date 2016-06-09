"use strict";

const express = require('express');
const port = process.env.PORT;

let app = express();
app.set('port', port);
app.use(express.static('public'));
app.listen(app.get('port'), () => {});