import React from 'react';
import { Dimensions } from 'react-native';
import { Overlay } from 'react-native-elements';

class OverlayItem extends React.Component {
    render() {
        return (
            <Overlay
                isVisible={this.props.isVisible}
                windowBackgroundColor="rgba(0, 0, 0, .5)"
                overlayBackgroundColor="#fff"
                onBackdropPress={this.props.onBackdropPress}
                width={Dimensions.get('window').width - 24}
                height={Dimensions.get('window').height/1.5}
                borderRadius={8}
            >
                {this.props.children}
            </Overlay>
        )
    }
}

export default OverlayItem;