import React from 'react';
import axios from 'axios';

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
            users: []
		};
	}

    componentDidMount() {
		axios.get('http://localhost:8888/api/users/').then(response => {
			this.setState({users: response.data});
		}).catch(error => console.log(error));
	}

	render() {
		return (
            <UserList users={this.state.users}/>
		);
	}
}

class UserList extends React.Component{
	render() {
		var users = this.props.users.map(user =>
			<User key={user.id} user={user}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Username</th>
						<th>Full Name</th>
						<th>Email</th>
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

export default App;
