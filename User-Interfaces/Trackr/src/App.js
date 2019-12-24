import React from 'react';
import { createAppContainer } from 'react-navigation';
import { createBottomTabNavigator } from 'react-navigation-tabs';
import Ionicons from 'react-native-vector-icons/Ionicons';
import moment from 'moment';

import Login from "./components/pages/Login/Login";
import AccountScreen from './components/pages/Account/Account';
import ActivityManagerScreen from './components/pages/ActivityManager/ActivityManager';
import MealManagerScreen from './components/pages/MealManager/MealManager';
import TodayScreen from './components/pages/Today/Today';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      userInfo: null,
    }
    this.loadAccountInfo = this.loadAccountInfo.bind(this);
    this.updateAccountInfo = this.updateAccountInfo.bind(this);
    this.deleteAccount = this.deleteAccount.bind(this);
    this.loadActivities = this.loadActivities.bind(this);
    this.loadMeals = this.loadMeals.bind(this);
  }

  async loadAccountInfo(token, username) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Get user info by calling API
    let accountInfoResponse = await fetch('https://mysqlcs639.cs.wisc.edu/users/' + username, { 
      method: 'GET',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (accountInfoResponse.message) {
      return accountInfoResponse.message;
    } else {
      this.setState({
        userInfo: {
          token: JSON.parse(JSON.stringify(token)),
          username: accountInfoResponse.username, 
          firstName: accountInfoResponse.firstName, 
          lastName: accountInfoResponse.lastName, 
          goalDailyCalories: accountInfoResponse.goalDailyCalories,
          goalDailyProtein: accountInfoResponse.goalDailyProtein,
          goalDailyCarbohydrates: accountInfoResponse.goalDailyCarbohydrates,
          goalDailyFat: accountInfoResponse.goalDailyFat,
          goalDailyActivity: accountInfoResponse.goalDailyActivity,
        },
      });
      return null;
    }
  }

  async updateAccountInfo(userInfo) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", userInfo.token)

    // Update user info by calling API
    let accountInfoResponse = await fetch('https://mysqlcs639.cs.wisc.edu/users/' + userInfo.username, { 
      method: 'PUT',
      headers: requestHeader,
      body: JSON.stringify({
        firstName: userInfo.firstName,
        lastName: userInfo.lastName,
        goalDailyCalories: userInfo.goalDailyCalories,
        goalDailyProtein: userInfo.goalDailyProtein,
        goalDailyCarbohydrates: userInfo.goalDailyCarbohydrates,
        goalDailyFat: userInfo.goalDailyFat,
        goalDailyActivity: userInfo.goalDailyActivity,
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (accountInfoResponse.message) {
      return accountInfoResponse.message;
    } else {
      return "An error occurred and your information was not saved. Please try again later.";
    }
  }

  async deleteAccount(userInfo) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("x-access-token", userInfo.token)

    // Update user info by calling API
    let accountInfoResponse = await fetch('https://mysqlcs639.cs.wisc.edu/users/' + userInfo.username, { 
      method: 'DELETE',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (accountInfoResponse.message) {
      return accountInfoResponse.message;
    } else {
      return "An error occurred and your account could not be deleted. Please try again later.";
    }
  }

  async loadActivities(date) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", this.state.userInfo.token)

    // Update user info by calling API
    let activityResponse = await fetch('https://mysqlcs639.cs.wisc.edu/activities/', { 
      method: 'GET',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (activityResponse.activities) {
      return activityResponse.activities.filter((activity) => moment(new Date(activity.date)).format('LL') == date);
    } else {
      return "An error occurred and we could not retrieve your activities information. Please try again later.";
    }
  }

  async loadMeals(date) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", this.state.userInfo.token)

    // Update user info by calling API
    let mealResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/', { 
      method: 'GET',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json());

    if (mealResponse.meals) {
      const filteredMealsList = mealResponse.meals.filter((meal) => moment(new Date(meal.date)).format('LL') == date);
      let mealsList = [];

      for (item of filteredMealsList) {
        let meal = JSON.parse(JSON.stringify(item));
        meal.foods = await this.loadFood(meal.id);
        mealsList.push(meal);
      }

      return mealsList;
    } else {
      return "An error occurred and we could not retrieve your meals information. Please try again later.";
    }
  }

  async loadFood(mealID) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", this.state.userInfo.token)

    // Update user info by calling API
    let foodResponse = await fetch('https://mysqlcs639.cs.wisc.edu/meals/' + mealID + '/foods/', { 
      method: 'GET',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (foodResponse.foods) {
      return foodResponse.foods;
    } else {
      return "An error occurred and we could not retrieve your food information. Please try again later.";
    }
  }

  render() {
    const getTabBarIcon = (navigation, tintColor) => {
      const { routeName } = navigation.state;
      let iconName;
      if (routeName === 'Today') {
        iconName = "ios-today";
      } else if (routeName === 'Activity Manager') {
        iconName = "ios-bicycle";
      } else if (routeName === 'Meal Manager') {
        iconName = "ios-cafe";
      } else if (routeName === 'Account') {
        iconName = "ios-person";
      }
    
      return <Ionicons name={iconName} size={25} color={tintColor} />;
    };

    const AppContainer = createAppContainer(
      createBottomTabNavigator(
        {
          'Today': {screen: TodayScreen},
          'Activity Manager': {screen: ActivityManagerScreen},
          'Meal Manager': {screen: MealManagerScreen},
          'Account': {screen: AccountScreen},
        },
        {
          defaultNavigationOptions: ({ navigation }) => ({
            tabBarIcon: ({tintColor }) =>
              getTabBarIcon(navigation, tintColor),
          }),
          tabBarOptions: {
            activeTintColor: '#5D98A6',
            inactiveTintColor: 'grey',
          },
        }
      )
    );
    
    if (this.state.userInfo == null) {
      return <Login loadAccountInfo={(token, username) => this.loadAccountInfo(token, username)} />
    } else {
      return (
        <AppContainer 
          screenProps={{ 
            userInfo: this.state.userInfo,
            logout: (() => this.setState({userInfo: null})),
            updateAccountInfo: ((info) => this.updateAccountInfo(info)),
            deleteAccount: ((info) => this.deleteAccount(info)),
            loadActivities: ((date) => this.loadActivities(date)),
            loadMeals: ((date) => this.loadMeals(date))
          }}
        />
      )
    }
  }
}

export default App;