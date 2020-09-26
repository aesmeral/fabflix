import Socket from "../util/Socket";
import { idmEPs } from "../Config.json";

const { loginEP, registerEP, privilegeEP } = idmEPs;

async function login(email, password) {
  const payLoad = {
    email: email,
    password: password.split("")
  };

  return await Socket.POST(loginEP, payLoad);
}

async function register(email, password) {
  const payLoad = {
    email: email,
    password: password.split("")
  };

  return await Socket.POST(registerEP, payLoad)
}

async function privilege(email, plevel) {
  const payLoad = {
    email: email,
    plevel: plevel
  };
  return await Socket.POST(privilegeEP, payLoad);
}

export default {
  login,
  register,
  privilege
};
