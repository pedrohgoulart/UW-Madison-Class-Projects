import React from 'react';
import { ScrollView, Text, TextInput, Alert } from 'react-native';
import DatePicker from 'react-native-datepicker'
import moment from "moment";

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class EditMeal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: this.props.meal.name,
      date: moment(new Date(this.props.meal.date)).format("LL"),
    }
  }

  updateMeal() {
    let meal = {
      id: this.props.meal.id,
      name: this.state.name,
      date: moment(new Date(this.state.date)).toISOString(),
    }

    this.props.updateMeals(meal);
  }

  removeMeal() {
    Alert.alert(
      'Delete Meal',
      'Are you sure you want to delete this meal? This action cannot be undone.',
      [
        {text: 'Cancel', style: 'cancel'},
        {text: 'Delete', onPress: () => this.props.removeMeals(this.props.meal.id)},
      ],
      {cancelable: true},
    );
  }

  render() {
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Edit Meal</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.marginVertical}>You can find the information for this meal below.</Text>
        
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
          text={'Save Changes'}
          onPress={() => this.updateMeal()}
        />

        <Button 
          buttonStyle={[styles.base, styles.button]}
          textStyle={styles.buttonText}
          text={'Delete Meal'}
          onPress={() => this.removeMeal()}
        />
      </ScrollView>
    );
  }
}

export default EditMeal;