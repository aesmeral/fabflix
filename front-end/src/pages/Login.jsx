import React, { Component } from "react";

import Idm from "../services/Idm";

import "../css/common.css";
import { Redirect } from "react-router-dom";

import queryString from 'query-string';

class Login extends Component {
  state = {
    email: "",
    password: "",
    redirect: false,
    error: false,
    message: "",
  };

  handleSubmit = e => {
    e.preventDefault();
    const { email, password } = this.state;

    Idm.login(email, password)
      .then(response => {
        this.validation(response)
      })
      .catch(error => console.log(error));
  };

  validation = response => {
    const { handleLogIn } = this.props;
    const { email } = this.state;
    let resultCode = parseInt(JSON.stringify(response.data.resultCode));
    if(resultCode === 120) {
      this.setState({redirect: true});
      handleLogIn(email, response.data.session_id);
    } else this.setState({
      error: true,
      message: "Password or Username is invalid"
    })
  }
  updateField = ({ target }) => {
    const { name, value } = target;

    this.setState({ [name]: value });
  };

  render() {
    const { email, password } = this.state;
    if (this.state.redirect) return <Redirect to="/index" />;
    return (
      <div>
        <h1>Login</h1>
        <form onSubmit={this.handleSubmit}>
          <label className="label">Email</label>
          <input
            className="input"
            type="email"
            name="email"
            value={email}
            onChange={this.updateField}
          ></input>
          <label className="label">Password</label>
          <input
            className="input"
            type="password"
            name="password"
            value={password}
            onChange={this.updateField}
          ></input>
          <button className="button">Login</button>
        </form>
        <br/>
        <p>Dont have an account? <a href="/Register"> Sign up </a></p>
        <br />
        {this.state.error && <p style={{color: "red"}}>{this.state.message}</p>}
      </div>
    );
  }
}

export default Login;
