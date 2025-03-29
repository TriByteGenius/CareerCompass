import React, { useEffect, useState, useMemo } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { getFavoriteJobs, getJobsByStatus } from '../../redux/slices/favoriteSlice';
import { 
  Container, 
  Typography, 
  Box, 
  Grid, 
  Alert, 
  CircularProgress, 
  Paper,
} from '@mui/material';
import FavoriteIcon from '@mui/icons-material/Favorite';
import JobCard from '../job/JobCard';
import StatusFilter from './StatusFilter';

const Favorite = () => {
  const dispatch = useDispatch();
  const [currentTab, setCurrentTab] = useState('all');
  const { isAuthenticated } = useSelector((state) => state.auth);
  const { favoriteJobs, loading, error } = useSelector((state) => state.favorites);

  // Load favorite jobs when component mounts (only if authenticated)
  useEffect(() => {
    if (isAuthenticated) {
      dispatch(getFavoriteJobs());
    }
  }, [dispatch, isAuthenticated]);

  // Handle tab change and filter jobs by status
  const handleTabChange = (event, newValue) => {
    setCurrentTab(newValue);
    
    if (isAuthenticated) {
      if (newValue === 'all') {
        dispatch(getFavoriteJobs());
      } else {
        dispatch(getJobsByStatus(newValue));
      }
    }
  };

  // Calculate status counts for badges
  const statusCounts = useMemo(() => {
    if (!favoriteJobs || !favoriteJobs.length) return { all: 0 };
    
    const counts = { all: favoriteJobs.length };
    
    favoriteJobs.forEach(item => {
      const status = item.status || 'new';
      counts[status] = (counts[status] || 0) + 1;
    });
    
    return counts;
  }, [favoriteJobs]);

  return (
    <Container maxWidth="lg" data-testid="favorites-container">
      <Box sx={{ py: 3 }}>
        {/* Header */}
        <Typography variant="h4" component="h1" sx={{ mb: 3, display: 'flex', alignItems: 'center' }}
          data-testid="favorites-title">
          <FavoriteIcon sx={{ mr: 1, fontSize: 32, color: 'error.main' }} />
          Favorite Jobs
        </Typography>

        {/* Status filter tabs */}
        <Paper elevation={1} sx={{ mb: 3 }}>
          <StatusFilter 
            value={currentTab} 
            onChange={handleTabChange} 
            counts={statusCounts}
          />
        </Paper>

        {/* Loading indicator */}
        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}
            data-testid="loading-indicator">
            <CircularProgress />
          </Box>
        )}

        {/* UnAuthenticated message */}
        {!isAuthenticated && (
          <Alert severity="warning" sx={{ my: 4 }}
            data-testid="unauthenticated-alert">
            {'You need to be logged in to view your favorites.'}
          </Alert>
        )}

        {/* No favorites message */}
        {!loading && isAuthenticated && (!favoriteJobs || favoriteJobs.length === 0) && (
          <Alert severity="info" sx={{ my: 4 }}
            data-testid="no-favorites-alert">
            {currentTab === 'all' 
              ? 'You haven\'t saved any jobs to your favorites yet.' 
              : `You don't have any jobs with status "${currentTab}".`}
          </Alert>
        )}

        {/* Job count */}
        {!loading && favoriteJobs && favoriteJobs.length > 0 && (
          <Typography variant="body2" color="text.secondary" fontSize={'large'} sx={{ mb: 2 }}
            data-testid="job-count">
            Found {favoriteJobs.length} {currentTab !== 'all' ? `${currentTab} ` : ''}jobs
          </Typography>
        )}

        {/* Favorite jobs grid */}
        <Grid container spacing={3} data-testid="favorites-grid">
          {favoriteJobs && favoriteJobs.map((favoriteJob) => (
            <Grid item xs={12} md={6} key={favoriteJob.id}>
              <JobCard 
                id={favoriteJob.job.id}
                name={favoriteJob.job.name}
                company={favoriteJob.job.company}
                type={favoriteJob.job.type}
                location={favoriteJob.job.location}
                time={favoriteJob.job.time}
                url={favoriteJob.job.url}
                website={favoriteJob.job.website}
                status={favoriteJob.job.status}
                applicationStatus={favoriteJob.status}
                isFavorite={true}
                showApplicationStatus={true}
              />
            </Grid>
          ))}
        </Grid>
      </Box>
    </Container>
  );
};

export default Favorite;