import React from 'react';
import { Form } from 'react-bootstrap';

export default class Checkbox extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selected: false
    };
  }

  triggerCheckbox() {
    let value = this.checkCourseInList();
    this.props.triggerAction(this.props.courseNum, this.props.section, this.props.subsection, value);
    this.setState({selected: !value})
  }

  checkCourseInList() {
    let courseInList = false;

    for (const course of Object.entries(this.props.selectionList)) {
      if (this.props.courseNum.includes(course[0])) {
        for (const section of Object.entries(course[1].sections)) {
          if (this.props.section.includes(section[0])) {
            if (this.props.subsection == null) {
              courseInList = true;
              break;
            } else {
              for (const subsection of Object.entries(section[1].subsections)) {
                if (this.props.subsection.includes(subsection[0])) {
                  courseInList = true;
                  break;
                }
              }
              break;
            }
          }
        }
        break;
      }
    }

    return courseInList;
  }

  render() {
    return (
      <Form.Check inline className="Modal-accordion-row-selection" checked={this.checkCourseInList()} onChange={() => this.triggerCheckbox()}/>
    )
  }
}