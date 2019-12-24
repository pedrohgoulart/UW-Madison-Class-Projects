import React from 'react';
import { Card } from 'react-bootstrap';
import CourseSelection from './components/CourseSelection';

import './css/PlannerSidebar.css';

export default class PlannerSidebar extends React.Component {
  getCourses() {
    // Check if there are no items on list
    if (Object.keys(this.props.data).length <= 0) {
      return (
        <div className="App-warning">
          <h6>There are no courses in the cart</h6>
        </div>
      )
    }

    return (
      Object.entries(this.props.data).map((course) => {
        return <CourseSelection key={course[0]} courseNum={course[0]} data={course[1]} selectedCourses={this.props.selectedCourses} triggerItem={(c, s, ss, v) => this.props.selectAction(c, s, ss, v)} />
      })
    )
  }

  render() {
    return (
      <Card className="App-sidebar">
        <Card.Body>
          <Card.Title>Planner</Card.Title>
          
          <label>Courses in Cart</label>
          <div className="Planner-course-selection-list">
            {this.getCourses()}
          </div>
        </Card.Body>
      </Card>
    )
  }
}