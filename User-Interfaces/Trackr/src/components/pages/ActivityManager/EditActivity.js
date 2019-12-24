import React from 'react';
import { ScrollView, Text, TextInput, Alert } from 'react-native';
import DatePicker from 'react-native-datepicker'
import moment from "moment";

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class EditActivity extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: this.props.openedActivity.name,
      duration: this.props.openedActivity.duration,
      date: moment(new Date(this.props.openedActivity.date)).format("llll"),
      calories: this.props.openedActivity.calories
    }
  }

  updateActivity() {
    let activity = {
      id: this.props.openedActivity.id,
      name: this.state.name,
      duration: this.state.duration,
      date: moment(new Date(this.state.date)).toISOString(),
      calories: this.state.calories
    }

    this.props.updateActivities(activity);
  }

  removeActivity() {
    Alert.alert(
      'Delete Activity',
      'Are you sure you want to delete this activity? This action cannot be undone.',
      [
        {text: 'Cancel', style: 'cancel'},
        {text: 'Delete', onPress: () => this.props.removeActivities(this.props.openedActivity.id)},
      ],
      {cancelable: true},
    );
  }

  render() {
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Edit Activity</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.marginVertical}>You can find the information for this activity below.</Text>

        <Text>Name</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({name: text})} 
          value={this.state.name} 
          returnKeyType={"done"} 
          placeholder={"Name"} 
        />

        <Text>Date</Text>
        <DatePicker
          style={styles.dateInput}
          customStyles={{dateInput: [styles.base, styles.textInput, {borderWidth: 0}]}}
          showIcon={false}
          onDateChange={(date) => this.setState({date: date})} 
          date={this.state.date}
          mode="datetime"
          format="llll"
          confirmBtnText="Confirm"
          cancelBtnText="Cancel" 
        />

        <Text>Duration (minutes)</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({duration: text})} 
          value={String(this.state.duration)} 
          returnKeyType={"done"} 
          placeholder={"Duration"} 
        />

        <Text>Calories</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({calories: text})} 
          value={String(this.state.calories)} 
          returnKeyType={"done"} 
          placeholder={"Calories"}
        />

        <Button 
          buttonStyle={[styles.base, styles.button]}
          textStyle={styles.buttonText}
          text={'Save Changes'} 
          onPress={() => this.updateActivity()}
        />

        <Button 
          buttonStyle={[styles.base, styles.button]}
          textStyle={styles.buttonText}
          text={'Delete Activity'}
          onPress={() => this.removeActivity()}
        />
      </ScrollView>
    );
  }
}

export default EditActivity;