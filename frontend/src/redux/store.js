import { configureStore } from '@reduxjs/toolkit'
import authReducer from './slices/authSlice'
import jobReducer from './slices/jobSlice'

const store = configureStore({
  reducer: {
    auth: authReducer,
    job: jobReducer,
  },
})

export default store