import * as redux from 'redux';
import thunk from 'redux-thunk';
import { reducer as formReducer } from 'redux-form';
import { authenticationReducer } from '../access/authentication/authenticationReducers'

export var configure = (initialState = { }) => {
    var reducer = redux.combineReducers({
      form: formReducer,
      auth: authenticationReducer
    });

    // compose composes all of our middleware
    return redux.createStore(reducer, initialState, redux.compose(
        redux.applyMiddleware(thunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f
    ));

};
