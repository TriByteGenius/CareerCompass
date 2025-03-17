import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';

const initialState = {
  jobs: [],
  pagination: {
    totalElements: 0,
    totalPages: 0,
    pageNumber: 0,
    pageSize: 20,
    lastPage: false,
  },
  filters: {
    keyword: '',
    status: '',
    website: '',
    timeInDays: null,
    sortBy: 'time',
    sortOrder: 'desc',
  },
  loading: false,
  error: null,
};

export const fetchJobs = createAsyncThunk(
  'jobs/fetchJobs',
  async (_, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      const { filters, pageNumber, pageSize } = getState().jobs;
      
      const params = {
        pageNumber,
        pageSize,
        ...filters,
      };
      
      const response = await axios.get('/api/jobs', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params,
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch jobs');
    }
  }
);

// Search Jobs with py backend
export const searchJobs = createAsyncThunk(
  'jobs/searchJobs',
  async (searchData, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      await axios.post('/api/jobs/search', searchData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return { success: true };
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Search failed');
    }
  }
);

const jobSlice = createSlice({
  name: 'jobs',
  initialState,
  reducers: {
    clearJobError: (state) => {
      state.error = null;
    },
    setPageNumber: (state, action) => {
      state.pageNumber = action.payload;
    },
    setPageSize: (state, action) => {
      state.pageSize = action.payload;
    },
    updateFilter: (state, action) => {
      state.filters = {
        ...state.filters,
        ...action.payload,
      };
      state.pageNumber = 0;
    },
    resetFilters: (state) => {
      state.filters = {
        keyword: '',
        status: '',
        website: '',
        timeInDays: null,
        sortBy: 'time',
        sortOrder: 'desc',
      };
      state.pageNumber = 0;
    },
  },
  extraReducers: (builder) => {
    // Fetch Jobs
    builder
      .addCase(fetchJobs.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchJobs.fulfilled, (state, action) => {
        state.loading = false;
        state.jobs = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.totalPages = action.payload.totalPages;
        state.pageNumber = action.payload.pageNumber;
        state.pageSize = action.payload.pageSize;
        state.lastPage = action.payload.lastPage;
      })
      .addCase(fetchJobs.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });

    // Search Jobs
    builder
      .addCase(searchJobs.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(searchJobs.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(searchJobs.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { 
  clearJobError, 
  setPageNumber, 
  setPageSize, 
  updateFilter, 
  resetFilters 
} = jobSlice.actions;
export default jobSlice.reducer;