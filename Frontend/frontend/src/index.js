import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import {HashRouter} from 'react-router-dom';
import * as serviceWorker from './serviceWorker';
import { UserSignupPage } from './pages/UserSignupPage';
import {LoginPage} from './pages/LoginPage';
import App from './containers/App';
import {Provider} from 'react-redux';
import configureStore from './redux/configureStore';

//globális state
const store = configureStore();

ReactDOM.render(
  //Beköti a globális tárolót az appba
  <Provider store={store}>
    <HashRouter>
      <App/>
    </HashRouter>
  </Provider>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();