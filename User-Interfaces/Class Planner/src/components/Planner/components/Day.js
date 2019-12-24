import React from 'react';
import { Card } from 'react-bootstrap';

class Day extends React.Component {

  // Day Component
  // Props:
  //  title (string)
  //  blocks, an array of objects with properties:
  //          - name (string)
  //          - start (float)
  //          - end (float))
  //  start (float) start as float
  //  end (float) end as float
  //  height (int) height - Must be numeric!
  //  width (int or string)

  getBlocks() {
    let blockComponents = [];

    for (const blockData of this.props.blocks) {
      let pxHeight = this.props.height*(blockData.end - blockData.start)/(this.props.end-this.props.start);
      if (pxHeight < 60) {
        pxHeight = 60;
      }
      let pxY = this.props.height*(blockData.start-this.props.start)/(this.props.end-this.props.start)

      blockComponents.push(
        <Card key={blockData.id + "-" + blockData.sectionId + "-" + this.props.title} style={{height:pxHeight, marginTop:pxY}} className={"Schedule-table-item"}>
          <p className="mb-0"><small className="font-weight-bold">{blockData.name} ({blockData.title})</small></p>
          <p className="mb-0"><small>{blockData.location}<br/>{blockData.time}</small></p>
        </Card>
      );
    }

    return blockComponents;
  }

  Capitalize(str){
    return str.charAt(0).toUpperCase() + str.slice(1);
  }

  render() {
    return (
      <Card className={"Schedule-table-column"}>
        <Card.Header className="square">{this.Capitalize(this.props.title)}</Card.Header>
        <Card.Body style={{padding: '0.2rem'}}>
          {this.getBlocks()}
        </Card.Body>
      </Card>
    )
  }

}

export default Day; 