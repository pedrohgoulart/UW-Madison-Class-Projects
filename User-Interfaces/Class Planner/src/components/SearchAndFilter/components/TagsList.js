import React from 'react';
import { Button, Badge } from 'react-bootstrap';

import '../css/TagsList.css';

export default class TagsList extends React.Component {
  constructor(props) {
      super(props);
      this.state = {
          tagList: []
      };
  }
  
  UNSAFE_componentWillReceiveProps(nextProps) {
    this.setState({tagList: nextProps.items});
  }
  
  render() {
      return (
        <>
          {this.state.tagList.map((item, index) => (
            <Badge key={item + index} pill variant="secondary" className="Badge-margin">
              {item}
              <Button variant="dark" onClick={() => this.props.delete(index)} className="Tags">X</Button>
            </Badge>
          ))}
        </>
      )
  }
}