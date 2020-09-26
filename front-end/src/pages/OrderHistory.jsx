import React, { Component } from "react";
import billing from "../services/billing"
import Cookies from "js-cookie"
class OrderHistory extends Component{
    state = {
        transactions: [],
        DNE: false,
    }

    getTransactions(){
        billing.orderRetrieve(Cookies.get("email"))
            .then(response => this.validate(response))
            .catch(error => alert(error))
    }

    validate = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3410){
            this.setState({transactions : response.data.transaction})
        } else this.setState({DNE : true})
    }

    parseDate = date => {
        date = new Date(date)
        let month = date.getMonth() + 1;
        let day = date.getDate();
        let year = date.getFullYear();
        return month + "/" + day + "/" + year;
    }

    // probably create a component that renders these?
    displayMovies = movieItems => {
        let movies = [];
        movies = movieItems;
        console.log(movies)
    }
    componentDidMount() { this.getTransactions(); }

    render(){
        const {transactions, DNE} = this.state;
        return (
            <div>
                <table className="cart-table">
                    <tbody>
                        <tr>
                            <td>Transaction Amount</td>
                            <td>Transaction Date</td>
                        </tr>
                    </tbody>
                {transactions.map(transaction=> 
                    <tr key={transaction.capture_id}>
                        <td>${transaction.amount.total}</td> 
                        <td>{this.parseDate(transaction.create_time)} </td>
                    </tr>)
                }
                </table>
            {DNE && <h1>Your Order History is Empty</h1>}
            </div>
        )
    }
}

export default OrderHistory;