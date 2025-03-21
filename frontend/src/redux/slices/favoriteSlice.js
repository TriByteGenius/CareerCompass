import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { logout } from '../slices/authSlice';
import api from '../../api/api';
import { toast } from 'react-hot-toast';

const initialState = {
  favoriteJobs: [],
  loading: false,
  error: null,
};

// Toggle favorite status (add/remove)
export const toggleFavorite = createAsyncThunk(
  'favorites/toggle',
  async (jobId, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      const response = await api.post(`/favorites/${jobId}/toggle`, null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to toggle favorite');
    }
  }
);

// Get all favorite jobs for the current user
export const getFavoriteJobs = createAsyncThunk(
  'favorites/getFavorites',
  async (_, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      const response = await api.get('/favorites', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch favorite jobs');
    }
  }
);

// Update job status (applied, interview, offer, rejected)
export const updateJobStatus = createAsyncThunk(
  'favorites/updateStatus',
  async ({ jobId, status }, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      const response = await api.put(
        `/favorites/${jobId}/status?status=${status}`, 
        null,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to update job status');
    }
  }
);

// Get jobs filtered by status
export const getJobsByStatus = createAsyncThunk(
  'favorites/getByStatus',
  async (status, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      const response = await api.get(`/favorites/status/${status}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch jobs by status');
    }
  }
);

const favoriteSlice = createSlice({
  name: 'favorites',
  initialState,
  reducers: {
    clearFavoriteError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    // Toggle favorite
    builder
      .addCase(toggleFavorite.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(toggleFavorite.fulfilled, (state, action) => {
        state.loading = false;
        // If action.payload is a message response (removed from favorites)
        if (action.payload && action.payload.message) {
          // Remove job from favorites
          state.favoriteJobs = state.favoriteJobs.filter(
            job => job.job.id !== action.meta.arg
          );
        } 
        // If action.payload contains a job (added to favorites)
        else if (action.payload) {
          // Check if job already exists in favorites
          const existingIndex = state.favoriteJobs.findIndex(
            item => item.id === action.payload.id
          );
          
          if (existingIndex >= 0) {
            // Update existing job
            state.favoriteJobs[existingIndex] = action.payload;
          } else {
            // Add new job to favorites
            state.favoriteJobs.push(action.payload);
          }
        }
      })
      .addCase(toggleFavorite.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });

    // Get favorite jobs
    builder
      .addCase(getFavoriteJobs.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getFavoriteJobs.fulfilled, (state, action) => {
        state.loading = false;
        state.favoriteJobs = action.payload;
      })
      .addCase(getFavoriteJobs.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });

    // Update job status
    builder
      .addCase(updateJobStatus.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateJobStatus.fulfilled, (state, action) => {
        state.loading = false;
        // Update the status in the local state
        const updatedJob = action.payload;
        const jobIndex = state.favoriteJobs.findIndex(job => job.id === updatedJob.id);
        if (jobIndex !== -1) {
          state.favoriteJobs[jobIndex] = updatedJob;
        }
      })
      .addCase(updateJobStatus.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });

    // Get jobs by status
    builder
      .addCase(getJobsByStatus.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getJobsByStatus.fulfilled, (state, action) => {
        state.loading = false;
        state.favoriteJobs = action.payload;
      })
      .addCase(getJobsByStatus.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });

      // Reset the favorites state to initial values
      builder.addCase(logout.fulfilled, (state) => {
        state.favoriteJobs = [];
      });
  },
});

export const { clearFavoriteError } = favoriteSlice.actions;
export default favoriteSlice.reducer;