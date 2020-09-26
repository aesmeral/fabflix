import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import Idm from "../services/Idm";

class Register extends Component{
    state = {
        email: "",
        password: "",
        error: false,
        message: "",
        redirect: false
    };

    handleSubmit = e => {
        e.preventDefault();
        const {email, password} = this.state;
        Idm.register(email, password)
            .then(response => this.validation(JSON.stringify(response.data.resultCode)))
            .catch(error => alert(error));
    };

    validation = resultCode => {
        resultCode = parseInt(resultCode);
        if(resultCode === 110) {
            this.setState({redirect : true});
        } else if(resultCode === 12) {
            this.setState({
                error: true,
                message: "Password does not meet length requirement."
            })
        } else if(resultCode === 13) {
            this.setState({
                error: true,
                message: "Password does not meet character requirements."
            })
        } else this.setState({
            error: true,
            message: "This email is already in use."
        })
    };

    updateField = e => {
        const {name, value} = e.target;
        this.setState({ [name]: value})
    };

    render(){
        const {email, password, redirect} = this.state;

        if(redirect) return <Redirect to="/login" />;

        return (
            <div>
              <h1>Register</h1>
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
                <button className="button">Sign up</button>
              </form>
              <br />
              <p>Already have an account? <a href="/login"> Login Here </a></p>
              <br />
              {this.state.error && <p style={{color: "red"}}>{this.state.message}</p>}
            </div>
          );

    }
}



export default Register;