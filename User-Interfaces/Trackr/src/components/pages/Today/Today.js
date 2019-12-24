import React from 'react';
import { SafeAreaView, ScrollView, View, Text } from 'react-native';
import { Header, Card, ListItem } from 'react-native-elements';
import Ionicons from 'react-native-vector-icons/Ionicons';
import { StackedBarChart } from 'react-native-svg-charts'
import moment from 'moment';

import styles from '../../../styleSheet/MainStyleSheet';

class Today extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: null,
      date: null,
      listOfActivities: [],
      listOfMeals: [],
      totalCalories: 0,
      totalActivity: 0,
      totalProtein: 0,
      totalCarbohydrates: 0,
      totalFat: 0
    }
  }

  async componentDidMount() {
    // Update when on focus
    this.focusListener = this.props.navigation.addListener('didFocus', async () => {
      this.loadStats(moment().format('LL'), null);
    });
  }

  componentWillUnmount() {
    // Remove the event listener
    this.focusListener.remove();
  }

  async loadStats(currDate, message) {
    // Get activities list
    let activities = await this.props.screenProps.loadActivities(currDate);
    if (!Array.isArray(activities)) {
      this.setState({message: String(activities)})
      return;
    }

    // Update activity calories count
    let activityCaloriesCount = activities.reduce((count, activity) => {
      return count + activity.calories;
    }, 0);

    // Get meals list
    let meals = await this.props.screenProps.loadMeals(currDate);
    if (!Array.isArray(meals)) {
      this.setState({message: String(meals)})
      return;
    }

    // Update calories, protein, carbs, and fat
    let caloriesCount = 0;
    let proteinCount = 0;
    let carbsCount = 0;
    let fatCount = 0;

    meals.map((meal) => {
      // Ensures array is not an error string
      if (Array.isArray(meal.foods)) {
        meal.foods.map((food) => {
          caloriesCount += food.calories;
          proteinCount += food.protein;
          carbsCount += food.carbohydrates;
          fatCount += food.fat;
        })
      }
    })

    // Update states
    this.setState({
      message: message,
      date: currDate,
      listOfActivities: activities, 
      totalActivity: activityCaloriesCount,
      listOfMeals: meals,
      totalCalories: caloriesCount,
      totalProtein: proteinCount,
      totalCarbohydrates: carbsCount,
      totalFat: fatCount
    });
  }

  displayNutrition() {
    const protein = "Protein (" + this.state.totalProtein + "g / " + this.props.screenProps.userInfo.goalDailyProtein + "g)";
    const carbs = "Carbs (" + this.state.totalCarbohydrates + "g / " + this.props.screenProps.userInfo.goalDailyCarbohydrates + "g)";
    const fat = "Fat (" + this.state.totalFat + "g / " + this.props.screenProps.userInfo.goalDailyFat + "g)";

    return (
      <Card title={"Nutrition"} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
        <Text>Calories Eaten: {this.state.totalCalories} kcal / {this.props.screenProps.userInfo.goalDailyCalories} kcal</Text>
        <Text>Calories Burned: {this.state.totalActivity} kcal / {this.props.screenProps.userInfo.goalDailyActivity} kcal</Text>

        <Text style={{marginTop: 10}}>{protein} {'\u2022'} {carbs} {'\u2022'} {fat}</Text>
        
        {this.renderBarChart(this.state.totalProtein, this.state.totalCarbohydrates, this.state.totalFat)}
      </Card>
    )
  }

  renderBarChart(protein, carbs, fat) {
    if (protein + carbs + fat === 0) {
      return;
    }

    return (
      <StackedBarChart 
        style={{ height: 50 }} 
        keys={['protein', 'carbs', 'fat']} 
        colors={['#406973', '#4D7E8A', '#5B95A3']} 
        data={[{
          protein: protein, 
          carbs: carbs, 
          fat: fat
        }]} 
        horizontal={true}
      />
    )
  }

  displayActivities() {
    if (this.state.listOfActivities.length === 0) {
      return (
        <Card title={"No activities logged"} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
          <Text>You can log activities on activity manager.</Text>
        </Card>
      )
    } else {
      return (
        <Card title={"Activities"} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
          {this.state.listOfActivities.map((activity) => {
            return (
              <ListItem 
                key={activity.id}
                title={activity.name + " (" + activity.calories + ' Kcal)'}
                subtitle={this.renderActivityDetailsString(activity.date, activity.duration)}
                titleStyle={styles.listItemTitle}
                subtitleStyle={styles.listItemSubtitle}
                containerStyle={styles.listItem}
                topDivider
              />
            )
          })}
        </Card>
      )
    }
  }

  displayMeals() {
    if (this.state.listOfMeals.length === 0) {
      return (
        <Card title={"No meals logged"} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
          <Text>You can log activities on meal manager.</Text>
        </Card>
      )
    } else {
      return (
        this.state.listOfMeals.map((meal) => {
          return (
            <Card key={meal.id} title={meal.name} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
              {this.displayFoods(meal.foods)}
            </Card>
          )
        })
      )
    }
  }

  displayFoods(foods) {
    if (!Array.isArray(foods)) {
      return <Text style={styles.message}>{foods}</Text>
    }

    if (foods.length === 0) {
      return <Text>No foods logged for this meal.</Text>
    } else {
      return (
        foods.map((food) => {
          return (
            <ListItem 
              key={food.id}
              title={food.name + " (" + food.calories + ' Kcal)'}
              subtitle={this.renderMacronutirentsString(food.protein, food.carbohydrates, food.fat)}
              titleStyle={styles.listItemTitle}
              subtitleStyle={styles.listItemSubtitle}
              containerStyle={styles.listItem}
              topDivider
            />
          )
        })
      )
    }
  }

  renderActivityDetailsString(date, duration) {
    return ("Start Time: " + moment(new Date(date)).format('LT') + " \u2022 Length: " + duration + "min")
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
          centerComponent={{ text: 'Today', style: styles.headerTitle }}
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
              <Text style={styles.message}>{this.state.message}</Text>
            )}

            {this.displayNutrition()}

            {this.displayActivities()}

            {this.displayMeals()}
            
            <View style={styles.extraPaddingBottom}></View>
          </ScrollView>
        </SafeAreaView>
      </>
    );
  }
}

export default Today;