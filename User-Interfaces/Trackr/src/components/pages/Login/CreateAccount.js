import React from 'react';
import { Text, ScrollView, TextInput } from 'react-native';

import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet'

class Modal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: null,
      usernameField: '',
      passwordField: '',
      passwordConfirmField: '',
    }
  }

  async createAccount() {
    // Check for password field
    if (this.state.passwordField !== this.state.passwordConfirmField) {
      this.setState({message: "Password fields do not match."});
      return;
    } else {
      this.setState({message: null});
    }

    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");

    // Create user by calling API
    let createUserResponse = await fetch('https://mysqlcs639.cs.wisc.edu/users/', { 
      method: 'POST',
      headers: requestHeader,
      body: JSON.stringify({
        username: this.state.usernameField,
        password: this.state.passwordConfirmField
      }),
      redirect: 'follow'
    }).then(response => response.json())
    .then(result => result)
    .catch(error => error);

    // Set message
    if (createUserResponse.message) {
      this.setState({message: createUserResponse.message});
    } else {
      this.setState({message: "An error occured and your account was not created. Please try again later."});
    }
  }

  render() {    
    return (
      <ScrollView>
        <Text style={{fontSize: 25}}>Create Account</Text>
        <Button buttonStyle={styles.modalCloseButton} textStyle={{fontSize: 25}} text={'✕'} onPress={() => this.props.hide()}/>

        <Text style={styles.marginVertical}>Fill out the information below to create an account. Please note the username and password fields are required.</Text>
        
        {!!this.state.message && (
          <Text style={styles.message}>{this.state.message}</Text>
        )}
        
        <Text>Username</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({usernameField: text})}
          value={this.state.usernameField} 
          textContentType={"username"} 
          autoCapitalize={"none"} 
          returnKeyType={"done"} 
          placeholder={"Username"} 
        />

        <Text>Password</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({passwordField: text})}
          value={this.state.passwordField} 
          textContentType={"newPassword"} 
          secureTextEntry={true} 
          returnKeyType={"done"} 
          placeholder={"Password"} 
        />
        
        <Text>Confirm Password</Text>
        <TextInput 
          style={[styles.base, styles.textInput]} 
          onChangeText={(text) => this.setState({passwordConfirmField: text})} 
          value={this.state.passwordConfirmField} 
          textContentType={"newPassword"} 
          secureTextEntry={true} 
          returnKeyType={"done"} 
          placeholder={"Confirm Password"} 
        />

        <Button 
          buttonStyle={[styles.base, styles.button]} 
          textStyle={styles.buttonText} 
          text={'Create Account'} 
          onPress={() => this.createAccount()}
        />
      </ScrollView>
    )
  }
}

export default Modal;