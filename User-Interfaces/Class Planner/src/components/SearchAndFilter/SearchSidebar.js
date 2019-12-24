import React from 'react';
import { Card, Form } from 'react-bootstrap';

import './css/SearchSidebar.css';

import SearchAndFilter from './components/SearchFilter';
import TagsList from './components/TagsList';

export default class SearchSidebar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tagsList: []
    };
    this.searchAndFilter = new SearchAndFilter();
    this.subject = React.createRef();
    this.minimumCredits = React.createRef();
    this.maximumCredits = React.createRef();
    this.tags = React.createRef();
    this.tagsAND = React.createRef();
    this.addItem = this.addItem.bind(this);
    this.removeItem = this.removeItem.bind(this);
  }

  setCourses() {
    this.props.setCourses(this.searchAndFilter.searchAndFilter(this.props.courses, this.tags.current.state.tagList, this.tagsAND.current.checked, this.subject.current.value, this.minimumCredits.current.value, this.maximumCredits.current.value));
  }

  handleCreditsKeyDown(e) {
    if(['0','1','2','3','4','5','6','7','8','9','Backspace','ArrowLeft','ArrowRight','ArrowUp','ArrowDown','Tab'].indexOf(e.key) === -1)
      e.preventDefault();
  }

  handleAddTag(e) {
    if (e.key === 'Enter') {
      this.addItem();
    }
  }

  getSubjectOptions() {
    let subjectOptions = [];

    for(const subject of this.props.subjects) {
      subjectOptions.push(<option key={subject}>{subject}</option>);
    }

    return subjectOptions;
  }

  addItem() {
    let tempList = this.state.tagsList;
    const tagsInputItem = document.getElementById("tagsInput");

    tempList.push(tagsInputItem.value.toLowerCase());
    tagsInputItem.value = "";

    this.setState({tagsList: tempList}, () => {this.setCourses()});
  }

  removeItem(index) {
    const tempList = this.state.tagsList.slice();
    tempList.splice(index, 1);

    this.setState({tagsList: tempList}, () => {this.setCourses()});
  }

  render() {
    return (
      <Card className="App-sidebar">
        <Card.Body>
          <Card.Title>Search and Filter</Card.Title>
          <Form>
            <Form.Group controlId="formKeywords" style={{width: '100%'}}>
              <Form.Label>Search</Form.Label>
              <input type="text" className="form-control mb-3" id="tagsInput" placeholder="Search..." onKeyDown={(e) => this.handleAddTag(e)}/>
              <TagsList items={this.state.tagsList} delete={this.removeItem} ref={this.tags}/>
              <div className='custom-control custom-switch mt-3'>
                <input type='checkbox' className='custom-control-input' id='and-or-switch' readOnly ref={this.tagsAND} onChange={() => this.setCourses()}/>
                <label className='custom-control-label' htmlFor='and-or-switch'>Use AND tag conjunction (default: OR)</label>
              </div>
            </Form.Group>

            <Form.Group controlId="formSubject">
              <Form.Label>Subject</Form.Label>
              <Form.Control as="select" ref={this.subject} onClick={() => this.setCourses()}>
                {this.getSubjectOptions()}
              </Form.Control>
            </Form.Group>

            <div style={{display: 'flex', flexDirection: 'row'}}>
              <Form.Group controlId="minimumCredits" onChange={() => this.setCourses()} onKeyDown={(e) => this.handleCreditsKeyDown(e)}>
                <Form.Label>Credits</Form.Label>
                <Form.Control type="text" placeholder="minimum" autoComplete="off" ref={this.minimumCredits}/>
              </Form.Group>
              <div style={{marginLeft: '5px', marginRight: '5px', marginTop: '38px'}}>to</div>
              <Form.Group controlId="maximumCredits" style={{marginTop: '32px'}} onChange={() => this.setCourses()} onKeyDown={(e) => this.handleCreditsKeyDown(e)}>
                <Form.Control type="text" placeholder="maximum" autoComplete="off" ref={this.maximumCredits}/>
              </Form.Group>
            </div>
          </Form>
        </Card.Body>
      </Card>
    )
  }
}