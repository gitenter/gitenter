# AngularJS Notes

A JavaScript framework for Single Page Applications (SPAs).

+ **Directives** (extend HTML): build-in ones with `ng-` (or `data-ng-` to make the page HTML valid)
	+ Customized by JavaScript function `angular.module(...).directive('name', function(...))` (name uses "camelCase" string, while the corresponding HTML name uses "-"-separation). 
		+ The HTML name can be either in (1) element name, (2) attribute -- default for most build-in directives, (3) class, and (4) comment.
		+ The return is a JSON with tag (1) `template` (mandatory), (2) `restrict` (optional, default value `EA`. `E` for element name, `A` for attribute, `C` for class, and `M` for comment).

### Build-in directives

Setup variables:

+ **Modules** (define the root element of AngularJS applications): `ng-app` w/ JavaScript function `angular.module('name', [])`
	+ If no `ng-controller` id defined, initial variables goes with `app.run(...)` with argument `$rootScope`.
+ **Controllers** (control AngularJS applications): `ng-controller` w/ JavaScript function `angular.module(...).controller('name', function(...))` with `function(...)` argument `$scope`.
	+ Beside `$scope`, other arguments are for **services**: (1) `$location`, (2) `$http`, (3) `$timeout`, (4) `$interval`.
		+ Can create services by `app.service('name', function(...))`.
+ Directive initializes variables: `ng-init`
	+ Not recommend, setup with `ng-app` and `ng-controller`.
+ Directive binds the value of HTML controls (`<input>`, ...) to application data: `ng-model`
	+ Overwrite the initial value defined by either (1) `ng-init` or (2) `ng-app` together with `ng-controller`.
	+ Can show status (1) `$valid`, (2) `$dirty`, (3) `touched`, (4) `$error`.
	+ Type validation using HTML attribute `type` (part of HTML5, not AngularJS), and error message comes with `ng-show` and `$error`.
	+ May use `ng-valid` or `ng-invalid` tag in the corresponding CSS.

Display variables:

+ **Expressions**: `{{ expression }}` or `ng-bind` w/ JavaScript expression of (1) literals, (2) operators, and (3) variables in it, but do not support (4) conditionals, (5) loops, and (6) exceptions. It also support (7) filters while JavaScript doesn't.
+ Directive binds application data to the HTML view: `ng-view`
+ Repeat an HTML element (when the value is a array): `ng-repeat`

Transform data:

+ **Filters**: `{{ expression | filter }}`
	+ Build-in filters: (1) `currency`, (2) `date`, (3) `filter : "a-string"`, (4) `json`, (5) `limitTo`, (6) `lowercase`, (7) `number`, (3) `orderBy`, (9) `uppercase`.
	+ Customized by JavaScript function `angular.module(...).filter('name', function(...))`
	+ Use the customized service inside of a filter `app.filter('name', ['service-name', function(...)])`
+ **Events**
+ **DOM**
+ **Forms**
+ **Input**
+ **Validation**
