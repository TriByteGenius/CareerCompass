import { configureStore } from '@reduxjs/toolkit'
import authReducer from './slices/authSlice'
import jobReducer from './slices/jobSlice'
import favoriteReducer from './slices/favoriteSlice'

const store = configureStore({
  reducer: {
    auth: authReducer,
    job: jobReducer,
    favorites: favoriteReducer,
  },
})

export default store