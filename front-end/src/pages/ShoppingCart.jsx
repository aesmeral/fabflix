import React, { Component } from "react";
import billing from "../services/billing"
import Cookies from "js-cookie"


const localStorage = require("local-storage");

class ShoppingCart extends Component{
    state = {
        items: [],
        DNE: false,
        checkout: false,
        url: "",
        code: ""
    }
    getData() {
        billing.retrieve(Cookies.get("email"))
            .then(response => this.validation(response))
            .catch(error => alert(error));
    }
    
    placeOrder = () => {
        billing.placeOrder(Cookies.get("email"))
            .then(response => this.orderValidation(response))
            .catch(error => alert(error));
    }

    validation = response => {
        let code = parseInt(JSON.stringify(response.data.resultCode));
        if(code === 3130){
            this.setState({items : response.data.items})
        } else if(code === 312) this.setState({ DNE : true})
    }
    
    orderValidation = response => {
        let code = parseInt(JSON.stringify(response.data.resultCode));
        if(code === 3400){
            // redirect them to paypayl.
        }
    }

    updateCart = e => {
        e.preventDefault();
        const { id, value } = e.target;
        billing.update(Cookies.get("email"),id, value)
            .then(response => this.updateValidation(response))
            .catch(error => alert(error));
    }

    updateValidation = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3110){
            this.getData();
        } else {
            this.setState({DNE: true});
        }
    }

    deleteFromCart = e => {
        e.preventDefault();
        const {id} = e.target;
        billing.deleteMovie(Cookies.get("email"),id)
            .then(response => this.deleteMovieValidation(response))
            .catch(error => alert(error));
    }

    deleteMovieValidation = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3120){
            this.getData();
        } else {
            this.setState({DNE: true});
        }
    }

    clearCart = e => {
        e.preventDefault();
        billing.clear(Cookies.get("email"))
            .then(response => this.clearValidation(response))
            .catch(error => alert(error));
    }
    clearValidation = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3140){
            this.getData();
        } else {
            this.setState({DNE : true});
        }
    }

    checkOut = e => {
        e.preventDefault();
        billing.orderPlace(Cookies.get("email"))
            .then(response => this.verifyCheckout(response))
            .catch(error => alert(error))
    }
    
    verifyCheckout = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3400){
            localStorage.set("email", Cookies.get("email"));
            localStorage.set("session_id", Cookies.get("session_id"));
            window.open(response.data.approve_url,"_self");
        } else {
            // something went wrong.
        }
    }
    getPrice = (quantity, discount, unit_price) => {
        let discountedPrice = unit_price - (unit_price * discount);
        return quantity * discountedPrice;
    }

    componentDidMount(){ this.getData(); }

    getpicture = path => {
        return "https://image.tmdb.org/t/p/w200" + path;
    }

    handleSubmit = e =>{
        e.preventDefault()
        billing.applyDiscount(Cookies.get("email"), this.state.code)
            .then(response => this.verifyDiscount(response))
    }

    verifyDiscount = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3600){
            this.setState({items : response.data.items})
            console.log("Discount was successful")
        } 
        else if(resultCode === 3610) alert("Discount is invalid")
        else if(resultCode === 3620) alert("Discount code is expired");
        else if(resultCode === 3630) alert("Discount code limit exceeded");
        else alert("Unable to apply discount code.")
    }
    updateField = ({ target }) => {
        const { name, value } = target;
        this.setState({ [name] : value })
    }
    render(){
        const {items, DNE}  = this.state;
        let price = 0;
        if(!DNE){
            for(let i = 0; i < items.length; i++){
                price += this.getPrice(items[i].quantity, items[i].discount, items[i].unit_price)
            }
        }
        // replace p with some actual stuff.
        return (
            <div>
                {!DNE && <div>
                    <h1 className="cart-center">Cart</h1>
                    <table className="cart-table">
                        <tbody>
                            <tr>
                            <td>Movie</td>
                            <td>Price</td>
                            <td>Quantity</td>
                            <td><button className="cart-button" onClick={this.clearCart}>Clear Cart</button></td>
                            </tr>
                        </tbody>
                        {items.map(item => 
                            <tr key={item.movie_id}>
                                <td>
                                    <div>
                                        <img className="cart-image" src={this.getpicture(item.poster_path)}/> 
                                    </div>
                                    <div>
                                        <p>{item.movie_title}</p>
                                    </div>
                                </td>
                                <td>
                                    <div>
                                        ${this.getPrice(item.quantity, item.discount, item.unit_price).toFixed(2)}
                                    </div>
                                </td>
                                    <td>
                                        <select id={item.movie_id} defaultValue={item.quantity} onChange={this.updateCart}>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                            <option value="6">6</option>
                                            <option value="7">7</option>
                                            <option value="8">8</option>
                                            <option value="9">9</option>
                                            <option value="10">10</option>
                                            <option value="11">11</option>
                                            <option value="12">12</option>
                                            <option value="13">13</option>
                                            <option value="14">14</option>
                                            <option value="15">15</option>
                                            <option value="16">16</option>
                                            <option value="17">17</option>
                                            <option value="18">18</option>
                                            <option value="19">19</option>
                                            <option value="20">20</option>
                                        </select>
                                    </td> 
                                    <td><button className="cart-button" id={item.movie_id} onClick={this.deleteFromCart}>Remove</button></td>
                            </tr>)
                        }
                    </table>
                </div> 
                }
                <div className="checkout-container">
                    {!DNE && <h1 className="cart-center">Total Cost: ${price.toFixed(2)}</h1>}
                    {!DNE && <button className="checkout-button" onClick={this.checkOut}>Check Out</button>}
                    {!DNE && 
                    <form className="cart-center" onSubmit={this.handleSubmit}>
                        <span className="label">Discount Code:</span>
                        <input 
                        name="code"
                        value={this.state.code}
                        onChange={this.updateField}/>
                        <button>Apply Discount</button>
                    </form>
                    }
                </div>
                {DNE && <h1>Your Shopping Cart is Empty</h1>}
            </div>
        )
    }
}

export default ShoppingCart;