import React,{ Component } from "react";
import { Redirect } from "react-router-dom";




class LoginIndex extends Component{
    state = {
        search: false,
        cart: false,
        orderhistory: false,
    }

    handleClick = e => {
        e.preventDefault();
        const {id} = e.target;
        this.setState({[id]: true})
    }
    render(){
        const {search, cart, orderhistory} = this.state;

        if(search) return <Redirect to="/movies" />;
        if(cart) return <Redirect to="/cart" />;
        if(orderhistory) return <Redirect to="/OrderHistory" /> 
        return (
            <div>
                <h1 style={{textAlign: 'center'}}>FabFlix</h1>
                <div className="index-buttons">
                    <button id="search" onClick={this.handleClick}>Search</button>
                    <button id="cart" onClick={this.handleClick}>Cart</button>
                    <button id="orderhistory" onClick={this.handleClick}>Order History</button>
                </div>
            </div>
        )}
}

export default LoginIndex;