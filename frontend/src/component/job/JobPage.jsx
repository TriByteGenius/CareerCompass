import React, { useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import useJobFilter from '../hooks/useJobFilter';
import { fetchJobs } from '../../redux/slices/jobSlice';
import { Grid, Box, Typography, Container, CircularProgress, Alert } from '@mui/material';
import Filter from './Filter';
import JobCard from './JobCard';
import Paginations from './Paginations';
import WorkIcon from '@mui/icons-material/Work';

const JobPage = () => {
  useJobFilter();
  const dispatch = useDispatch();
  const { jobs, loading, error } = useSelector(state => state.job);
  const { pageNumber, pageSize, totalElements, totalPages } = useSelector(state => state.job.pagination);

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 3 }}>
        {/* Header */}
        <Typography variant="h4" component="h1" sx={{ mb: 3, display: 'flex', alignItems: 'center' }}>
          <WorkIcon sx={{ mr: 1, fontSize: 32 }} />
          Job Listings
        </Typography>

        {/* Filter */}
        <Box sx={{ position: 'sticky', top: 0, zIndex: 10, backgroundColor: 'white', py: 2, mb: 3 }}>
          <Filter />
        </Box>

        {/* Loading indicator */}
        {loading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
            <CircularProgress />
          </Box>
        )}

        {/* Error message */}
        {error && (
          <Alert severity="error" sx={{ my: 2 }}>
            {error}
          </Alert>
        )}

        {/* Job count */}
        {!loading && jobs && (
          <Typography variant="body2" color="text.secondary" fontSize={'large'} sx={{ mb: 2 }}>
            Found {totalElements || jobs.length} jobs
          </Typography>
        )}

        {/* Job cards grid */}
        <Grid container spacing={3}>
          {jobs && jobs.map((job, index) => (
            <Grid item xs={12} md={6} key={index}>
              <JobCard {...job} />
            </Grid>
          ))}
        </Grid>

        {/* No jobs message */}
        {!loading && (!jobs || jobs.length === 0) && (
          <Alert severity="info" sx={{ my: 4 }}>
            No jobs found. Try adjusting your search filters.
          </Alert>
        )}

        {/* Fixed position pagination */}
        {totalPages > 0 && (
          <Box sx={{ 
            position: 'sticky', 
            bottom: 0, 
            backgroundColor: 'white', 
            py: 2, 
            mt: 3,
            display: 'flex',
            justifyContent: 'center',
            borderTop: '1px solid #eee'
          }}>
            <Paginations numberOfPage={totalPages} />
          </Box>
        )}
      </Box>
    </Container>
  );
};

export default JobPage;