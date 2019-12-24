import React from 'react';
import { Button, Card, Modal, Accordion } from 'react-bootstrap';

import {formatCredits, getSections} from './reusable/CourseReusable';

import './css/Course.css';
import './css/Buttons.css';

export default class Course extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      showCartModal: false
    };
  }

  setShowCart(value) {
    this.setState({showCartModal: value});
  }

  render() {
    
    return (
      <>
        <Card className="App-course-container">
          <Card.Body>
            <Button variant="custom-red" className="float-right" onClick={() => this.setShowCart(true)}>{this.props.partOfCart ? "Remove from cart" : "Add to cart"}</Button>
            <Card.Title>{this.props.data.name}</Card.Title>
            <Card.Subtitle className="text-muted">{this.props.data.number} - {formatCredits(this.props.data.credits)}</Card.Subtitle>
          </Card.Body>
        </Card>

        <Modal size="lg" show={this.state.showCartModal} onHide={() => this.setShowCart(false)}>
          <Modal.Header closeButton>
            <Modal.Title>{this.props.data.number}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <h5>Description:</h5>
            <p>{this.props.data.description}</p>

            <div className="mb-3">
              <Button className="float-right" size="sm" variant="custom-grey" onClick={() => this.props.cartAction(this.props.courseNum, null, null)}>{this.props.partOfCart ? "Remove all sections" : "Add all sections"}</Button>
              <h5>Sections:</h5>
            </div>
            
            <Accordion defaultActiveKey={Object.keys(this.props.data.sections)[0]}>
              {getSections(this.props.data.sections, this.props.cartAction, null, this.props.partOfCart, this.props.courseNum)}
            </Accordion>
          </Modal.Body>
        </Modal>
      </>
    )
  }
}