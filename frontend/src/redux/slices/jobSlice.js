import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../../api/api';

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
  async (queryString, { rejectWithValue }) => {
    try {
      const response = await api.get(`/jobs?${queryString}`);
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch jobs');
    }
  }
);

// Admin Search action
export const searchJobsAdmin = createAsyncThunk(
  'jobs/searchJobsAdmin',
  async (keyword, { getState, rejectWithValue }) => {
    try {
      const { token } = getState().auth;
      const response = await api.get(`/jobs/update?keyword=${encodeURIComponent(keyword)}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Admin search failed');
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
      state.pagination.pageNumber = action.payload;
    },
    setPageSize: (state, action) => {
      state.pagination.pageSize = action.payload;
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
        state.pagination.totalElements = action.payload.totalElements;
        state.pagination.totalPages = action.payload.totalPages;
        state.pagination.pageNumber = action.payload.pageNumber;
        state.pagination.pageSize = action.payload.pageSize;
        state.pagination.lastPage = action.payload.lastPage;
      })
      .addCase(fetchJobs.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });

    // Admin Search Jobs
    builder
      .addCase(searchJobsAdmin.pending, (state) => {
        state.adminSearchLoading = true;
        state.adminSearchMessage = null;
        state.error = null;
      })
      .addCase(searchJobsAdmin.fulfilled, (state, action) => {
        state.adminSearchLoading = false;
        state.adminSearchMessage = action.payload;
      })
      .addCase(searchJobsAdmin.rejected, (state, action) => {
        state.adminSearchLoading = false;
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