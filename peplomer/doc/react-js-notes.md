# ReactJS Notes

`package.json` said we should start the application from hot generated file `main` of `index.js`.

`webpack.config.js` said `entry:` of `index.js` is hot generated from `output:` of `main.js`.

`index.html` is found because it has the same name as `index.js`??

`App.jsx` because `main.js` has `import App from './App.jsx';`.

`class App extends React.Component` because `main.js` has `ReactDOM.render(<App />, document.getElementById('app'));`, which later linked to `<div id = "app"></div>` in `index.html`.

More general, in `.jsx` `<ComponentName/>` will render `class ComponentName extends React.Component`

-------------------

When need to use **mutable** `json` data, put them into JavaScript variable `this.state` (`state` is the place where the data comes from). If using multiple data component, put them all in the `this.state` variable with different key.

When need to use **immutable** data, put them in (1) attribute `<ComponentName attributeProp="..." />`, or (2) use `ComponentName.defaultProps` in `class ComponentName`, or (3) use `state` in parent component and passing it down the component tree using `props`.

For (3), there are multiple ways:

(a) said `props` in `ParentComponent` constructor.

```
constructor(props) {
  super(props);
  this.state = {...}
}
```

and render define attribute `<ComponentName attributeProp="..." />`. then in `class ChildComponent` uses it by `this.props.attributeProp`.

(b) Link a `this.state` array component (here `this.state.thisStateKeyName`) to a user defined class using EcmaScript 2015 `=>` notation

```
{this.state.data.map((dummyDataName, dummyKeyName) => <UserDefinedComponent key = {dummyKeyName} thisStateKeyName = {dummyDataName} />)}
```

Then define `class UserDefinedComponent extends React.Component` which uses `this.props` (`props` is immutable) to render the data.
