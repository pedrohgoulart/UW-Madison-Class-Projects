import React from 'react';
import { Card } from 'react-bootstrap';
import Schedule from "./components/Schedule";

import './css/PlannerSchedule.css';

export default class PlannerSchedule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      schedules: {},
      schedule: {},
      page: 1,
      totalPages: 1,
      noSchedulesPossible: false,
    };
    this.onOffset = this.onOffset.bind(this);
    this.exportToCart = this.exportToCart.bind(this);
  }

  generateSchedules() {
    let timeBlocks = [];

    // Get required combination of classes (section) / (section, subsection)
    for (const course of Object.entries(this.props.selectedCourses)) {
      let courseTimeBlocks = [];

      for (const section of Object.entries(course[1].sections)) {
        let tempSection = JSON.parse(JSON.stringify(section));
        tempSection[1].name = course[1].number;
        tempSection[1].title = this.formatSectionName(section[0]);
        tempSection[1].sectionId = section[0];
        tempSection[1].subsectionId = null;
        tempSection[1].id = course[0];
        delete tempSection[1].subsections;

        for (const t of Object.entries(section[1].time)) {
          let times = this.convertTimeString(t[1]);
          tempSection[1].time[t[0]] = [tempSection[1].time[t[0]], times[0], times[1]];
        }

        if (Object.keys(section[1].subsections).length === 0) {
          courseTimeBlocks.push([tempSection]);
        } else {
          for (const subsection of Object.entries(section[1].subsections)) {
            let tempSubsection = JSON.parse(JSON.stringify(subsection));
            tempSubsection[1].name = course[1].number;
            tempSubsection[1].title = this.formatSectionName(subsection[0]);
            tempSubsection[1].sectionId = section[0];
            tempSubsection[1].subsectionId = subsection[0];
            tempSubsection[1].id = course[0];

            for (const t of Object.entries(subsection[1].time)) {
              let times = this.convertTimeString(t[1]);
              tempSubsection[1].time[t[0]] = [tempSubsection[1].time[t[0]], times[0], times[1]];
            }

            courseTimeBlocks.push([tempSection, tempSubsection])
          }
        }
      }

      timeBlocks.push(courseTimeBlocks);
    }

    // Generate all possible combinations for courses
    let possibleSchedules = [];
    let possibleSchedulesDone = false;
    let counterArray = Array(timeBlocks.length).fill(0);
    let counterArrayMax = Array(timeBlocks.length).fill(0);
    let counterArrayIndex = timeBlocks.length - 1;
    let timeBlocksIndex = 1;
    let incrementOneTurn = false;

    for (const [idx, course] of timeBlocks.reverse().entries()) {
      counterArrayMax[idx] = (course.length - 1);
    }

    while(!possibleSchedulesDone) {
      let tempPossibility = [];

      for (let i = 0; i < timeBlocks.length; i++) {
        if (incrementOneTurn) {
          counterArray[counterArrayIndex]++;
          counterArray.fill(0, counterArrayIndex + 1);
          counterArrayIndex = timeBlocks.length - 1;
          timeBlocksIndex++;
          incrementOneTurn = false;
        }


        for (let j = timeBlocks.length - 1; j >= 0; j--) {
          tempPossibility.push(timeBlocks[j][counterArray[j]]);
        }

        possibleSchedules.push(tempPossibility);
        tempPossibility = [];

        if (counterArray[counterArrayIndex] < counterArrayMax[counterArrayIndex]) {
          counterArray[counterArrayIndex]++;
        } else {
          for (let k = 0; k < timeBlocksIndex; k++) {
            counterArrayIndex--;

            if (counterArrayIndex < 0) {
              possibleSchedulesDone = true;
              break;
            } else if (counterArray[counterArrayIndex] < counterArrayMax[counterArrayIndex]) {
              incrementOneTurn = true;
              break;
            }
          }
        }
      }
    }

    // Iterate over weekday of possibleSchedules and check for conflicts
    let generatedSchedules = {}
    let generatedSchedulesCounter = 1;

    for (const schedule of possibleSchedules) {
      let week = {monday: [], tuesday: [], wednesday: [], thursday: [], friday: []};

      for (const combination of schedule) {
        for (const course of combination) {
          for (const dayOfWeek of Object.keys(week)) {
            if (course[1].time[dayOfWeek] !== undefined) {
              let classBlock = {id: course[1].id, sectionId: course[1].sectionId, subsectionId: course[1].subsectionId, name: course[1].name, title: course[1].title, location: course[1].location, time: course[1].time[dayOfWeek][0], start: course[1].time[dayOfWeek][1], end: course[1].time[dayOfWeek][2]}
              week[dayOfWeek].push(classBlock);
            }
          }
        }
      }

      // Check if conflict exists
      let includeCombination = true;

      for (const dayOfWeek of Object.entries(week)) {
        if (!includeCombination) break;
        // Check courses
        for (const course of dayOfWeek[1]) {
          if (!includeCombination) break;
          // Compare it against other courses
          for (const otherCourse of dayOfWeek[1]) {
            if (!course.name.includes(otherCourse.name) && course.start >= otherCourse.start && course.start <= otherCourse.end) {
              includeCombination = false; 
              break;
            }
          }
        }
      }

      if (includeCombination) {
        generatedSchedules[generatedSchedulesCounter] = week;
        generatedSchedulesCounter++;
      }
    }

    if (Object.keys(generatedSchedules).length === 0) {
      this.setState({
        schedules: {},
        schedule: {},
        page: 1,
        totalPages: 1,
        noSchedulesPossible: true
      });
    } else {
      let totalPages = generatedSchedulesCounter - 1;

      this.setState({
        schedules: generatedSchedules, 
        schedule: generatedSchedules[1],
        page: 1,
        totalPages: totalPages,
        noSchedulesPossible: false,
      });
    }
  }

  convertTimeString(str) {
    let timesArray = [];
    let hyphenDivider = str.split('-');

    for (const time of hyphenDivider) {
      let columnDivider = time.split(':');
      let hour = parseInt(columnDivider[0], 10);
      let minutes = parseFloat(columnDivider[1].substring(0,2));

      if (columnDivider[1].substring(2,4) === 'pm') {
        if (hour < 12) {
          hour += 12;
        }
      }

      timesArray.push(hour + minutes/60);
    }

    return timesArray;
  }

  formatSectionName(str) {
    return str.replace(/_/g, ' ');
  }

  onOffset(offset) {
    const newPage = this.state.page + offset;
    this.setState({
      schedule: this.state.schedules[newPage],
      page: newPage
    });
  }

  getCredits() {
    let counter= 0;
  
    for (const selectedCourse of Object.keys(this.props.selectedCourses)) {
      for (const cartCourse of Object.entries(this.props.data)) {
        if (selectedCourse.includes(cartCourse[0])) {
          counter += cartCourse[1].credits;
        }
      }
    }
  
    return counter;
  }

  exportToCart() {
    let exportedSchedule = [];

    for (const dayOfWeek of Object.entries(this.state.schedule)) {
      for (const course of dayOfWeek[1]) {
        exportedSchedule.push([course.id, course.sectionId, course.subsectionId]);
      }
    }

    this.props.exportToCart(exportedSchedule);

    this.setState({
      schedules: {}, 
      schedule: {}, 
      page: 1,
      totalPages: 1
    })

    return;
  }

  render() {
    return (
      <Card className="Planner-schedule-container">
        <Schedule onNext={() => this.onOffset(1)} onPrevious={() => this.onOffset(-1)} page={this.state.page} totalPages={this.state.totalPages} data={this.props.data} selectedCourses={this.props.selectedCourses} schedule={this.state.schedule} selectedCredits={this.getCredits()} generateSchedules={() => this.generateSchedules()} exportToCart={() => this.exportToCart()} noSchedulesPossible={this.state.noSchedulesPossible}/>
      </Card>
    )
  }
}