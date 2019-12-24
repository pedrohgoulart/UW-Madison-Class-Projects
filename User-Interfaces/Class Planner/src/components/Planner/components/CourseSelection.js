import React from 'react';
import { Button, Card, Modal, Accordion } from 'react-bootstrap';

import {formatCredits, getSections} from '../../reusable/CourseReusable';

import '../css/CourseSelection.css';

export default class Course extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showSelectionModal: false
    };
  }

  setShowSelection(value) {
    this.setState({showSelectionModal: value});
  }

  getSectionsCount() {
    let sections = 0;
    let subsections = 0;

    if (this.props.selectedCourses[this.props.courseNum] !== undefined) {
      for (const section of Object.entries(this.props.selectedCourses[this.props.courseNum].sections)) {
        sections++;
        subsections += Object.keys(section[1].subsections).length;
      }
    }
    
    return [sections, subsections];
  }

  getConsideringString(sectionsCount) {
    let sections = sectionsCount[0];
    let subsections = sectionsCount[1];
    let string = "";
    
    if (sections === 1) {
      string = '1 section';
    } else {
      string = sections + ' sections';
    }

    if (subsections === 0) {
      return string;
    } else if (subsections === 1) {
      return string + ' \u2022 1 subsection';
    } else {
      return string + ' \u2022 ' + subsections + ' subsections';
    }
  }

  render() {
    let sectionsCount = this.getSectionsCount();
    let cardStyle = "Course-selection-container";
    let checkboxSelectionStatus = true;

    if (sectionsCount[0] === 0) {
      cardStyle += " Course-selection-empty";
      checkboxSelectionStatus = false;
    }

    return (
      <>
        <Card className={cardStyle} onClick={() => this.setShowSelection(true)}>
          <Card.Body>
            <Card.Title className="mb-2">{this.props.data.number} - {formatCredits(this.props.data.credits)}</Card.Title>
            <Card.Subtitle className="mb-2 text-muted">{this.props.data.name}</Card.Subtitle>
            <Card.Subtitle className="mb-2 text-muted">Considering: {this.getConsideringString(sectionsCount)}</Card.Subtitle>
            <Card.Subtitle><small>Click to edit selection</small></Card.Subtitle>
          </Card.Body>
        </Card>

        <Modal size="lg" show={this.state.showSelectionModal} onHide={() => this.setShowSelection(false)}>
          <Modal.Header closeButton>
            <Modal.Title>{this.props.data.number}</Modal.Title>
          </Modal.Header>

          <Modal.Body>
            <div className="mb-3">
              <Button className="float-right" size="sm" variant="custom-grey" onClick={() => this.props.triggerItem(this.props.courseNum, null, null, checkboxSelectionStatus)}>Select/Unselect all sections</Button>
              <h5>Sections:</h5>
            </div>
            
            <Accordion defaultActiveKey={Object.keys(this.props.data.sections)[0]}>
              {getSections(this.props.data.sections, this.props.triggerItem, this.props.selectedCourses, null, this.props.courseNum)}
            </Accordion>
          </Modal.Body>
        </Modal>
      </>
    )
  }
}