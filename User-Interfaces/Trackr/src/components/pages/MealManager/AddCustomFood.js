import React from 'react';
import { ScrollView, Text, TextInput } from 'react-native';

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class AddCustomFood extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: null,
      calories: 0,
      protein: 0,
      carbohydrates: 0,
      fat: 0
    }
  }

  addFood() {
    let food = {
      name: this.state.name,
      calories: this.state.calories,
      protein: this.state.protein,
      carbohydrates: this.state.carbohydrates,
      fat: this.state.fat
    }

    this.props.addFood(food);
  }

  render() {
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Add Custom Food</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.extraMarginTop}>Fill in below to add food to the selected meal.</Text>

        <Button 
          buttonStyle={[styles.base, styles.button, {marginBottom: 20}]}
          textStyle={styles.buttonText}
          text={'Back to List'}
          onPress={() => this.props.switchToList()}
        />

        <Text>Name</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({name: text})} 
          value={this.state.name} 
          returnKeyType={"done"} 
          placeholder={"Name"} 
        />

        <Text>Calories</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({calories: text})} 
          value={String(this.state.calories)} 
          keyboardType={"decimal-pad"} 
          returnKeyType={"done"} 
          placeholder={"Calories"} 
        />

        <Text>Protein (grams)</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({protein: text})} 
          value={String(this.state.protein)} 
          keyboardType={"decimal-pad"} 
          returnKeyType={"done"} 
          placeholder={"Protein"} 
        />

        <Text>Carbohydrates (grams)</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({carbohydrates: text})} 
          value={String(this.state.carbohydrates)} 
          keyboardType={"decimal-pad"} 
          returnKeyType={"done"} 
          placeholder={"Carbohydrates"} 
        />

        <Text>Fat (grams)</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({fat: text})} 
          value={String(this.state.fat)}
          keyboardType={"decimal-pad"} 
          returnKeyType={"done"} 
          placeholder={"Fat"} 
        />

        <Button 
          buttonStyle={[styles.base, styles.button]}
          textStyle={styles.buttonText}
          text={'Add Food'}
          onPress={() => this.addFood()}
        />
      </ScrollView>
    );
  }
}

export default AddCustomFood;