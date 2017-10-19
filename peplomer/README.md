## Development environment setting

### React.js

The default Node.js version in both Ubuntu 14.04 and 16.04 are too old (in 14.04 is v0.10.25, and in 16.04 is v4.2.6). Use [this script](https://github.com/nodesource/distributions#debinstall) to install the newest version.

```
$ curl -sL https://deb.nodesource.com/setup_8.x | sudo -E bash -
$ sudo apt-get install -y nodejs
$ nodejs -v
v8.2.1
$ npm -v
5.3.0
```

Then install `babel-cli` through `npm` globally.

```
$ sudo npm install -g babel
$ sudo npm install -g babel-cli
```

Install various dependencies

```
$ npm install webpack --save
$ npm install webpack-dev-server --save
$ npm install react --save
$ npm install react-dom --save
$ npm install babel-core
$ npm install babel-loader
$ npm install babel-preset-react
$ sudo npm install babel-preset-es2015 // Seems this one needs sudo. Not exactly sure about others.
```

Current `npm start` can run HelloWorld, and the site is in `http://localhost:8765/`. But currently the backend is Node.js rather than Spring.

May need to use `sudo fuser 8765/tcp -k` to close the server.
