import React from 'react';
import { SafeAreaView, ScrollView, View, Text, TouchableHighlight } from 'react-native';
import { Header, Card } from 'react-native-elements';
import Ionicons from 'react-native-vector-icons/Ionicons';
import moment from "moment";

import AddActivity from './AddActivity';
import EditActivity from './EditActivity';

import OverlayItem from '../../reusable/OverlayItem';
import styles from '../../../styleSheet/MainStyleSheet';

class ActivityManager extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: null,
      date: null,
      listOfActivities: [],
      openedActivity: null,
      showAddActivity: false,
    }
  }

  componentDidMount() {
    this.loadStats(moment().format('LL'), null);
  }

  async loadStats(currDate, message) {
    // Get activities list
    let activities = await this.props.screenProps.loadActivities(currDate);
    if (!Array.isArray(activities)) {
      this.setState({
        message: String(activities),
        openedActivity: null,
        showAddActivity: false
      })
      return;
    }

    this.setState({
      message: message,
      date: currDate,
      listOfActivities: activities,
      openedActivity: null,
      showAddActivity: false
    });
  }

  defaultErrorMessage() {
    return "An error occurred and we could not perform the action. Please try again later."
  }

  async addActivities(token, activity) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let activityResponse = await fetch('https://mysqlcs639.cs.wisc.edu/activities/', { 
      method: 'POST',
      headers: requestHeader,
      body: JSON.stringify({
        name: activity.name,
        duration: activity.duration,
        date: activity.date,
        calories: activity.calories
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (activityResponse.message) {
      this.loadStats(moment(new Date(activity.date)).format('LL'), activityResponse.message);
    } else {
      this.loadStats(moment(new Date(activity.date)).format('LL'), this.defaultErrorMessage());
    }
  }

  async updateActivities(token, activity) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let activityResponse = await fetch('https://mysqlcs639.cs.wisc.edu/activities/' + activity.id, { 
      method: 'PUT',
      headers: requestHeader,
      body: JSON.stringify({
        name: activity.name,
        duration: activity.duration,
        date: activity.date,
        calories: activity.calories
      }),
      redirect: 'follow'
    }).then(response => response.json())

    if (activityResponse.message) {
      this.loadStats(moment(new Date(activity.date)).format('LL'), activityResponse.message);
    } else {
      this.loadStats(moment(new Date(activity.date)).format('LL'), this.defaultErrorMessage());
    }
  }

  async removeActivities(token, activityID) {
    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("x-access-token", token)

    // Update user info by calling API
    let activityResponse = await fetch('https://mysqlcs639.cs.wisc.edu/activities/' + activityID, { 
      method: 'DELETE',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    if (activityResponse.message) {
      this.loadStats(this.state.date, activityResponse.message);
    } else {
      this.loadStats(this.state.date, this.defaultErrorMessage());
    }
  }

  displayActivities() {
    if (this.state.listOfActivities.length === 0) {
      return <Text style={[styles.extraMarginTop, {fontWeight: 'bold'}]}>You don't have any activities logged for this day.</Text>
    } else {
      return (
        this.state.listOfActivities.map((activity) => {
          return (
            <TouchableHighlight key={activity.id}>
              <Card title={activity.name + " (" + activity.calories + " Kcal)"} titleStyle={styles.cardTitle} containerStyle={styles.card} dividerStyle={styles.cardDivider}>
                <Ionicons name={"ios-more"} size={20} color={"#222"} onPress={() => this.setState({openedActivity: activity})} style={styles.cardOptions} />
                <Text>Start Time: {moment(new Date(activity.date)).format('LT')} {'\u2022'} Length: {activity.duration}min</Text>
              </Card>
            </TouchableHighlight>
          )
      }))
    }
  }

  updateDate(offset) {
    let currDate = moment(new Date(this.state.date));
    this.loadStats(currDate.add(offset, "days").format('LL'), null);
  }

  render() {
    return (
      <>
        <Header
          centerComponent={{ text: 'Activity Manager', style: styles.headerTitle }}
          rightComponent={{ text: 'Add', style: styles.headerButton, onPress: (() => this.setState({showAddActivity: true})) }}
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

            {this.displayActivities()}

            <View style={styles.extraPaddingBottom}></View>
          </ScrollView>
        </SafeAreaView>

        <OverlayItem isVisible={this.state.showAddActivity} onBackdropPress={() => this.setState({showAddActivity: false})}>
          <AddActivity 
            addActivities={(activity) => this.addActivities(this.props.screenProps.userInfo.token, activity)} 
            hide={() => this.setState({showAddActivity: false})}
          />
        </OverlayItem>

        <OverlayItem isVisible={this.state.openedActivity !== null} onBackdropPress={() => this.setState({openedActivity: null})}>
          <EditActivity 
            openedActivity={this.state.openedActivity} 
            updateActivities={(activity) => this.updateActivities(this.props.screenProps.userInfo.token, activity)} 
            removeActivities={(activityID) => this.removeActivities(this.props.screenProps.userInfo.token, activityID)} 
            hide={() => this.setState({openedActivity: null})}
          />
        </OverlayItem>
      </>
    );
  }
}

export default ActivityManager;