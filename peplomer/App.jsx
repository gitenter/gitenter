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

export default App;
