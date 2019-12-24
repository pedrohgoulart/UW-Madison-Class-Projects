import React from 'react';
import { View, SafeAreaView, ScrollView, Text, TextInput, Alert } from 'react-native';
import { Header } from 'react-native-elements';

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet';

class Account extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      accountInfoMessage: null,
      firstName: this.props.screenProps.userInfo.firstName,
      lastName: this.props.screenProps.userInfo.lastName,
      goalDailyCalories: this.props.screenProps.userInfo.goalDailyCalories,
      goalDailyProtein: this.props.screenProps.userInfo.goalDailyProtein,
      goalDailyCarbohydrates: this.props.screenProps.userInfo.goalDailyCarbohydrates,
      goalDailyFat: this.props.screenProps.userInfo.goalDailyFat,
      goalDailyActivity: this.props.screenProps.userInfo.goalDailyActivity,
    }
  }

  async updateAccount(action) {
    this.setState({accountInfoMessage: null});

    let userInfo = {
      token: this.props.screenProps.userInfo.token,
      username: this.props.screenProps.userInfo.username,
      firstName: this.state.firstName,
      lastName: this.state.lastName,
      goalDailyCalories: this.state.goalDailyCalories,
      goalDailyProtein: this.state.goalDailyProtein,
      goalDailyCarbohydrates: this.state.goalDailyCarbohydrates,
      goalDailyFat: this.state.goalDailyFat,
      goalDailyActivity: this.state.goalDailyActivity,
    }

    if (action === 'delete') {
      this.setState({accountInfoMessage: await this.props.screenProps.deleteAccount(userInfo)});
      if (this.state.accountInfoMessage === 'User has been deleted!') {
        this.props.screenProps.logout();
      }
    } else {
      this.setState({accountInfoMessage: await this.props.screenProps.updateAccountInfo(userInfo)});
    }
  }

  deleteAccountConfirmation() {
    Alert.alert(
      'Delete Account',
      'Are you sure you want to delete your account? This action cannot be undone.',
      [
        {text: 'Cancel', style: 'cancel'},
        {text: 'Delete', onPress: () => this.updateAccount('delete')},
      ],
      {cancelable: true},
    );
  }

  render() {
    return (
      <>
        <Header
          centerComponent={{ text: 'Account', style: styles.headerTitle }}
          rightComponent={{ text: 'Logout', style: styles.headerButton, onPress: (() => this.props.screenProps.logout()) }}
          backgroundColor={'transparent'}
        />
        <SafeAreaView style={styles.pageViewContainer}>
          <ScrollView style={styles.pageView}>
            <Text>First Name</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({firstName: text})} 
              value={this.state.firstName} 
              textContentType={"givenName"} 
              returnKeyType={"done"} 
              placeholder={"First Name"}
            />

            <Text>Last Name</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({lastName: text})} 
              value={this.state.lastName} 
              textContentType={"familyName"} 
              returnKeyType={"done"} 
              placeholder={"Last Name"} 
            />

            <Text>Daily Calories Goal</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({goalDailyCalories: text})}
              value={String(this.state.goalDailyCalories)} 
              keyboardType={"decimal-pad"} 
              returnKeyType={"done"} 
              placeholder={"Daily Calories Goal"} 
            />

            <Text>Daily Protein Goal (Grams)</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({goalDailyProtein: text})} 
              value={String(this.state.goalDailyProtein)} 
              keyboardType={"decimal-pad"} 
              returnKeyType={"done"} 
              placeholder={"Daily Protein Goal"} 
            />

            <Text>Daily Carbohydrates Goal (Grams)</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({goalDailyCarbohydrates: text})} 
              value={String(this.state.goalDailyCarbohydrates)} 
              keyboardType={"decimal-pad"} 
              returnKeyType={"done"} 
              placeholder={"Daily Carbohydrates Goal"} 
            />

            <Text>Daily Fat Goal (Grams)</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({goalDailyFat: text})} 
              value={String(this.state.goalDailyFat)} 
              keyboardType={"decimal-pad"} 
              returnKeyType={"done"} 
              placeholder={"Daily Fat Goal"} 
            />

            <Text>Daily Activity Goal (Calories)</Text>
            <TextInput 
              style={[styles.base, styles.textInput]} 
              onChangeText={(text) => this.setState({goalDailyActivity: text})} 
              value={String(this.state.goalDailyActivity)}
              keyboardType={"decimal-pad"} 
              returnKeyType={"done"} 
              placeholder={"Daily Activity Goal"} 
            />
            
            {!!this.state.accountInfoMessage && (
              <Text style={[styles.message, styles.extraMarginTop, {textAlign: 'center'}]}>{this.state.accountInfoMessage}</Text>
            )}

            <Button 
              buttonStyle={[styles.base, styles.button]} 
              textStyle={styles.buttonText}
              text={'Save Changes'}
              onPress={() => this.updateAccount('update')}
            />

            <Button 
              buttonStyle={[styles.base, styles.button]} 
              textStyle={styles.buttonText} 
              text={'Delete Account'}
              onPress={() => this.deleteAccountConfirmation()} 
            />
            
            <View style={styles.extraPaddingBottom}></View>
          </ScrollView>
        </SafeAreaView>
      </>
    );
  }
}

export default Account;