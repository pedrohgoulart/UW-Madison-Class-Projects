import React from 'react';
import { ScrollView, Text, TextInput } from 'react-native';
import DatePicker from 'react-native-datepicker';
import moment from "moment";

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class AddActivity extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: null,
      duration: 0,
      date: moment(new Date()).format("llll"),
      calories: 0
    }
  }

  addActivity() {
    let activity = {
      name: this.state.name,
      duration: this.state.duration,
      date: moment(new Date(this.state.date)).toISOString(),
      calories: this.state.calories
    }

    this.props.addActivities(activity);
  }

  render() {
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Add Activity</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.marginVertical}>Fill in below to add an activity.</Text>
        
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
          text={'Add Activity'} 
          onPress={() => this.addActivity()}
        />
      </ScrollView>
    );
  }
}

export default AddActivity;