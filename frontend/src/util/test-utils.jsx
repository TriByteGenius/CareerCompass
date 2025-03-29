import React from 'react';
import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import { BrowserRouter } from 'react-router-dom';
import authReducer from '../redux/slices/authSlice';
import jobReducer from '../redux/slices/jobSlice';
import favoriteReducer from '../redux/slices/favoriteSlice';

export function renderWithProviders(
  ui, {
  preloadedState = {},
  store = configureStore({
    reducer: {
      auth: authReducer,
      job: jobReducer,
      favorites: favoriteReducer,
    },
    preloadedState
  }),
  ...renderOptions
} = {}) {
  function Wrapper({ children }) {
    return (
      <Provider store={store}>
        <BrowserRouter>
          {children}
        </BrowserRouter>
      </Provider>
    );
  }
  return { 
    store,
    ...render(ui, { wrapper: Wrapper, ...renderOptions }) 
  };
}