import React, { Component } from "react";
import queryString from 'query-string';
import billing from '../services/billing'
import { Redirect } from "react-router-dom";
import Cookies from 'js-cookie';

class Complete extends Component{
    state = {
        verified: false
    }
    respondTo(){
        let params = queryString.parse(this.props.location.search);
        billing.orderComplete(params)
            .then(response => console.log(JSON.stringify(response)))
            .catch(error => alert(error))
    }
    verify = response => {
        console.log(Cookies.get("email"));
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        console.log(resultCode);
        if(resultCode === 3420){
            this.setState({verified : true})
        }
    }

    componentDidMount() { this.respondTo(); }

    render(){
        return <Redirect to="/index" />;
    }
}

export default Complete;