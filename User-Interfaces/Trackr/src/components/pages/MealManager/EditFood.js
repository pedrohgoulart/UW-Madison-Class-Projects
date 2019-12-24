import React from 'react';
import { ScrollView, Text, TextInput, Alert } from 'react-native';

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class EditFood extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      name: this.props.food.name,
      calories: this.props.food.calories,
      protein: this.props.food.protein,
      carbohydrates: this.props.food.carbohydrates,
      fat: this.props.food.fat
    }
  }

  updateFood() {
    let food = {
      id: this.props.food.id,
      name: this.state.name,
      calories: this.state.calories,
      protein: this.state.protein,
      carbohydrates: this.state.carbohydrates,
      fat: this.state.fat
    }

    this.props.updateFood(food);
  }

  removeFood() {
    Alert.alert(
      'Delete Food',
      'Are you sure you want to delete this food? This action cannot be undone.',
      [
        {text: 'Cancel', style: 'cancel'},
        {text: 'Delete', onPress: () => this.props.removeFood(this.props.food.id)},
      ],
      {cancelable: true},
    );
  }

  render() {
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Edit Food</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.marginVertical}>You can find the information for this food below.</Text>

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
          text={'Save Changes'}
          onPress={() => this.updateFood()}
        />

        <Button 
          buttonStyle={[styles.base, styles.button]}
          textStyle={styles.buttonText}
          text={'Delete Food'}
          onPress={() => this.removeFood()}
        />
      </ScrollView>
    );
  }
}

export default EditFood;