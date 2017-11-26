const webpack = require('webpack');
const path = require('path');
const StringReplacePlugin = require("string-replace-webpack-plugin");

module.exports = {
  entry: "./src/index.js",
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: "bundle.js"
  },
  module: {
    loaders: [{
      test: /\.css$/,
      loader: "style!css"
    }, {
      test: /$/,
      loader: StringReplacePlugin.replace({
        replacements: [{
          pattern: 'process.env.CLIENT_URL',
          replacement: () => {return process.env.CLIENT_URL || 'localhost'}
        }]
      })
    }]
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        PUBLISH_KEY: JSON.stringify(process.env.PUBLISH_KEY) || '',
        SUBSCRIBE_KEY: JSON.stringify(process.env.SUBSCRIBE_KEY) || ''
      }
    }),
    new StringReplacePlugin()
  ]
};