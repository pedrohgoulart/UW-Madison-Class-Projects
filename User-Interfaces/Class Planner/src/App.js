import React from 'react';
import { Tabs, Tab, Toast } from 'react-bootstrap';

import 'bootstrap/dist/css/bootstrap.min.css';
import './css/App.css';

import SearchSidebar from './components/SearchAndFilter/SearchSidebar';
import PlannerSidebar from './components/Planner/PlannerSidebar';
import PlannerSchedule from './components/Planner/PlannerSchedule';
import CourseArea from './components/CourseArea';

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      key: 'search',
      allCourses: {},
      cartCourses: {},
      selectedCourses: {},
      filteredCourses: {},
      subjects: [],
      showToastSearch: false,
      showToastCart: false,
      showToastPlanner: false
    };
    this.addCartAction = this.addCartAction.bind(this);
    this.removeCartAction = this.removeCartAction.bind(this);
    this.triggerSelectedItem = this.triggerSelectedItem.bind(this);
    this.exportToCart = this.exportToCart.bind(this);
  }

  // Tabs functionality
  setKey(newKey) {
    this.setState({key: newKey});
  }

  // Toast messages
  setToastSearchShow(value) {
    this.setState({showToastSearch: value});
  }

  setToastCartShow(value) {
    this.setState({showToastCart: value});
  }

  setToastPlannerShow(value) {
    this.setState({showToastPlanner: value});
  }

  // Filter and Search functionality
  componentDidMount() {
    fetch('https://mysqlcs639.cs.wisc.edu:5000/classes/').then(
      res => res.json()
    ).then(data => this.setState({allCourses: data, filteredCourses: data, subjects: this.getSubjects(data)}));
  }

  getSubjects(data) {
    let subjects = [];
    subjects.push("All");

    for(const course of Object.values(data)) {
      if(subjects.indexOf(course.subject) === -1)
        subjects.push(course.subject);
    }

    return subjects;
  }

  setCourses(courses) {
    this.setState({filteredCourses: courses})
  }

  // Add to cart functionality
  addCartAction(courseNum, sectionNum, subsectionNum) {
    let courseInCart = false;

    for (const course of Object.keys(this.state.cartCourses)) {
      if (courseNum.includes(course)) {
        courseInCart = true;
        break;
      }
    }

    for (const course of Object.entries(this.state.allCourses)) {
      if (courseNum.includes(course[0])) {
        let cartList = JSON.parse(JSON.stringify(this.state.cartCourses));

        if (sectionNum == null && subsectionNum == null) {
          // Add all sections/subsections
          cartList[course[0]] = course[1];
        } else if (subsectionNum == null) {
          // Add all subsections of selected section
          if (courseInCart) {
            cartList[courseNum].sections[sectionNum] = course[1].sections[sectionNum];
          } else {
            let tempCourse = JSON.parse(JSON.stringify(course));
            let tempSection = {};
            tempSection[sectionNum] = course[1].sections[sectionNum];
            tempCourse[1].sections = tempSection;
            cartList[course[0]] = tempCourse[1];
          }
          
        } else {
          // Add specific subsection of selected section
          if (courseInCart) {
            cartList[courseNum].sections[sectionNum].subsections[subsectionNum] = course[1].sections[sectionNum].subsections[subsectionNum];
          } else {
            let tempCourse = JSON.parse(JSON.stringify(course));
            let tempSection = {};
            let tempSubsection = {};
            tempSection[sectionNum] = JSON.parse(JSON.stringify(course[1].sections[sectionNum]));
            tempSubsection[subsectionNum] = course[1].sections[sectionNum].subsections[subsectionNum];
            tempCourse[1].sections = tempSection;
            tempCourse[1].sections[sectionNum].subsections = tempSubsection;
            cartList[course[0]] = tempCourse[1];
          }
        }

        // Add course to cart
        this.setState({cartCourses: cartList});

        // Display confirmation message
        this.setToastSearchShow(true);

        return;
      }
    }
  }

  // Remove to cart functionality
  removeCartAction(courseNum, sectionNum, subsectionNum) {
    for (const course of Object.entries(this.state.cartCourses)) {
      if (courseNum.includes(course[0])) {
        let cartList = JSON.parse(JSON.stringify(this.state.cartCourses));

        if (sectionNum == null && subsectionNum == null) {
          delete cartList[courseNum];
        } else if (subsectionNum == null) {
          if (Object.entries(cartList[courseNum].sections).length <= 1) {
            delete cartList[courseNum];
          } else {
            delete cartList[courseNum].sections[sectionNum];
          }
        } else {
          if (Object.entries(cartList[courseNum].sections[sectionNum].subsections).length <= 1) {
            if (Object.entries(cartList[courseNum].sections).length <= 1) {
              delete cartList[courseNum];
            } else {
              delete cartList[courseNum].sections[sectionNum];
            }
          } else {
            delete cartList[courseNum].sections[sectionNum].subsections[subsectionNum];
          }
        }
        
        // Trigger re-render
        this.setState({cartCourses: cartList});

        // Display confirmation message
        this.setToastCartShow(true);

        return;
      }
    }
  }

  triggerSelectedItem(courseNum, sectionNum, subsectionNum, removeFromList) {
    if (removeFromList) {
      this.unselectItem(courseNum, sectionNum, subsectionNum)
    } else {
      this.selectItem(courseNum, sectionNum, subsectionNum)
    }
  }

  selectItem(courseNum, sectionNum, subsectionNum) {
    let courseInList = false;

    for (const course of Object.keys(this.state.selectedCourses)) {
      if (courseNum.includes(course)) {
        courseInList = true;
        break;
      }
    }

    for (const course of Object.entries(this.state.cartCourses)) {
      if (courseNum.includes(course[0])) {
        let selectedList = JSON.parse(JSON.stringify(this.state.selectedCourses));


        if (sectionNum == null && subsectionNum == null) {
          // Add all sections/subsections
          selectedList[course[0]] = {sections: course[1].sections, number: course[1].number};
        } else if (subsectionNum == null) {
          // Add all subsections of selected section
          if (courseInList) {
            selectedList[courseNum].sections[sectionNum] = course[1].sections[sectionNum];
          } else {
            let tempCourse = JSON.parse(JSON.stringify(course));
            let tempSection = {};
            tempSection[sectionNum] = course[1].sections[sectionNum];
            tempCourse[1].sections = tempSection;
            selectedList[course[0]] = {sections: tempCourse[1].sections, number: course[1].number};
          }
          
        } else {
          // Add specific subsection of selected section
          if (courseInList) {
            selectedList[courseNum].sections[sectionNum].subsections[subsectionNum] = course[1].sections[sectionNum].subsections[subsectionNum];
          } else {
            let tempCourse = JSON.parse(JSON.stringify(course));
            let tempSection = {};
            let tempSubsection = {};
            tempSection[sectionNum] = JSON.parse(JSON.stringify(course[1].sections[sectionNum]));
            tempSubsection[subsectionNum] = course[1].sections[sectionNum].subsections[subsectionNum];
            tempCourse[1].sections = tempSection;
            tempCourse[1].sections[sectionNum].subsections = tempSubsection;
            selectedList[course[0]] = {sections: tempCourse[1].sections, number: course[1].number};
          }
        }

        // Add course to cart
        this.setState({selectedCourses: selectedList});
        
        return;
      }
    }
  }

  unselectItem(courseNum, sectionNum, subsectionNum) {
    for (const course of Object.entries(this.state.selectedCourses)) {
      if (courseNum.includes(course[0])) {
        let selectedList = JSON.parse(JSON.stringify(this.state.selectedCourses));

        if (sectionNum == null && subsectionNum == null) {
          delete selectedList[courseNum];
        } else if (subsectionNum == null) {
          if (Object.entries(selectedList[courseNum].sections).length <= 1) {
            delete selectedList[courseNum];
          } else {
            delete selectedList[courseNum].sections[sectionNum];
          }
        } else {
          if (Object.entries(selectedList[courseNum].sections[sectionNum].subsections).length <= 1) {
            if (Object.entries(selectedList[courseNum].sections).length <= 1) {
              delete selectedList[courseNum];
            } else {
              delete selectedList[courseNum].sections[sectionNum];
            }
          } else {
            delete selectedList[courseNum].sections[sectionNum].subsections[subsectionNum];
          }
        }
        
        // Trigger re-render
        this.setState({selectedCourses: selectedList});
        return;
      }
    }
  }

  exportToCart(exportedSchedule) {
    let cartList = {};

    for (const course of Object.entries(this.state.cartCourses)) {
      for (const coursesToExport of exportedSchedule) {
        if (coursesToExport[0].includes(course[0])) {
          let tempCourse = JSON.parse(JSON.stringify(this.state.cartCourses[course[0]]));
          let tempSection = JSON.parse(JSON.stringify(course[1].sections[coursesToExport[1]]));
          tempCourse.sections = {};
          tempCourse.sections[coursesToExport[1]] = tempSection;

          if (coursesToExport[2] !== null) {
            let tempSubsection = JSON.parse(JSON.stringify(course[1].sections[coursesToExport[1]].subsections[coursesToExport[2]]));
            tempCourse.sections[coursesToExport[1]].subsections = {};
            tempCourse.sections[coursesToExport[1]].subsections[coursesToExport[2]] = tempSubsection;
          }

          cartList[coursesToExport[0]] = tempCourse;
          break;
        }
      }
    }

    // Trigger re-render
    this.setState({cartCourses: cartList, selectedCourses: {}});

    // Display confirmation message
    this.setToastPlannerShow(true);

    return;
  }

  // Render main page
  render() {
    return (
      <div className={'App-container'}>
        <Tabs activeKey={this.state.key} onSelect={k => this.setKey(k)} className={'App-tabs'}>
          <Tab eventKey="search" title="Search and Filter">
            <SearchSidebar setCourses={(courses) => this.setCourses(courses)} courses={this.state.allCourses} subjects={this.state.subjects}/>
            <CourseArea data={this.state.filteredCourses} partOfCart={false} cartAction={(c, s, ss) => this.addCartAction(c, s, ss)}/>
          </Tab>
          <Tab eventKey="cart" title={"Cart (" + Object.values(this.state.cartCourses).length + ")"}>
            <CourseArea data={this.state.cartCourses} partOfCart={true} cartAction={(c, s, ss) => this.removeCartAction(c, s, ss)} />
          </Tab>
          <Tab eventKey="planner" title={"Planner"}>
            <PlannerSidebar data={this.state.cartCourses} selectedCourses={this.state.selectedCourses} selectAction={(c, s, ss, v) => this.triggerSelectedItem(c, s, ss, v)}/>
            <PlannerSchedule data={this.state.cartCourses} selectedCourses={this.state.selectedCourses} exportToCart={(exportedSchedule) => this.exportToCart(exportedSchedule)} />
          </Tab>
        </Tabs>

        <Toast onClose={() => this.setToastSearchShow(false)} show={this.state.showToastSearch} delay={2500} className={"Toast-message"} autohide>
          <Toast.Header>
            <strong className="mr-auto">Course added to cart</strong>
          </Toast.Header>
          <Toast.Body>Click on the 'Cart' tab to view items or click 'Planner' to generate schedules.</Toast.Body>
        </Toast>

        <Toast onClose={() => this.setToastCartShow(false)} show={this.state.showToastCart} delay={2500} className={"Toast-message"} autohide>
          <Toast.Header>
            <strong className="mr-auto">Course removed from cart</strong>
          </Toast.Header>
          <Toast.Body>Click on the 'Search and Filter' tab to add more items or go to 'Planner' to generate schedules.</Toast.Body>
        </Toast>

        <Toast onClose={() => this.setToastPlannerShow(false)} show={this.state.showToastPlanner} delay={2500} className={"Toast-message"} autohide>
          <Toast.Header>
            <strong className="mr-auto">Courses exported to cart</strong>
          </Toast.Header>
          <Toast.Body>Click on the 'Cart' tab to view items.</Toast.Body>
        </Toast>
      </div>
    )
  }
}