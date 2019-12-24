import React from 'react';
import { SafeAreaView, ScrollView, View, Text, TouchableHighlight } from 'react-native';
import { Header, Card, ListItem } from 'react-native-elements';
import Ionicons from 'react-native-vector-icons/Ionicons';
import moment from "moment";

import AddMeal from './AddMeal';
import EditMeal from './EditMeal';
import AddListedFood from './AddListedFood';
import AddCustomFood from './AddCustomFood';
import EditFood from './EditFood';

import OverlayItem from '../../reusable/OverlayItem';
import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class MealManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: null,
      date: null,
      listOfMeals: [],
      showAddMeal: false,
      showAddListedFood: false,
      showAddCustomFood: false,
      foodMealID: null,
      openedMeal: null,
      openedFood: null
    }
  }

  componentDidMount() {
    this.loadStats(moment().format('LL'), null);
  }

  async loadStats(currDate, message) {
    // Get meals list
    let tempMeals = await this.props.screenProps.loadMeals(currDate);
    if (!Array.isArray(tempMeals)) {
      this.setState({
        message: String(tempMeals),
        openedMeal: null,
        showAddMeal: false,
        showAddListedFood: false,
        showAddCustomFood: false,
        foodMealID: null,
        openedMeal: null,
        openedFood: null
      })
      return;
    }

    meals = [];

    tempMeals.map((meal) => {
      // Ensures array is not an error string
      if (Array.isArray(meal.foods)) {
        // Update calories, protein, carbs, and fat for meal
        let caloriesCount = 0;
        let proteinCount = 0;
        let carbsCount = 0;
        let fatCount = 0;

        meal.foods.map((food) => {
          caloriesCount += food.calories;
          proteinCount += food.protein;
          carbsCount += food.carbohydrates;
          fatCount += food.fat;
        })

        let detailedMeal = JSON.parse(JSON.stringify(meal));
        detailedMeal.caloriesCount = caloriesCount;
        detailedMeal.proteinCount = proteinCount;
        detailedMeal.carbsCount = carbsCount;
        detailedMeal.fatCount = fatCount;
        meals.push(detailedMeal);
      }
    })

    this.setState({
      message: message,
      date: currDate,
      listOfMeals: meals,
      openedMeal: null,
      showAddMeal: false,
      showAddListedFood: false,
      showAddCustomFood: false,
      foodMealID: null,
      openedMeal: null,
      openedFood: null
    });
  }

  defaultErrorMessage() {
    return "An error occurred and we could not perform the action. Please try again later."
  }

  async addMeals(token, meal) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let mealResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/', { 
      method: 'POST',
      headers: requestHeader,
      body: JSON.stringify({
        name: meal.name,
        date: meal.date,
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (mealResponse.message) {
      this.loadStats(moment(new Date(meal.date)).format("LL"), mealResponse.message);
    } else {
      this.loadStats(moment(new Date(meal.date)).format("LL"), this.defaultErrorMessage());
    }
  }

  async updateMeals(token, meal) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let mealResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/' + meal.id, { 
      method: 'PUT',
      headers: requestHeader,
      body: JSON.stringify({
        name: meal.name,
        date: meal.date,
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (mealResponse.message) {
      this.loadStats(moment(new Date(meal.date)).format("LL"), mealResponse.message);
    } else {
      this.loadStats(moment(new Date(meal.date)).format("LL"), this.defaultErrorMessage());
    }
  }

  async removeMeals(token, mealID) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token);

    // Update user info by calling API
    let mealResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/' + mealID, { 
      method: 'DELETE',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (mealResponse.message) {
      this.loadStats(this.state.date, mealResponse.message);
    } else {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }
  }

  async addFood(token, food) {
    // Check if meal ID is set
    if (this.state.foodMealID === null) {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }

    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token);

    // Update user info by calling API
    let foodResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/' + this.state.foodMealID + '/foods/', { 
      method: 'POST',
      headers: requestHeader,
      body: JSON.stringify({
        name: food.name,
        calories: food.calories,
        protein: food.protein,
        carbohydrates: food.carbohydrates,
        fat: food.fat,
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (foodResponse.message) {
      this.loadStats(this.state.date, foodResponse.message);
    } else {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }
  }

  async updateFood(token, food) {
    // Check if meal ID is set
    if (this.state.foodMealID === null) {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }

    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let foodResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/' + this.state.foodMealID + '/foods/' + food.id, { 
      method: 'PUT',
      headers: requestHeader,
      body: JSON.stringify({
        name: food.name,
        calories: food.calories,
        protein: food.protein,
        carbohydrates: food.carbohydrates,
        fat: food.fat
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (foodResponse.message) {
      this.loadStats(this.state.date, foodResponse.message);
    } else {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }
  }

  async removeFood(token, foodID) {
    // Check if meal ID is set
    if (this.state.foodMealID === null) {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }

    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let foodResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/' + this.state.foodMealID + '/foods/' + foodID, { 
      method: 'DELETE',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (foodResponse.message) {
      this.loadStats(this.state.date, foodResponse.message);
    } else {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }
  }

  displayMeals() {
    if (this.state.listOfMeals.length === 0) {
      return <Text style={[styles.extraMarginTop, {fontWeight: 'bold'}]}>You don't have any meals logged for this day.</Text>
    } else {
      return (
        this.state.listOfMeals.map((meal) => {
          return (
            <TouchableHighlight key={meal.id}>
              <Card title={meal.name + " (" + meal.caloriesCount + " Kcal)"} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
                <>
                  <Ionicons name={"ios-more"} size={20} color={"#222"} onPress={() => this.setState({openedMeal: meal})} style={styles.cardOptions} />
                  
                  <Text style={{marginBottom: 10, fontWeight: 'bold'}}>Total Protein: {meal.proteinCount}g {'\u2022'} Total Carbs: {meal.carbsCount}g {'\u2022'} Total Fat: {meal.fatCount}g</Text>

                  {this.displayFoods(meal.foods, meal.id)}

                  <Button
                    buttonStyle={[styles.base, styles.cardButton]}
                    textStyle={styles.buttonText}
                    text={'+ Add Food'}
                    onPress={() => this.setState({showAddListedFood: true, foodMealID: meal.id})}
                  />
                </>
              </Card>
            </TouchableHighlight>
          )
      }))
    }
  }

  displayFoods(foods, mealID) {
    if (foods.length === 0) {
      return <Text style={{marginBottom: 5}}>No foods logged for this meal.</Text>
    } else {
      return (
        foods.map((food) => {
          return (
            <ListItem 
              key={food.id}
              title={food.name + " (" + food.calories + " Kcal)"}
              subtitle={this.renderMacronutirentsString(food.protein, food.carbohydrates, food.fat)}
              titleStyle={styles.listItemTitle}
              subtitleStyle={styles.listItemSubtitle}
              rightIcon={<Ionicons name={"ios-more"} size={20} color={"#222"} onPress={() => this.setState({openedFood: food, foodMealID: mealID})} />}
              containerStyle={styles.listItem}
              topDivider
            />
          )
        })
      )
    }
  }

  renderMacronutirentsString(protein, carbohydrates, fat) {
    return ("Protein: " + protein + "g \u2022 " + "Carbs: " + carbohydrates +  "g \u2022 " + "Fat: " + fat + "g")
  }

  updateDate(offset) {
    let currDate = moment(new Date(this.state.date));
    this.loadStats(currDate.add(offset, "days").format('LL'), null);
  }

  render() {
    return (
      <>
        <Header
          centerComponent={{ text: 'Meal Manager', style: styles.headerTitle }}
          rightComponent={{ text: 'Add', style: styles.headerButton, onPress: (() => this.setState({showAddMeal: true})) }}
          backgroundColor={'transparent'}
        />
        <SafeAreaView style={styles.pageViewContainer}>
          <ScrollView style={styles.pageView}>            
            <View style={styles.dayNavigationContainer}>
              <Ionicons name={"ios-arrow-dropleft-circle"} size={20} color={"#222"} onPress={() => this.updateDate(-1)} />
              <Text style={styles.dayNavigationTitle}>{this.state.date}</Text>
              <Ionicons name={"ios-arrow-dropright-circle"} size={20} color={"#222"} onPress={() => this.updateDate(1)} />
            </View>

            {!!this.state.message && (
              <Text style={[styles.message, styles.extraMarginTop]}>{this.state.message}</Text>
            )}

            {this.displayMeals()}

            <View style={styles.extraPaddingBottom}></View>
          </ScrollView>
        </SafeAreaView>

        <OverlayItem isVisible={this.state.showAddMeal} onBackdropPress={() => this.setState({showAddMeal: false})}>
          <AddMeal 
            addMeals={(meal) => this.addMeals(this.props.screenProps.userInfo.token, meal)} 
            hide={() => this.setState({showAddMeal: false})}
          />
        </OverlayItem>

        <OverlayItem isVisible={this.state.openedMeal !== null} onBackdropPress={() => this.setState({openedMeal: null})}>
          <EditMeal 
            meal={this.state.openedMeal} 
            updateMeals={(meal) => this.updateMeals(this.props.screenProps.userInfo.token, meal)} 
            removeMeals={(mealID) => this.removeMeals(this.props.screenProps.userInfo.token, mealID)}
            hide={() => this.setState({openedMeal: null})}
          />
        </OverlayItem>

        <OverlayItem isVisible={this.state.showAddListedFood} onBackdropPress={() => this.setState({showAddListedFood: false})}>
          <AddListedFood
            switchToCustom={() => this.setState({showAddCustomFood: true, showAddListedFood: false})}
            addFood={(food) => this.addFood(this.props.screenProps.userInfo.token, food)}
            hide={() => this.setState({showAddListedFood: false})}
          />
        </OverlayItem>

        <OverlayItem isVisible={this.state.showAddCustomFood} onBackdropPress={() => this.setState({showAddCustomFood: false})}>
          <AddCustomFood
            switchToList={() => this.setState({showAddListedFood: true, showAddCustomFood: false})}
            addFood= {(food) => this.addFood(this.props.screenProps.userInfo.token, food)}
            hide={() => this.setState({showAddCustomFood: false})}
          />
        </OverlayItem>

        <OverlayItem isVisible={this.state.openedFood !== null} onBackdropPress={() => this.setState({openedFood: null})}>
          <EditFood 
            food={this.state.openedFood}
            updateFood={(food) => this.updateFood(this.props.screenProps.userInfo.token, food)} 
            removeFood={(foodID) => this.removeFood(this.props.screenProps.userInfo.token, foodID)}
            hide={() => this.setState({openedFood: null})}
          />
        </OverlayItem>
      </>
    );
  }
}

export default MealManager;