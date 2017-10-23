import React from 'react';
import Axios from 'axios';

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            users: []
		};
	}

    componentDidMount() {
/*		client({method: 'GET', path: 'http://localhost:8888/api/users/'}).done(response => {
			this.setState({users: response.entity});
		});*/

		Axios.get('http://localhost:8888/api/users/').then(response => {
			console.log("~~~~~componentDidMount()~~~~~~~~");
			console.log(response);
			console.log("response.data = " + response.data);
			console.log("response.data.users = " + response.data.users);
			console.log("response.entity = " + response.entity);
			console.log("response.data[0] = " + response.data[0]);
			console.log("response.data.users[0] = " + response.data.users[0]);
			console.log("username = " + response.data.users[0].username);
			console.log(typeof response.data.users[0].username);
			// console.log("xxx" +this.state.users);
			this.setState({users: response.data.users});
/*			var responseData = JSON.parse(response.data);
                this.setState({
                    users: responseData
			}).bind(this).catch((err) => {
                console.log(err);
			});*/
			// console.log("users = " + this.state.users);
		}).catch(error => console.log(error));
/*
		Axios.get('http://localhost:8888/api/users/').then(res => {
			this.setState({users});
		});*/
	}

	render() {
        console.log("~~~~~render()~~~~~~~~");
        console.log(this.state.users);
        console.log(this.props.users);
		return (
            <UserList users={this.state.users}/>
/*            <div>
                <Header/>
                <table>
                    <tbody>
                        {this.state.users.map((user, i) => <TableRow key = {i} users = {user} />)}
                    </tbody>
                </table>
            </div>*/
		);
	}
}

class UserList extends React.Component{
	render() {
		var users = this.props.users.map(user =>
			<User key={i} users={user}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Firs Name</th>
						<th>Last Name</th>
						<th>Description</th>
					</tr>
					{users}
				</tbody>
			</table>
		)
	}
}

class User extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.user.username}</td>
				<td>{this.props.user.displayName}</td>
				<td>{this.props.user.email}</td>
			</tr>
		)
	}
}

/*
class Header extends React.Component {
	render() {
		return (
			<div>
				<h1>Header</h1>
			</div>
		);
	}
}

class TableRow extends React.Component {
	render() {
		return (
			<tr>
				<td>{this.props.users.username}</td>
				<td>{this.props.users.displayName}</td>
				<td>{this.props.users.email}</td>
			</tr>
		);
	}
}
*/
export default App;
