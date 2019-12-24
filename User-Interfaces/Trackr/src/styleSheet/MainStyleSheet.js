import { StyleSheet } from 'react-native';

export default StyleSheet.create({
  // Views
  pageViewContainer: {
    flex: 1,
  },
  pageView: {
    padding: 12,
  },
  loginView: {
    alignItems: 'center',
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#5D98A6'
  },
  // Base
  base: {
    borderRadius: 8,
    color: '#222', 
    fontSize: 16, 
    padding: 10, 
  },
  // Header
  headerTitle: {
    color: '#5D98A6', 
    fontSize: 22,
    fontWeight: 'bold'
  },
  headerButton: {
    color: '#5D98A6', 
    fontSize: 16, 
    fontWeight: 'bold'
  },
  loginHeading: {
    color: '#fff',
    fontSize: 24,
    marginBottom: 30
  },
  // Day Navigation
  dayNavigationContainer: {
    flexDirection: 'row',
    borderRadius: 8,
    borderWidth: 0,
    backgroundColor: '#eee',
    flex: 1,
    padding: 10,
    justifyContent: 'center',
  },
  dayNavigationTitle: {
    color: '#222', 
    flexGrow: 1,
    fontSize: 16,
    fontWeight: 'bold',   
    textAlign: 'center'
  },
  // Text
  title: {
    color: '#fff',
    fontSize: 24,
    paddingVertical: 12
  },
  message: {
    color: "#BF5A36", 
    marginBottom: 20,
  },
  loginMessage: {
    color: "#FF8B47", 
    fontWeight: 'bold',
    marginBottom: 20,
  },
  // Text Input
  textInput: {
    backgroundColor: '#eee', 
    marginVertical: 5,
    textAlign: 'left'
  },
  dateInput: {
    marginVertical: 5, 
    width: '100%'
  },
  // Button
  button: {
    backgroundColor: '#eee', 
    alignItems: 'center', 
    justifyContent: 'center', 
    marginTop: 20, 
    padding: 10,
    minWidth: 120,
  },
  buttonText: {
    color: '#222', 
    fontSize: 16, 
    fontWeight: 'bold'
  },
  modalCloseButton: {
    alignItems: 'center', 
    justifyContent: 'center', 
    position: 'absolute', 
    right: 0,
  },
  loginLinks: {
    color: '#fff',
    textDecorationLine: 'underline',
  },
  // Card
  card: {
    marginHorizontal: 0,
    borderRadius: 8,
    borderWidth: 0,
    backgroundColor: '#eee',
    padding: 10
  },
  cardTitle: {
    textAlign: 'left',
    marginBottom: 10
  },
  cardOptions: {
    position: 'absolute', 
    right: 0
  },
  cardDivider: {
    backgroundColor: 'transparent',
    marginBottom: 0
  },
  cardButton: {
    backgroundColor: 'transparent', 
    alignItems: 'center', 
    justifyContent: 'center',
    padding: 0,
    marginTop: 10,
    marginBottom: 5
  },
  listItem: {
    backgroundColor: '#eee',
    padding: 0,
  },
  listItemTitle: {
    fontSize: 16,
    paddingTop: 8,
    paddingBottom: 4
  },
  listItemSubtitle: {
    fontSize: 14,
    paddingBottom: 8,
  },
  // Margins
  marginVertical: {
    marginVertical: 20,
  },
  extraMarginTop: {
    marginTop: 20
  },
  extraPaddingBottom: {
    marginBottom: 25,
  }
})