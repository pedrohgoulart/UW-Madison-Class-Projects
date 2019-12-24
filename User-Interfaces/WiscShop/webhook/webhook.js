const express = require('express')
const { WebhookClient } = require('dialogflow-fulfillment')
const app = express()
const fetch = require('node-fetch')
const base64 = require('base-64')

let username = null;
let token = null;

/* ==================== Helper Functions ==================== */

const itemNotFoundResponse = "Sorry, I am not sure what product you are referring to. Can you try rephrasing your request?";
const categoryNotFoundResponse = "Sorry, I am not sure what category you are referring to. Can you try rephrasing your request?";
const generalSearchError = "Sorry, for some reason I was not able to perform your seach. Could you please try again?";
const generalCartError = "Sorry, for some reason I was not able to access your cart. Can you try again?";

function getHeader() {
  return {"Content-Type": "application/json", "x-access-token": token}
}

function handleError(string, unrecognizedResponse) {
  console.log(unrecognizedResponse ? "Unrecognized response: " : "" + string);
}

function handleUserMessage(agent, welcomeMessage) {
  // Check if user is logged in
  if (token === null) {
    if (welcomeMessage) {
      agent.add("Hi! My name is Pietro, and I'm here to help. Please login to WiscShop to continue.");
    } else {
      agent.add("Ok, gotcha. Sorry, it seems that you are logged out. Please login to WiscShop to continue.");
    }
    return 1;
  }

  // Print user message
  printMessage(agent.query, true);
  return 0;
}

function handleAgentResponse(agent, string) {
  printMessage(string, false);
  agent.add(string);
}

/* ==================== Login Function ==================== */

async function getToken(password) {
  try {
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/login', { 
      method: 'GET',
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Basic "+ base64.encode(username + ":" + password)
      },
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.token) {
      token = serverResponse.token
    } else {
      handleError(serverResponse, true);
    }
  } catch(e) {
    handleError(e, false);
  }
}

/* ==================== Messages Functions ==================== */

async function clearMessages() {
  try {
    // Get header
    const header = getHeader();

    // Delete messages by calling API
    await fetch('https://mysqlcs639.cs.wisc.edu/application/messages', { 
      method: 'DELETE',
      headers: header,
      redirect: 'follow'
    });
  } catch(e) {
    handleError(e, false);
  }
}

async function printMessage(text, isUser) {
  try {
    // Get header
    const header = getHeader();

    // Post message by calling API
    await fetch('https://mysqlcs639.cs.wisc.edu/application/messages', { 
      method: 'POST',
      headers: header,
      body: JSON.stringify({
        text: text,
        isUser: isUser
      }),
      redirect: 'follow'
    });
  } catch(e) {
    handleError(e, false);
  }
}

/* ==================== Navigation Functions ==================== */

async function getCurrentPage() {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/application', { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.page) {
      return serverResponse.page;
    } else {
      handleError(serverResponse, true);
      return null;
    }
  } catch(e) {
    handleError(e, false);
    return null;
  }
}

async function redirectUser(websiteString) {
  try {
    // Get header
    const header = getHeader();

    // Get body
    const bodyString = (websiteString === null ? JSON.stringify({back: true}) : JSON.stringify({page: "/" + websiteString}));
    
    // Perform function by calling API
    await fetch('https://mysqlcs639.cs.wisc.edu/application', { 
      method: 'PUT',
      headers: header,
      body: bodyString,
      redirect: 'follow'
    })
  } catch(e) {
    handleError(e, false);
  }
}

/* ==================== Search Functions (Tags) ==================== */

async function addTags(tagsArray) {
  if (tagsArray.length == 0) {
    return "Sorry, I am not sure what tags you would like to include."
  }

  try {
    // Get header
    const header = getHeader();

    // Check current category
    category = await getCurrentCategory();

    if (category === undefined || category === null) {
      return "Sorry, it seems that you are not currently on a category page. Please let me know what category you would like to search for.";
    }

    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/categories/' + category + '/tags', { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.tags) {
      let filteredTags = tagsArray.filter((item) => serverResponse.tags.indexOf(item) > -1);

      // TODO
      console.log(tagsArray)
      console.log(serverResponse.tags)
      console.log(filteredTags)

      if (filteredTags.length === 0) {
        return "Sorry, there doesn't seem to be an option to add these tags to this category.";
      }

      // Perform function by calling API
      for (item of tagsArray) {
        await fetch('https://mysqlcs639.cs.wisc.edu/application/tags/' + item, { 
          method: 'POST',
          headers: header,
          redirect: 'follow'
        }).then(response => response.json());
      }

      return "Ok, I have added the tag(s) " + filteredTags.join(", ") + " to your search in this category.";
    } else {
      handleError(serverResponse, true);
      return generalSearchError;
    }
  } catch(e) {
    handleError(e, false);
    return generalSearchError;
  }
}

async function removeTags(tagsArray) {
  try {
    // Get header
    const header = getHeader();

    // Handle querry if array is empty
    if (tagsArray.length === 0) {
      await fetch('https://mysqlcs639.cs.wisc.edu/application/tags', { 
        method: 'DELETE',
        headers: header,
        redirect: 'follow'
      });

      return "Alright, I have reset your search.";
    }

    // Check current category
    category = await getCurrentCategory();

    if (category === undefined || category === null) {
      return "Sorry, it seems that you are not currently on a category page. Please let me know what category you would like to search for.";
    }

    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/categories/' + category + '/tags', { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.tags) {
      let filteredTags = tagsArray.filter((item) => serverResponse.tags.indexOf(item) > -1);

      if (filteredTags.length === 0) {
        return "Sorry, there doesn't seem to be an option to remove these tags from this category.";
      }

      // Perform function by calling API
      for (item of tagsArray) {
        await fetch('https://mysqlcs639.cs.wisc.edu/application/tags/' + item, { 
          method: 'DELETE',
          headers: header,
          redirect: 'follow'
        }).then(response => response.json());
      }

      return "Ok, I have removed the tag(s) " + filteredTags.join(", ") + " from your search in this category.";
    } else {
      handleError(serverResponse, true);
      return generalSearchError;
    }
  } catch(e) {
    handleError(e, false);
    return generalSearchError;
  }
}

/* ==================== Category Functions ==================== */

async function getCurrentCategory() {
  try {
    let currentPage = await getCurrentPage();

    if (currentPage === null) {
      return null;
    } else {
      return currentPage.split("/")[2];
    }
  } catch(e) {
    handleError(e, false);
    return null;
  }
}

async function getCategoryInfo() {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/categories', { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.categories) {
      return serverResponse.categories.join(", ");
    } else {
      handleError(serverResponse, true);
      return categoryNotFoundResponse;
    }
  } catch(e) {
    handleError(e, false);
    return categoryNotFoundResponse;
  }
}

async function getCategoryTags(categoryTitle) {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/categories/' + categoryTitle + "/tags", { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.tags) {
      let response = serverResponse.tags.join(", ");

      if (serverResponse.tags.length == 0) {
        return "This category does not have any tags.";
      } else {
        return "The tag(s) for this category are: " + response;
      }
    } else {
      handleError(serverResponse, true);
      return categoryNotFoundResponse;
    }
  } catch(e) {
    handleError(e, false);
    return categoryNotFoundResponse;
  }
}

/* ==================== Item Functions ==================== */

async function getCurrentItemID() {
  try {
    let currentPage = await getCurrentPage();

    if (currentPage === null) {
      return null;
    } else {
      return currentPage.split("/").pop();
    }
  } catch(e) {
    handleError(e, false);
    return null;
  }
}

async function getItemInfo(itemID) {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/products/' + itemID, { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.id) {
      return serverResponse;
    } else {
      handleError(serverResponse, true);
      return null;
    }
  } catch(e) {
    handleError(e, false);
    return null;
  }
}

async function getItemTags(itemID) {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/products/' + itemID + "/tags", { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.tags) {
      let response = serverResponse.tags.join(", ");

      if (serverResponse.tags.length == 0) {
        return "This item does not have any tags.";
      } else {
        return "The tag(s) for this item are: " + response;
      }   
    } else {
      handleError(serverResponse, true);
      return null;
    }
  } catch(e) {
    handleError(e, false);
    return null;
  }
}

async function getItemReviews(itemID) {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/products/' + itemID + "/reviews", { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.reviews) {
      let reviews = {count: 0, average: 0};

      await serverResponse.reviews.map((item) => {
        reviews.count++;
        reviews.average += item.stars;
      })
      
      reviews.average = (reviews.average / reviews.count).toFixed(1);

      return reviews;
    } else {
      handleError(serverResponse, true);
      return null;
    }
  } catch(e) {
    handleError(e, false);
    return null;
  }
}

/* ==================== Cart Functions ==================== */

function getCartItemDetails(productsList, productID) {
  let result = {price: 0, quantity: 0};

  if (productID != null) {
    let product = productsList.filter(item => item.id == productID);

    if (product.length !== 0) {
      result.price = (product[0].price * product[0].count);
      result.quantity = product[0].count;
    }
  } else {
    productsList.map((item) => {
      result.price += (item.price * item.count);
      result.quantity += item.count;
    })
  }

  return result;
}

async function addCartItem(itemID, itemCount) {
  try {
    // Check values
    if (itemID === "this" || itemID === null) {
      itemID = await getCurrentItemID();

      if (itemID === null || isNaN(itemID)) {
        return itemNotFoundResponse;
      }
    }

    if (itemCount === null) {
      itemCount = 1;
    }

    // Get header
    const header = getHeader();
    
    // Run API according to value specified by user
    for (let i = 0; i < itemCount; ++i) {
      await fetch('https://mysqlcs639.cs.wisc.edu/application/products/' + itemID, { 
        method: 'POST',
        headers: header,
        redirect: 'follow'
      })
    }

    // Perform function by calling API
    const itemQuantityInfo = await getCartInfo(itemID, true, false);

    return "Ok, I have added " + itemCount + " to your cart. " + itemQuantityInfo;
  } catch(e) {
    handleError(e, false);
    return itemNotFoundResponse;
  }
}

async function removeCartItem(itemID, itemCount) {
  try {
    // Check values
    if (itemID === "this" || itemID === null) {
      itemID = await getCurrentItemID();

      if (itemID === null || isNaN(itemID)) {
        return itemNotFoundResponse;
      }
    }

    // Get header
    const header = getHeader();

    // Check current number of this item in cart
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/application/products', { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    // Check result
    if (!serverResponse.products) {
      return generalCartError;
    }

    let info = getCartItemDetails(serverResponse.products, itemID);

    if (itemCount === null || itemCount > info.quantity) {
      itemCount = info.quantity;
    }

    // Run API according to value specified by user
    for (let i = 0; i < itemCount; ++i) {
      await fetch('https://mysqlcs639.cs.wisc.edu/application/products/' + itemID, { 
        method: 'DELETE',
        headers: header,
        redirect: 'follow'
      })
    }

    let remainingItems = info.quantity - itemCount;

    if (remainingItems === 0) {
      return "Ok, I have removed this item from your cart. Do you need anything else?";
    } else {
      return "Ok, I have removed " + itemCount + " from your cart. You still have " + remainingItems + " of this item in your cart.";
    }
  } catch(e) {
    handleError(e, false);
    return itemNotFoundResponse;
  }
}

async function clearCart(itemID) {
  if (itemID !== null) {
    return removeCartItem(itemID, null);
  }

  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    await fetch('https://mysqlcs639.cs.wisc.edu/application/products', { 
      method: 'DELETE',
      headers: header,
      redirect: 'follow'
    })

    return "Ok! I have cleared your cart. Let me know if you need anything else!";
  } catch(e) {
    handleError(e, false);
    return generalCartError;
  }
}

async function getCartInfo(itemID, quantityInfo, priceInfo) {
  try {
    // Get header
    const header = getHeader();
    
    // Perform function by calling API
    const serverResponse = await fetch('https://mysqlcs639.cs.wisc.edu/application/products', { 
      method: 'GET',
      headers: header,
      redirect: 'follow'
    }).then(response => response.json());

    if (serverResponse.products) {
      const info = getCartItemDetails(serverResponse.products, itemID);
      let responseString = "";

      if (info.quantity === 0) {
        if (itemID !== null) {
          return "You don't have this item in your cart yet.";
        } else {
          return "Seems like your cart is currently empty.";
        }
      }

      if (quantityInfo) {
        if (itemID !== null) {
          responseString += "You currently have " + info.quantity + " of this item in your cart. ";
        } else {
          responseString += "You currently have " + info.quantity + " items in your cart. ";
        }
      }

      if (priceInfo) {
        if (itemID !== null) {
          responseString += "The total for this item in your cart is $" + info.price + " dollars.";
        } else {
          responseString += "The total for your cart is $" + info.price + " dollars.";
        }
      }

      return responseString;
    } else {
      handleError(serverResponse, true);
      return generalCartError;
    }
  } catch(e) {
    handleError(e, false);
    return generalCartError;
  }
}

/* ==================== Intent Functions ==================== */

app.get('/', (req, res) => res.send('online'));

app.post('/', express.json(), (req, res) => {
  const agent = new WebhookClient({ request: req, response: res })

  function welcomeIntent() {
    if (handleUserMessage(agent, true)) {
      return;
    }
    
    handleAgentResponse(agent, "Hi! My name is Pietro, do you need any help with WiscShop?");
  }

  async function loginIntent() {
    // Get login parameters and log user in
    username = agent.parameters.username;
    await getToken(agent.parameters.password);

    if (token === null) {
      agent.add("Sorry, I was unable to log you in with username " + username + ". Your username or password might be incorrect.");
    } else {
      await redirectUser(username);
      await clearMessages();
      agent.add("Perfect, I have signed you into WiscShop. Let me know what else you would like to do.");
    }
  }

  async function navigationIntent() {
    if (handleUserMessage(agent, false)) {
      return;
    }

    // Get parameters
    const page = agent.parameters.pageNavigation;
    let responseString = "";

    // Check what user wants to do
    switch(page) {
      case "back":
        await redirectUser(null);
        responseString = "Ok, no problem. I have taken you to your previous page.";
        break;
      case "home":
        await redirectUser(username);
        responseString = "Ok, I have redirected you to the home page.";
        break;
      default:
        await redirectUser(username + "/" + page);
        responseString = "Ok, gotcha. I have redirected you to the " + page + " page.";
    }

    handleAgentResponse(agent, responseString);
  }

  async function searchProductIntent() {
    if (handleUserMessage(agent, false)) {
      return;
    }

    // Get parameters
    let action = agent.parameters.searchAction;
    let tags = [...agent.parameters.tag, ...agent.parameters.color];
    let responseString = "";

    // Check what user wants to do
    switch(action) {
      case "search":
        responseString =  await addTags(tags);
        break;
      case "reset":
        responseString = await removeTags(tags);
        break;
      default:
        responseString = "Sorry, I am not sure what you asked. Could you try rephrasing perhaps?";
    }

    handleAgentResponse(agent, responseString);
  }

  async function categoryInfoIntent() {
    if (handleUserMessage(agent, false)) {
      return;
    }

    // Get parameters
    const action = agent.parameters.categoryAction;
    const category = agent.parameters.category;
    let responseString = "";

    // Check what user wants to do
    switch(action) {
      case "categories":
        responseString = "The categories of items we have at WiscShop are: " + await getCategoryInfo();
        break;
      case "tags":
        if (category === "this" || category === null) {
          let currentCategory = await getCurrentCategory();

          if (currentCategory === null) {
            responseString = categoryNotFoundResponse;
          } else {
            responseString = await getCategoryTags(currentCategory);
          }
        } else {
          responseString = await getCategoryTags(category);
        }
        break;
      default:
        responseString = "Sorry, I am not sure what you asked. Could you try rephrasing that perhaps?";
    }

    handleAgentResponse(agent, responseString);
  }

  async function productInfoIntent() {
    if (handleUserMessage(agent, false)) {
      return;
    }

    // Get parameters
    const action = agent.parameters.productAction;
    let itemContext = agent.parameters.itemContext;
    let responseString = "";

    if (itemContext.length === 0) {
      itemContext = null;
    }

    // Check value
    if (itemContext === "this" || itemContext === null) {
      itemContext = await getCurrentItemID();

      if (itemContext === null || isNaN(itemContext)) {
        return itemNotFoundResponse;
      }
    }

    // Get product
    let product = await getItemInfo(itemContext);

    // Check what user wants to do
    switch(action) {
      case "name":
        responseString = "The name of this product is " + product.name;
        break;
      case "id":
        responseString = "Sure! Our ID for this item is " + product.id;
        break;
      case "category":
        responseString = "This item is classified under " + product.category;
        break;
      case "tags":
        responseString = await getItemTags(product.id);
        break;
      case "price":
        responseString = "This product costs " + product.price;
        break;
      case "description":
        responseString = "The description for this product is: " + product.description;
        break;
      case "reviews":
        const reviews = await getItemReviews(product.id);
        if (reviews === null) {
          responseString = "Sorry, for some reason I can't access the reviews for this item. Could you please try again?"
        } else {
          responseString = "Ok, this item has " + reviews.count + " review(s) and the average rating is " + reviews.average + " starts.";
        }
        break;
      case "go":
        await redirectUser(username + "/" + product.category + "/products/" + product.id);
        responseString = "Alright! Here is the product you are looking for.";
        break;
      default:
        await redirectUser(username + "/" + product.category + "/products/" + product.id);
        responseString = "Hmm, I'm not sure what you meant by that. Here is the product page though.";
    }

    handleAgentResponse(agent, responseString);
  }

  async function cartIntent() {
    if (handleUserMessage(agent, false)) {
      return;
    }

    // Get parameters
    const action = agent.parameters.cartAction;
    let itemContext = agent.parameters.itemContext;
    let value = agent.parameters.numberValue;
    let responseString = "";

    if (itemContext.length === 0) {
      itemContext = null;
    }

    if (value.length === 0) {
      value = null;
    }

    // Check what user wants to do
    switch(action) {
      case "add":
        responseString = await addCartItem(itemContext, value);
        break;
      case "remove":
        responseString = await removeCartItem(itemContext, value);
        break;
      case "clear":
        responseString = await clearCart(itemContext);
        break;
      case "quantity":
        responseString = "Ok, gotcha! " + await getCartInfo(itemContext, true, false);
        break;
      case "cost":
        responseString = "Ok, gotcha! " + await getCartInfo(itemContext, false, true);
        break;
      case "information":
        responseString = "Alright! " + await getCartInfo(itemContext, true, true);
        break;
      default:
        responseString = "Sorry, I am not sure what you would like to do the cart, but here is some information about it: " + await getCartInfo(itemContext, true, true);
    }

    handleAgentResponse(agent, responseString);
  }

  async function checkoutIntent() {
    if (handleUserMessage(agent, false)) {
      return;
    }

    // Get parameter and check what user wants to do.
    const action = agent.parameters.checkoutAction;
    let responseString = "";

    // Check what user wants to do
    switch(action) {
      case "confirm":
        // Check if user is in checkout page, if not, take them there
        let currentPage = await getCurrentPage();

        if (currentPage !== "/" + username + "/cart-review") {
          await redirectUser(username + "/cart-review");
          responseString = "Ok, here is the confimration page. Let me know if you would like to confirm your order.";
        } else {
          await redirectUser(username + "/cart-confirmed");
          responseString = "Ok, perfect. I have confirmed your order. Thank you for shopping with us!";
        }
        break;
      case "edit":
        await redirectUser(username + "/cart");
        responseString = "Ok, I have redirected you to your cart. Let me know what modifications your would like to make.";
        break;
      case "cancel":
        await redirectUser(username + "/cart");
        responseString = "Ok, gotcha. I will not proceed with your order. Here is your cart if you would like to make any modifications to it.";
        break;
      default:
        responseString = "Sorry, I didn't get that. Could you perhaps rephrase that?";
    }

    handleAgentResponse(agent, responseString);
  }

  let intentMap = new Map();
  intentMap.set('Default Welcome Intent', welcomeIntent);
  intentMap.set('Login', loginIntent);
  intentMap.set('Navigation', navigationIntent);
  intentMap.set('SearchProduct', searchProductIntent);
  intentMap.set('CategoryInfo', categoryInfoIntent);
  intentMap.set('ProductInfo', productInfoIntent);
  intentMap.set('Cart', cartIntent);
  intentMap.set('Checkout', checkoutIntent);

  agent.handleRequest(intentMap);
})

app.listen(process.env.PORT || 8080)