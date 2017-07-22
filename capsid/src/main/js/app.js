const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {organizations: []};
	}

	componentDidMount() {
		client({method: 'GET', path: 'hal/organizations'}).done(response => {
			this.setState({organizations: response.entity._embedded.organizations});
		});
	}

	render() {
		return (
			<OrganizationList organizations={this.state.organizations}/>
		)
	}
}

class OrganizationList extends React.Component{
	render() {
		var organizations = this.props.organizations.map(organization =>
			<Organization key={organization._links.self.href} organization={organization}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Name</th>
						<th>Display Name</th>
					</tr>
					{organizations}
				</tbody>
			</table>
		)
	}
}

class Organization extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.organization.name}</td>
				<td>{this.props.organization.displayName}</td>
			</tr>
		)
	}
}

ReactDOM.render(
	<App />,
	document.getElementById('react')
)
