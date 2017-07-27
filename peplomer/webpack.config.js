var webpack = require('webpack');
var path = require('path');

//var BUILD_DIR = path.resolve(__dirname, '/../capsid/src/main/webapp/resources/js/built/');
//var BUILD_DIR = path.resolve(__dirname, '/');
//var APP_DIR = path.resolve(__dirname, '/');
var BUILD_DIR = __dirname;
var APP_DIR = __dirname;

var config = {

  entry: APP_DIR + '/main.js',

  output: {
    path: BUILD_DIR,
//    filename: 'bundle.js'
    filename: 'index.js'
  },
	
   devServer: {
      inline: true,
      port: 8765
   },
	
   module: {
      loaders: [
         {
            test: /\.jsx?$/,
            exclude: /node_modules/,
            loader: 'babel-loader',
				
            query: {
               presets: ['es2015', 'react']
            }
         }
      ]
   }
};

module.exports = config;

