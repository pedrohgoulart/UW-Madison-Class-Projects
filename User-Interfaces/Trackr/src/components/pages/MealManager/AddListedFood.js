import React from 'react';
import { ScrollView, Text } from 'react-native';
import { ListItem } from 'react-native-elements';
import Ionicons from 'react-native-vector-icons/Ionicons';

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class AddListedFood extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: null,
      listOfFood: []
    }
  }

  async componentDidMount() {
    let foodResponse = await fetch('https://mysqlcs639.cs.wisc.edu/foods/', { 
      method: 'GET',
      redirect: 'follow'
    }).then(response => response.json());

    if (foodResponse.foods) {
      this.setState({listOfFood: foodResponse.foods})
    } else {
      this.setState({message: String(foodResponse)})
    }
  }

  renderMacronutirentsString(protein, carbohydrates, fat) {
    return ("Protein: " + protein + "g \u2022 " + "Carbs: " + carbohydrates +  "g \u2022 " + "Fat: " + fat + "g")
  }

  render() {
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Add Food</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.extraMarginTop}>Select from the foods below to add them to your meal.</Text>

        <Button 
          buttonStyle={[styles.base, styles.button, {marginBottom: 20}]}
          textStyle={styles.buttonText}
          text={'Add Custom Food'}
          onPress={() => this.props.switchToCustom()}
        />

        {!!this.state.message && (
          <Text style={[styles.message, styles.extraMarginTop]}>{this.state.message}</Text>
        )}

        {this.state.listOfFood.map((food) => {
          return (
            <ListItem 
              key={food.id}
              title={food.name + " (" + food.calories + " Kcal)"}
              subtitle={this.renderMacronutirentsString(food.protein, food.carbohydrates, food.fat)}
              titleStyle={styles.listItemTitle}
              subtitleStyle={styles.listItemSubtitle}
              rightIcon={<Ionicons name={"ios-add"} size={20} color={"#222"} onPress={() => this.props.addFood(food)} />}
              topDivider
            />
          )
        })}
      </ScrollView>
    );
  }
}

export default AddListedFood;