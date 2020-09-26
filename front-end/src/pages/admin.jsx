import React, { Component } from "react";
import Idm from "../services/Idm";
import Cookies from "js-cookie";
import billing from "../services/billing";



class admin extends Component{
    state = {
        isAdmin : false,

        code : "",
        start_date: "",
        end_date: "",
        discount: 0.0,
        limit: 0

    }
    handleSubmit = e => {
        e.preventDefault();
        const payLoad = {
            "email": Cookies.get("email"),
            "code": this.state.code,
            "discount" : parseFloat(this.state.discount),
            "sale_start": this.state.start_date,
            "sale_end": this.state.end_date,
            "limit": parseInt(this.state.limit)
        }
        billing.createDiscount(payLoad)
            .then(response => this.discountValidation(response))
            .catch(error => alert("Something went wrong creating the discount"))

    }

    discountValidation = response =>{
        let code = parseInt(JSON.stringify(response.data.resultCode));
        if(code === 3200){
            alert("Discount code has been created");
        } else {
            alert("Unable to create your discount code.")
        }
    }
    getuserdata(){
        Idm.privilege(Cookies.get("email"), 2)
            .then(response => this.validation(response))
            .catch(error => alert(error))
    }

    validation = response => {
        let code = parseInt(JSON.stringify(response.data.resultCode));
        if(code === 140){
            this.setState({isAdmin : true})
        }
    }
    updateField = ({ target }) => {
        const { name, value } = target;
        this.setState({ [name] : value })
    }
    
    componentDidMount(){ this.getuserdata(); }
    render(){
        const {isAdmin, code, discount, start_date, end_date, limit} = this.state;
        
        return(
                <div>
                    <h1>Create a Discount</h1>
                    <form onSubmit ={this.handleSubmit}>
                        <div>
                            <span className="label">Code:</span>
                            <input 
                            name="code"
                            value ={code}
                            onChange={this.updateField}/>
                        </div>
                        <div>
                            <span className="label">discount:</span>
                            <input 
                            name="discount"
                            value ={discount}
                            onChange={this.updateField}/>
                        </div>
                        <div>
                            <span className="label">start:</span>
                            <input 
                            name="start_date"
                            value ={start_date}
                            onChange={this.updateField}/>
                        </div>
                        <div>
                            <span className="label">end:</span>
                            <input 
                            name="end_date"
                            value ={end_date}
                            onChange={this.updateField}/>
                        </div>
                        <div>
                            <span className="label">limit:</span>
                            <input 
                            name="limit"
                            value ={limit}
                            onChange={this.updateField}/>
                        </div>
                        <button>Create</button>
                    </form>
                </div>
        )
    }
}

export default admin;