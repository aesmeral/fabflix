import Socket from "../util/Socket";
import { billingEPs } from "../Config.json";
import Axios from "axios";

const { insertEP, updateEP, 
        deleteEP, retrieveEP, 
        clearEP, orderPlaceEP,
        orderRetrieveEP, orderCompleteEP, 
        createDiscountEP, discountApplyEP } = billingEPs;


async function insert(email, movie_id, quantity){
    console.log(movie_id)
    const payLoad = {
        "email": email,
        "movie_id": movie_id,
        "quantity": quantity
    };
    return await Socket.POST(insertEP, payLoad);
}

async function update(email, movie_id, quantity){
    const payLoad = {
        "email": email,
        "movie_id": movie_id,
        "quantity": quantity
    };    
    return await Socket.POST(updateEP, payLoad);
}

async function deleteMovie(email, movie_id){
    const payLoad = {
        "email": email,
        "movie_id": movie_id
    };
    return await Socket.POST(deleteEP, payLoad)
}

async function retrieve(email){
    const payLoad = {
        "email": email
    };
    return await Socket.POST(retrieveEP, payLoad);
}

async function clear(email){
    const payLoad = {
        "email": email
    };

    return await Socket.POST(clearEP, payLoad)
}

async function orderPlace(email){
    const payLoad = {
        "email": email
    };
    return await Socket.POST(orderPlaceEP, payLoad)
}

async function orderRetrieve(email){
    const payLoad = {
        "email": email
    };
    return await Socket.POST(orderRetrieveEP, payLoad);
}

async function orderComplete(queryfield){
    const request = {
        params: queryfield
    };
    return await Socket.PARAMGET(orderCompleteEP, request);
}

async function createDiscount(payLoad){
    return await Socket.POST(createDiscountEP, payLoad);
}

async function applyDiscount(email, discount_code){
    const payLoad = {
        "email": email,
        "discount_code": discount_code
    };
    return await Socket.POST(discountApplyEP, payLoad);
}







export default {
    insert,
    update,
    deleteMovie,
    retrieve,
    clear,
    orderPlace,
    orderRetrieve,
    orderComplete,
    createDiscount,
    applyDiscount
};