import React from 'react';
import { Button, Card, Accordion } from 'react-bootstrap';
import Checkbox from './Checkbox';

import '../css/Course.css';

export function formatCredits(credits) {
  if (credits === 1) {
    return '1 credit';
  } else {
    return credits + ' credits';
  }
}

export function getSections(sections, action, selectionList, partOfCart, courseNum) {
  return (
    Object.entries(sections).map((section) => {
      let subsectionsCount = Object.keys(section[1].subsections).length;
      let hasSubsections = (subsectionsCount > 0 ? true : false);

      let accordionToggleClass = (selectionList ? "Modal-accordion-row-container" : "");

      if (hasSubsections) {
        accordionToggleClass += " Card-header-selectable";
      }

      return (
        <Card key={section[0]}>
          <Accordion.Toggle as={Card.Header} eventKey={section[0]} className={accordionToggleClass}>
            {courseSectionFunction(selectionList, hasSubsections, partOfCart, courseNum, action, section[0])}
            <div className="Modal-accordion-row-details">
              <p className="m-0"><strong>{formatSectionName(section[0])} - {section[1].instructor}</strong></p>
              <p className="m-0"><small>{formatSectionTime(section[1].time)} {section[1].location}</small></p>
              <small>{hasSubsections ? "Click to show subsections" : ""}</small>
            </div>
          </Accordion.Toggle>
          {getSubsections(selectionList, hasSubsections, section, action, partOfCart, courseNum)}
        </Card>
      )
    })
  )
}

function courseSectionFunction(selectionList, hasSubsections, partOfCart, courseNum, action, section) {
  if (selectionList != null) {
    return <Checkbox triggerAction={action} courseNum={courseNum} section={section} subsection={null} selectionList={selectionList} />
  } else {
    return <Button className="float-right" size="sm" variant={hasSubsections ? "custom-grey" : "custom-red"} onClick={() => action(courseNum, section, null)}>{getSectionButtonText(hasSubsections, partOfCart)}</Button>
  }
}

function getSectionButtonText(hasSubsections, partOfCart) {
  if (hasSubsections) {
    return partOfCart ? "Remove all subsections" : "Add all subsections";
  } else {
    return partOfCart ? "Remove section" : "Add section";
  }
}

function formatSectionName(str) {
  return str.replace(/_/g, ' ');
}

function formatSectionTime(times) {
  return(
    Object.entries(times).map((time) => {
      return (time[0].substring(0,3).toUpperCase() + " (" + time[1] + "), ");
    })
  )
}

function getSubsections(selectionList, hasSubsections, section, action, partOfCart, courseNum) {
  if (hasSubsections) {
    return (
      <Accordion.Collapse eventKey={section[0]}>
        <Card.Body className="Modal-subsection-card-body">
          {Object.entries(section[1].subsections).map((subsection) => {
            let accordionToggleClass = (selectionList ? "Modal-subsection-row Modal-accordion-row-container" : "Modal-subsection-row");
            
            return (
              <div key={subsection} className={accordionToggleClass}>
                {courseSubsectionFunction(action, partOfCart, selectionList, courseNum, section[0], subsection[0])}
                <div className="Modal-accordion-row-details">
                  <p className="m-0"><strong>{formatSectionName(subsection[0])}</strong></p>
                  <p className="m-0"><small>{formatSectionTime(subsection[1].time)} {subsection[1].location}</small></p>
                </div>
              </div>
            )
          })}
        </Card.Body>
      </Accordion.Collapse>
    )
  }
}

function courseSubsectionFunction(action, partOfCart, selectionList, courseNum, section, subsection) {
  if (selectionList != null) {
    return <Checkbox triggerAction={action} courseNum={courseNum} section={section} subsection={subsection} selectionList={selectionList} />
  } else {
    return <Button className="float-right" size="sm" variant="custom-red" onClick={() => action(courseNum, section, subsection)}>{partOfCart ? "Remove subsection" : "Add subsection"}</Button>
  }
}