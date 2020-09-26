import React, { Component, Fragment } from "react";
import { NavLink } from "react-router-dom";

import "./css/style.css";

class NavBar extends Component {
  render() {
    const { handleLogOut, loggedIn } = this.props;

    return (
      <nav className="nav-bar">
        {!loggedIn && (
          <Fragment>
            <NavLink className="nav-link" to="/login">
              Login
            </NavLink>
          </Fragment>
        )}
        {loggedIn && (
          <div className="login-nav-bar">
            <div className="login-nav-bar-left">
              <NavLink className="nav-link" to="/index">
                Home
              </NavLink>
              <NavLink className="nav-link" to="/movies">
                Search
              </NavLink>
              <NavLink className="nav-link" to="/cart">
                Cart
              </NavLink>
              <NavLink className="nav-link" to="/OrderHistory">
                Order History
              </NavLink>
            </div>
            <div className="login-nav-bar-right">
              <button onClick={handleLogOut} className="nav-button">
                Log Out
              </button>
            </div>
          </div>
        )}
      </nav>
    );
  }
}

export default NavBar;
