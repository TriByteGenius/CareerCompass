import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import useJobFilter from '../hooks/useJobFilter';
import { getFavoriteJobs } from '../../redux/slices/favoriteSlice';
import { Grid, Box, Typography, Container, CircularProgress, Alert } from '@mui/material';
import Filter from './Filter';
import JobCard from './JobCard';
import Paginations from './Paginations';
import WorkIcon from '@mui/icons-material/Work';

// Helper function to check if a job is in favorites
const isJobFavorited = (favoriteJobs = [], jobId) => {
  if (!Array.isArray(favoriteJobs) || !jobId) return false;
  return favoriteJobs.some(item => item.job?.id === jobId);
};

// Helper function to get job status from favorites
const getJobStatus = (favoriteJobs = [], jobId) => {
  if (!Array.isArray(favoriteJobs) || !jobId) return 'new';
  const favoriteJob = favoriteJobs.find(item => item.job?.id === jobId);
  return favoriteJob?.status || 'new';
};

const JobPage = () => {
  useJobFilter();
  const dispatch = useDispatch();
  const { jobs, loading, error } = useSelector(state => state.job);
  const { pageNumber, pageSize, totalElements, totalPages } = useSelector(state => state.job.pagination);
  const { isAuthenticated } = useSelector(state => state.auth);
  const { favoriteJobs } = useSelector(state => state.favorites);

  // Fetch favorite jobs only when the user is authenticated
  useEffect(() => {
    if (isAuthenticated) {
      dispatch(getFavoriteJobs());
    }
  }, [dispatch, isAuthenticated]);

  return (
    <Container maxWidth="lg" data-testid="jobs-container">
      <Box sx={{ py: 3 }}>
        {/* Header */}
        <Typography variant="h4" component="h1" sx={{ mb: 3, display: 'flex', alignItems: 'center' }}
          data-testid="jobs-title">
          <WorkIcon sx={{ mr: 1, fontSize: 32 }} />
          Job Listings
        </Typography>

        {/* Filter */}
        <Box sx={{ top: 0, zIndex: 10, py: 2, mb: 3 }} data-testid="filter-section">
          <Filter />
        </Box>

        {/* Loading indicator */}
        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}
            data-testid="loading-indicator">
            <CircularProgress />
          </Box>
        )}

        {/* Error message */}
        {error && (
          <Alert severity="error" sx={{ my: 2 }} data-testid="error-alert">
            {error}
          </Alert>
        )}

        {/* Job count */}
        {!loading && jobs && (
          <Typography variant="body2" color="text.secondary" fontSize={'large'} sx={{ mb: 2 }}
            data-testid="job-count">
            Found {totalElements || jobs.length} jobs
          </Typography>
        )}

        {/* Job cards grid */}
        <Grid container spacing={3} data-testid="jobs-grid">
          {jobs && jobs.map((job) => (
            <Grid item xs={12} md={6} key={job.id}>
              <JobCard 
                {...job} 
                // Check if this job is favorited
                isFavorite={isJobFavorited(favoriteJobs, job.id)}
                // Explicitly set showApplicationStatus to false to hide the application status button
                showApplicationStatus={false}
              />
            </Grid>
          ))}
        </Grid>

        {/* No jobs message */}
        {!loading && (!jobs || jobs.length === 0) && (
          <Alert severity="info" sx={{ my: 4 }} data-testid="no-jobs-alert">
            No jobs found. Try adjusting your search filters.
          </Alert>
        )}

        {/* Fixed position pagination */}
        {totalPages > 0 && (
          <Box sx={{ 
            bottom: 0, 
            py: 2, 
            mt: 3,
            display: 'flex',
            justifyContent: 'center',
            borderTop: '1px solid #eee'
          }}
          data-testid="pagination-container">
            <Paginations numberOfPage={totalPages} />
          </Box>
        )}
      </Box>
    </Container>
  );
};

export default JobPage;