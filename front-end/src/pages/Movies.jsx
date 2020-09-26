import React, { Component } from "react";
import movies from "../services/movies";
import { Redirect } from "react-router-dom";
class Movies extends Component {
  state = {
    moviesdata: [],
    moviesFound: true,
    title: true,               // default select is titles
    keywords: false,           // we can switch to keywords..
    mainField: "",             // if its keywords, put keywords. else put a title.
    year: "",           
    genre: "",
    director: "",
    limit: 10,                // default result per page
    orderby: "title",         // default sort type
    direction: "asc",          // default to asc.
    offset: 0,
    redirect: false,
    redirectTo: ""

  };

  onSubmitHandler = e => {
    e.preventDefault();
    const { title, keywords, mainField, year, genre, director, limit, orderby, direction, offset} = this.state;
    if(title && !keywords){
      if(mainField === "" && year === "" && genre === "" && director === ""){
        this.setState({moviesFound: false});
      } else {
        let queryfield = {limit: limit, orderby: orderby, direction: direction, offset: offset};
        if(mainField !== "") queryfield.title = mainField;
        if(year !== "") queryfield.year = year;
        if(genre !== "") queryfield.genre = genre;
        if(director !== "") queryfield.director = director;
        movies.search(queryfield)
          .then(response => this.verify(response))
          .catch(error => alert(error));
      }
    } else {
      if(keywords === ""){
        this.setState({moviesFound: false});
      } else {
        let queryfield = {limit: limit, orderby: orderby, direction: direction, offset: offset};
        movies.browse(mainField, queryfield)
          .then(response => this.verify(response))
          .catch(error => alert(error));
      }
    }

  }
  verify = response => {
    let resultCode = parseInt(JSON.stringify(response.data.resultCode));
    if(resultCode === 210){
      this.setState({moviesdata : response.data.movies, moviesFound: true});
    } else {
      this.setState({moviesFound : false});
    }
  }



  updateField = e => {
    e.preventDefault();
    const {name, value} = e.target;
    this.setState({[name]: value});
  }

  searchType = e => {
    e.preventDefault();
    this.setState({title: !this.state.title, keywords: !this.state.keywords});
  }

  sortingType = e => {
    e.preventDefault();
    const {id, value} = e.target;
    this.setState({[id]: value})
  }

  getpicture = path => {
    return "https://image.tmdb.org/t/p/w200" + path;
  }

  clickedMovie = title => {
    console.log(title);
    this.props.history.push({
      pathname:"/moviesDetailed",
      state:{
        movie_id: title
      }
    });
  }
  componentDidUpdate(prevProps,prevState, snapshot){
    if(prevState.offset !== this.state.offset){
      this.getData();
      window.scroll(0,0);   
    }
  }

  getData(){
    const { title, keywords, mainField, year, genre, director, limit, orderby, direction, offset} = this.state;
    if(title && !keywords){
      if(mainField === "" && year === "" && genre === "" && director === ""){
        this.setState({moviesFound: false});
      } else {
        let queryfield = {limit: limit, orderby: orderby, direction: direction, offset: offset};
        if(mainField !== "") queryfield.title = mainField;
        if(year !== "") queryfield.year = year;
        if(genre !== "") queryfield.genre = genre;
        if(director !== "") queryfield.director = director;
        movies.search(queryfield)
          .then(response => this.verify(response))
          .catch(error => alert(error));
      }
    } else {
      if(keywords === ""){
        this.setState({moviesFound: false});
      } else {
        let queryfield = {limit: limit, orderby: orderby, direction: direction, offset: offset};
        movies.browse(mainField, queryfield)
          .then(response => this.verify(response))
          .catch(error => alert(error));
      }
    }
  }

  clickedOffset = e => {
    e.preventDefault();
    const {id} = e.target;
    const {offset, limit, moviesdata } = this.state;
    let page;
    console.log(id);
    if(id === "prev") page = parseInt(offset) - parseInt(limit);
    else{
       if(parseInt(limit) === moviesdata.length) page = parseInt(offset) + parseInt(limit)
       else page = offset;  
    }
    if(page < 0) page = 0; 
    this.setState({offset : page});
  }

  render() {
    const {moviesdata, mainField, title, keywords, director, genre, year} = this.state;
    return (
      <div>

        <div className="search-box">
          <form onSubmit={this.onSubmitHandler}>
            <div className="field-objects">
              <select className="search-field-select"defaultValue="title" onChange={this.searchType}>
                <option  value="title">title</option>
                <option  value="keywords">keywords</option>
              </select>
              <input
                className="search-field"
                name="mainField" 
                value={mainField}
                onChange={this.updateField}
                placeholder={title ? "Enter a title name here " : "Enter some keywords"}
              />
            </div>
            {
              title && !keywords && 
              <div className="additional-fields">
                <div>
                  <div className="field-objects">
                    <span className="label">Director:</span>
                    <input
                      className = "right-floaters" 
                      name = "director"
                      value = {director}
                      onChange={this.updateField}
                    />
                  </div>
                  <div className="field-objects">
                    <span className="label">Genre:</span>
                    <input
                      className="right-floaters"
                      name ="genre"
                      value = {genre}
                      onChange={this.updateField}
                    />
                  </div>
                  <div className="field-objects">
                    <span className="label">Year:</span>
                    <input
                      className = "right-floaters"
                      name ="year"
                      value = {year}
                      onChange={this.updateField}
                    />
                  </div>
                </div>
                <div className="movie-floaters">
                  <div>
                    <span className="label">Limit</span>
                    <select id="limit" defaultValue="10" onChange={this.sortingType}>
                      <option value="10">10</option>
                      <option value="25">25</option>
                      <option value="50">50</option>
                      <option value="100">100</option>
                    </select>
                  </div>
                  <div>
                    <span className="label">Sort</span>
                    <select id="orderby" defaultValue="title" onChange={this.sortingType}>
                      <option value="title">title</option>
                      <option value="rating">rating</option>
                      <option value="year">year</option>
                    </select>
                    <select id="direction" defaultValue="asc" onChange={this.sortingType}>
                      <option value="asc">asc</option>
                      <option value="desc">desc</option>
                    </select>
                  </div>
                </div>
              </div>
            }
            {
              keywords && !title &&
              <div>
                  <label>limit </label>
                  <select id="limit" defaultValue="10" onChange={this.sortingType}>
                    <option value="10">10</option>
                    <option value="25">25</option>
                    <option value="50">50</option>
                    <option value="100">100</option>
                  </select>
                  <br/>
                  <label>sort by </label>
                  <select id="orderby" defaultValue="title" onChange={this.sortingType}>
                    <option value="title">title</option>
                    <option value="rating">rating</option>
                    <option value="year">year</option>
                  </select>
                  <label>limit </label>
                  <select id="direction" defaultValue="asc" onChange={this.sortingType}>
                    <option value="asc">asc</option>
                    <option value="desc">desc</option>
                  </select>
                </div>
            }
            <button className="search-button">Search</button>
          </form>
        </div>

        <div className="movie-list">       
            {moviesdata && moviesdata.map(movie => 
              <div className="movie" key={movie.movie_id}>
                <button className="movie-poster"><img src={this.getpicture(movie.poster_path)} onClick={() => this.clickedMovie(movie.movie_id)}/></button>
                <p>{movie.title}</p>
              </div>
            )}
        </div>
        <div className="offsetbuttons">
          <button id="prev" onClick={this.clickedOffset}>Prev</button>
          <button id="next" onClick={this.clickedOffset}>Next</button>
        </div>
      </div>
    )
  }
}

export default Movies;
