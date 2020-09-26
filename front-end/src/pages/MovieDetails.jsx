import React, { Component } from "react";
import movies from "../services/movies";
import billing from "../services/billing"
import Cookies from "js-cookie"
class MovieDetails extends Component{
    state = {
        movie_id : this.props.location.state.movie_id,
        title: "",
        year: "",
        director: "",
        rating: "",
        num_votes: "",
        overview: "",
        poster_path: "",
        backdrop_path: "",
        genres: [],
        people: [],
        DNE: false,

        quantity: 1,
        message: "",
        successful: false,
        error : false,
    }

    getMovieDetail(){
        const { movie_id } = this.state;
        movies.getMovie(movie_id)
            .then(response => this.verify(response))
            .catch(error => alert(error));
    }

    verify = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 210){
            this.setState({
                title: response.data.movie.title,
                year: response.data.movie.year,
                director: response.data.movie.director,
                rating: response.data.movie.rating,
                num_votes: response.data.movie.num_votes,
                overview: response.data.movie.overview,
                poster_path: response.data.movie.poster_path,
                backdrop_path: response.data.movie.backdrop_path,
                genres: response.data.movie.genres,
                people: response.data.movie.people
            })
        } else {
            this.setState({DNE: true})
        }
    }

    componentDidMount(){ this.getMovieDetail(); }

    changeAmount = e => {
        e.preventDefault();
        const {value} = e.target;
        this.setState({quantity: value});
    }
    insertToCart = e => {
        e.preventDefault();
        const {quantity, movie_id} = this.state;
        billing.insert(Cookies.get("email"),movie_id, quantity)
            .then(response => this.verifyInsert(response))
            .catch(error => alert(error));
    }

    verifyInsert = response => {
        let resultCode = parseInt(JSON.stringify(response.data.resultCode));
        if(resultCode === 3100){
            this.setState({successful: true})
            alert("Items added onto your cart");
        } else if(resultCode === 311){
            this.setState({error: true, message: "This item is already in your cart."})
        }
    }
    getpicture = path => {
        return "https://image.tmdb.org/t/p/w300" + path;
      }
    getBackGround = path => {
        return "https://image.tmdb.org/t/p/w500" + path;
    }
    render(){
        const { title, year, director, rating, num_votes, overview, poster_path, backdrop_path, genres, people, DNE} = this.state;
        return(
            <div>
                <div className="movie-details">
                    <div className="movie-detail-poster">
                        <img src={this.getpicture(poster_path)} />
                    </div>
                    <div className="movie-detail-details">
                        <h1>{title}</h1>
                        <div>
                            <div><h3>{year}</h3></div>
                            <div><h3>{director}</h3></div>
                        </div>
                        <div>
                            <p>{overview}</p>
                        </div>
                        <div>
                            <div><h4>Rating: {rating}</h4></div>
                            <div><h4>Votes: {num_votes}</h4></div>
                        </div>
                        <div>
                            <p>Peoples Component</p>
                        </div>
                        <div>Genres Component</div>
                    </div>
                </div>
                <div className="add-movie-to-cart">
                    <span className="label">Order: </span>
                    <select id={this.state.movie_id} defaultValue="1" onChange={this.changeAmount}>
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
                    <button className="add-button" onClick={this.insertToCart}>Order Now</button>
                </div>
            </div>
        )
    }
}


export default MovieDetails;