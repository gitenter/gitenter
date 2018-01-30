# ReactJS Notes

## Basics

`package.json` said we should start the application from hot generated file `main` of `index.js`.

`webpack.config.js` said `entry:` of `index.js` is hot generated from `output:` of `main.js`.

`index.html` is found because it has the same name as `index.js`??

`App.jsx` because `main.js` has `import App from './App.jsx';`.

`class App extends React.Component` because `main.js` has `ReactDOM.render(<App />, document.getElementById('app'));`, which later linked to `<div id = "app"></div>` in `index.html`.

More general, in `.jsx` `<ComponentName/>` will render `class ComponentName extends React.Component`

#### `state`

When need to use **mutable** `json` data, put them into JavaScript variable `this.state` (`state` is the place where the data comes from). If using multiple data component, put them all in the `this.state` variable with different key.

`state` is initialized in constructor: `this.state = {...}`

```
constructor() {
  super();
  this.state = {...}
}
```

`state` can be changed. You always want to bind `xxxHandler()` (`setStateHandler()`, `forceUpdateHandler()`, `findDomNodeHandler()` ...) to the constructor like

```
constructor() {
  ...
  this.xxxHandler = this.xxxHandler.bind(this);
}
```

(1) `setState()` not replace the state but only add changes

```
setStateHandler() {
  ...
}
```

then call `setStateHandler` to make the change, e.g., `<button onClick = {this.setStateHandler}>`

(2) `forceUpdate()` for manual update: define `forceUpdateHandler()` to do only `this.forceUpdate();`. Then when `forceUpdateHandler()` is executed, all variables are force updated.

(3) `ReactDOM.findDOMNode()`

`findDomNodeHandler()` parse HTML tags, and change its attribute, e.g.

```
findDomNodeHandler() {
  var myDiv = document.getElementById('myDiv');
  ReactDOM.findDOMNode(myDiv).style.color = 'green';
}
```

for `<div id = "myDiv">NODE</div>`

#### `props`

##### Setup

When need to use **immutable** data, put them in (1) attribute `<ComponentName attributeProp="..." />`, or (2) use `ComponentName.defaultProps` variable in `class ComponentName`, or (3) use `state` in parent component and passing it down the component tree using `props`.

For (3), there are multiple ways:

(a) said `props` in `ParentComponent` constructor.

```
constructor(props) {
  super(props);
  ...
}
```

and render define attribute `<ParentComponent attributeProp="..." />`. then in `class ChildComponent` uses it by `this.props.attributeProp`.

(b) Link a `this.state` array component (here `this.state.thisStateKeyName`) to a user defined class using EcmaScript 2015 `=>` notation

```
{this.state.data.map((dummyDataName, dummyKeyName) => <UserDefinedComponent key = {dummyKeyName} thisStateKeyName = {dummyDataName} />)}
```

Then define `class UserDefinedComponent extends React.Component` which uses `this.props` (`props` is immutable) to render the data.

##### Validation

Since `props` are immutable, they can be validated. Define `ComponentName.propTypes` variable to set them up.

#### (Component) Lifecycle management

Can overwrite `componentXxx()` methods in `class Xxx extends React.Component` so they'll be executed in desired time.

## Associated JavaScript Modules for using

`webpack`: associated with the definition of `webpack.config.js` which is used to setup the server, port, ...

`babel`: for the transpiler to provide both JSX and ES6 support all at once.

`axios`: For query data from other site any put it into JavaScript variable. [Tutorial](https://github.com/axios/axios)...

## Working together with Spring (Boot)

#### Setup

One possible way is to use `frontend-maven-plugin`, then the JavaScript and Java parts are under the same IP/port.

Or you can set them up as two completely different projects, with Spring runs in Tomcat to one URL/port and npm running to another URL/port. In that case, CORS need to be setup.

## References

1. [ReactJS Tutorial](https://www.tutorialspoint.com/reactjs/) in tutorialspoint.
1. [React.js and Spring Data REST](https://spring.io/guides/tutorials/react-and-spring-data-rest/).

