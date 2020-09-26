import Axios from "axios";
import Cookies from "js-cookie";

import Config from "../Config.json";

const { baseUrl, pollLimit, gatewayEPs } = Config;
const HTTPMethod = Object.freeze({
  GET: "GET",
  POST: "POST",
  DELETE: "DELETE"
});

function initSocket() {
  const { common } = Axios.defaults.headers;

  common["session_id"] = Cookies.get("session_id");
  Axios.defaults.baseURL = baseUrl;
}

function setHeaders() {
  const { common } = Axios.defaults.headers;

  common["email"] = Cookies.get("email");
  common["transaction_id"] = Cookies.get("transaction_id");
  common["session_id"] = Cookies.get("session_id")
}

async function GET(path) {
  setHeaders();
  return await sendHTTP(HTTPMethod.GET, path);
}

async function PARAMGET(path, request){
  setHeaders();
  return await sendHTTP(HTTPMethod.GET, path, request);
}

async function POST(path, data) {
  setHeaders();
  return await sendHTTP(HTTPMethod.POST, path, data);
}

async function DELETE(path) {
  setHeaders();
  return await sendHTTP(HTTPMethod.DELETE, path);
}

async function sendHTTP(method, path, data) {
  let response;

  switch (method) {
    case HTTPMethod.GET:
      if(data === null) response = await Axios.get(path);
      else response = await Axios.get(path, data);
      break;
    case HTTPMethod.POST:
      response = await Axios.post(path, data);
      break;
    case HTTPMethod.DELETE:
      response = await Axios.delete(path);
      break;
    default:
    // Should never reach here
  }

  /************************************************
        TODO Do error checking on response 
  ************************************************/

  return await getReport(response);
}

async function getReport(response) {
  const axiosConfig = {
    headers: { transaction_id: response.headers["transaction_id"]}
  };
  return await pollForReport(axiosConfig);
}

async function pollForReport(axiosConfig) {  let noContent = 204;


  for (let i = 0; i < pollLimit; i++) {
    const response = await Axios.get(gatewayEPs.reportEP, axiosConfig);
    if (response.status !== noContent) {
      /************************************************
            TODO More Robust checking for response  
      ************************************************/
      return response;
    } else await timeOut();
  }

  return undefined;
}

async function timeOut() {
  return new Promise(resolve => {
    let pollingLimit = 500;
    setTimeout(() => resolve(), pollingLimit);
  });
}

export default {
  initSocket,
  GET,
  POST,
  DELETE,
  PARAMGET
};
