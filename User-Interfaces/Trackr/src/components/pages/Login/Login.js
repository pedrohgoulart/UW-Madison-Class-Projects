import React from 'react';
import { SafeAreaView, Text, TextInput, Dimensions } from 'react-native';
import { Header } from 'react-native-elements';
import base64 from 'base-64';

import CreateAccount from './CreateAccount';

import OverlayItem from '../../reusable/OverlayItem';
import Button from '../../reusable/Button';
import styles from '../../../styleSheet/MainStyleSheet'

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      message: null,
      usernameField: '',
      passwordField: '',
      showCreateAccountModal: false
    }
  }

  async login() {
    this.setState({message: null});

    // Check for common errors
    if (this.state.usernameField.trim() == "") {
      this.setState({message: "Please type your username."});
      return;
    } else if (this.state.passwordField.trim() == "") {
      this.setState({message: "Please type your password."});
      return;
    }

    // Set header
    const requestHeader = new Headers();
    requestHeader.append("Accept", "application/json");
    requestHeader.append("Content-Type", "application/json");
    requestHeader.append("Authorization", "Basic " + base64.encode(this.state.usernameField + ":" + this.state.passwordField))

    // Login user by calling API
    let loginResponse = await fetch('https://mysqlcs639.cs.wisc.edu/login/', { 
      method: 'GET',
      headers: requestHeader,
      redirect: 'follow'
    }).then(response => response.json())

    // Set error message if any login errors are found
    if (loginResponse.token) {
      let message = await this.props.loadAccountInfo(loginResponse.token, this.state.usernameField);
      if (message != null) {
        this.setState({ message: message });
      }
    } else {
      this.setState({ message: loginResponse.message });
    }
  }

  render() {
    const textInputWidth = Dimensions.get('window').width - 24;

    return (
      <>
        <Header
          centerComponent={{ text: 'Trackr Login', style: styles.headerTitle }}
          backgroundColor={'transparent'}
        />
        
        <SafeAreaView style={styles.loginView}>
          <Text style={styles.loginHeading}>Welcome Back!</Text>
      
          {!!this.state.message && (
            <Text style={[styles.loginMessage]}>{this.state.message}</Text>
          )}
          
          <TextInput 
            style={[styles.base, styles.textInput]} 
            width={textInputWidth} 
            onChangeText={(text) => this.setState({usernameField: text})} 
            value={this.state.usernameField} 
            textContentType={"username"} 
            returnKeyType={"done"} 
            autoCapitalize={"none"} 
            placeholder={"Username"} />

          <TextInput 
            style={[styles.base, styles.textInput]} 
            width={textInputWidth} 
            onChangeText={(text) => this.setState({passwordField: text})}
            value={this.state.passwordField} 
            textContentType={"password"} 
            returnKeyType={"done"} 
            secureTextEntry={true} 
            placeholder={"Password"} />
            
          <Button buttonStyle={[styles.base, styles.button]} textStyle={styles.buttonText} text={'Login'} onPress={() => this.login()}/>
          
          <Text style={[styles.base, styles.loginLinks, styles.extraMarginTop]} onPress={() => this.setState({showCreateAccountModal: true})}>Create an account</Text>
        </SafeAreaView>

        <OverlayItem isVisible={this.state.showCreateAccountModal} onBackdropPress={() => this.setState({showCreateAccountModal: false})}>
          <CreateAccount 
            hide={() => this.setState({showCreateAccountModal: false})}
          />
        </OverlayItem>
      </>
    );
  }
}

export default Login;