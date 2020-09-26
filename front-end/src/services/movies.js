import Socket from "../util/Socket";
import { moviesEPs } from "../Config.json";
import Axios from "axios";


const { searchEP, browseEP, getEP, 
        thumbnailEP, peopleEP, peopleSearchEP,
        peopleGetEP} = moviesEPs

async function search(queryfield){
    const request = {
        params: queryfield
    };
    return await Socket.PARAMGET(searchEP, request);
}

async function browse(phrase, queryfield){
    const request ={
        params: queryfield
    };
    let tempurl = browseEP + "/" + phrase;
    console.log(tempurl);
    return await Socket.PARAMGET(tempurl, request);
}

async function getMovie(id, queryfield){
    const request = {
        params: queryfield
    };
    let tempurl = getEP + "/" + id;
    return await Socket.PARAMGET(tempurl, request);
}
async function thumbnail(data){
    const payLoad = {
        movie_ids: data
    };
    return await Socket.POST(thumbnailEP, payLoad);
}

async function people(queryfield){
    const request = {
        params: queryfield
    };
    return await Socket.PARAMGET(peopleEP, request);
}

async function peopleSearch(queryfield){
    const request = {
        params : queryfield
    };
    return await Socket.PARAMGET(peopleSearchEP, request);
}

async function peopleGet(id){
    let tempurl = peopleGetEP + "/" + id;
    return await Socket.GET(tempurl);
}

export default {
    search,
    browse,
    getMovie,
    thumbnail,
    people,
    peopleSearch,
    peopleGet
}