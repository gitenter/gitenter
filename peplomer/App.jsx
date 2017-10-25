import React from 'react';
import axios from 'axios';

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			members: []
		};
	}

	componentDidMount() {
		axios.get('http://localhost:8888/api/members/').then(response => {
			this.setState({members: response.data});
		}).catch(error => console.log(error));
	}

	render() {
		return (
			<div>
				<AddNewMember/>
				<MemberList members={this.state.members}/>
			</div>
		);
	}
}

class MemberList extends React.Component {
	render() {
		var members = this.props.members.map(member =>
			<Member key={member.id} member={member}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Username</th>
						<th>Full Name</th>
						<th>Email</th>
					</tr>
					{members}
				</tbody>
			</table>
		)
	}
}

class Member extends React.Component {
	render() {
		return (
			<tr>
				<td>{this.props.member.username}</td>
				<td>{this.props.member.displayName}</td>
				<td>{this.props.member.email}</td>
			</tr>
		)
	}
}

class AddNewMember extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			username: '',
			password: '',
			displayName: '',
			email: ''
		};

		this.handleChange = this.handleChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleChange(event) {
		const name = event.target.name;
		this.setState({[name]: event.target.value});
	}

	handleSubmit(event) {
		axios.post('http://localhost:8888/api/members/', {
			username: this.state.username,
			password: this.state.password,
			displayName: this.state.displayName,
			email: this.state.email
		}).then(function (response) {
			console.log(response);
		}).catch(error => console.log(error));

		event.preventDefault();
	}

	render() {
		return (
			<form onSubmit={this.handleSubmit}>
				<label>
					Username: <input type="text" name="username" value={this.state.username} onChange={this.handleChange}/>
				</label>
				<br/>
				<label>
					Password: <input type="text" name="password" value={this.state.password} onChange={this.handleChange} />
				</label>
				<br/>
				<label>
					Display Name: <input type="text" name="displayName" value={this.state.displayName} onChange={this.handleChange} />
				</label>
				<br/>
				<label>
					Email: <input type="text" name="email" value={this.state.email} onChange={this.handleChange} />
				</label>
				<input type="submit" value="Submit" />
			</form>
		);
	}
}

export default App;
