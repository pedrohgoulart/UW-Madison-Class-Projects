import React from 'react';
import { ScrollView, Text, TextInput } from 'react-native';
import DatePicker from 'react-native-datepicker';
import moment from "moment";

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class AddMeal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: null,
      date: moment(new Date()).format("LL"),
    }
  }

  addMeal() {
    let meal = {
      name: this.state.name,
      date: moment(new Date(this.state.date)).toISOString(),
    }

    this.props.addMeals(meal);
  }

  render() {
    return (
      <ScrollView >
        <Text style={{fontSize: 25}}>Add Meal</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.marginVertical}>Fill in below to add a meal.</Text>
        
        <Text>Title</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({name: text})} 
          value={this.state.name} 
          returnKeyType={"done"} 
          placeholder={"Title"} 
        />

        <Text>Date</Text>
        <DatePicker
          style={styles.dateInput}
          customStyles={{dateInput: [styles.base, styles.textInput, {borderWidth: 0}]}}
          showIcon={false}
          onDateChange={(date) => this.setState({date: date})} 
          date={this.state.date}
          mode="datetime"
          format="LL"
          confirmBtnText="Confirm"
          cancelBtnText="Cancel" 
        />

        <Button 
          buttonStyle={[styles.base, styles.button]}
          textStyle={styles.buttonText}
          text={'Add Meal'}
          onPress={() => this.addMeal()}
        />
      </ScrollView>
    );
  }
}

export default AddMeal;