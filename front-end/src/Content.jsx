import React, { Component } from "react";
import { Route, Switch, Redirect} from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import Movies from "./pages/Movies";
import ShoppingCart from "./pages/ShoppingCart";
import LoginIndex from "./pages/LoginIndex";
import OrderHistroy from "./pages/OrderHistory";
import moviesDetailed from "./pages/MovieDetails";
import Complete from "./pages/Complete";
import admin from "./pages/admin";

const redirectLogin = () => <Redirect to="/login" />;

class Content extends Component {
  render() {
    const { handleLogIn } = this.props;

    return (
      <div className="content">
        <Switch>
          <Route
            path="/login"
            component={props => <Login handleLogIn={handleLogIn} {...props} />}
          />
          <Route path="/register" component={Register} />
          <Route path="/movies" component={Movies} />
          <Route path="/moviesDetailed" component={moviesDetailed} />
          <Route path="/cart" component={ShoppingCart} />
          <Route path="/index" component={LoginIndex} />
          <Route path="/OrderHistory" component={OrderHistroy} />
          <Route path="/complete" component={Complete} />
          <Route path="/admin" component={admin} />
          <Route path="/" component={redirectLogin} />
        </Switch>
      </div>
    );
  }
}

export default Content;
