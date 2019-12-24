import React from 'react';
import Course from './Course';

import './css/CourseArea.css';

export default class CourseArea extends React.Component {
  getCourses() {
    // Check if there are no items on list
    if (Object.keys(this.props.data).length <= 0) {
      return (
        <div className="App-warning">
          <h6>{this.props.partOfCart ? "There are no courses in the cart" : "There are no results for this search"}</h6>
        </div>
      )
    }

    return (
      Object.entries(this.props.data).map((course) => {
        return <Course key={course[0]} courseNum={course[0]} data={course[1]} partOfCart={this.props.partOfCart} cartAction={(c, s, ss) => this.props.cartAction(c, s, ss)}/>
      })
    )
  }

  render() {
    return (
      <div className="App-course-list">
        {this.getCourses()}
      </div>
    )
  }
}