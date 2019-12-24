import React from "react";
import { Button } from 'react-bootstrap';

import Day from './Day';

export default class Schedule extends React.Component {
  scheduleHeader() {
    return (
      <div className="Planner-schedule-header">
        <div className="Planner-schedule-header-pagination">
          <Button className="mr-3" size="sm" variant="custom-grey-light" disabled={this.props.page <= 1 ? true : false} onClick={this.props.onPrevious}>&lt;</Button>
          <label className="mr-3">{this.props.page} of {this.props.totalPages} Schedules</label>
          <Button size="sm" variant="custom-grey-light" disabled={this.props.page >= this.props.totalPages ? true : false} onClick={this.props.onNext}>&gt;</Button>
        </div>
        <div className="Planner-schedule-header-credits">
          <Button className="mr-3" size="sm" variant="custom-grey-light" disabled={Object.keys(this.props.selectedCourses).length <= 0 ? true : false} onClick={(this.props.generateSchedules)}>Generate schedules</Button>
          <Button className="mr-3" size="sm" variant="custom-grey-light" disabled={Object.keys(this.props.schedule).length <= 0 ? true : false} onClick={this.props.exportToCart}>Export to cart</Button>
          <label>Credits for selected courses: {this.props.selectedCredits}</label>
        </div>
      </div>
    );
  }

  generateScheduleTable() {
    // Check if there are no items on list
    if (Object.keys(this.props.schedule).length <= 0) {
      if (this.props.noSchedulesPossible) {
        return (
          <div className="App-warning m-3">
            <h6>There are no possible course combinations that do not conflict. <br/>Please check your cart and try again</h6>
          </div>
        )
      } else {
        return (
          <div className="App-warning m-3">
            <h6>Please click 'Generate Schedules' after selecting your courses</h6>
          </div>
        )
      }
    }

    let table = [];

    for (const day of Object.entries(this.props.schedule)) {
      table.push(<Day key={day[0]} title={day[0]} blocks={day[1]} start={7} end={19} height={800} />);
    }

    return (
      <div className="Planner-schedule-table">
        {table}
      </div>
      );
  }

  render() {
    return (
      <>
        {this.scheduleHeader()}
        {this.generateScheduleTable()}
      </>
    )
  }
}