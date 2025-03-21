import React from 'react';
import { 
  Container, 
  Typography, 
  Box, 
  Button, 
  Paper, 
  Grid,
  Card,
  CardContent,
  Divider,
} from '@mui/material';
import GitHubIcon from '@mui/icons-material/GitHub';
import WorkIcon from '@mui/icons-material/Work';
import SearchIcon from '@mui/icons-material/Search';
import FavoriteIcon from '@mui/icons-material/Favorite';
import PeopleIcon from '@mui/icons-material/People';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const navigate = useNavigate();

  const handleExploreJobs = () => {
    navigate('/jobs');
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ py: 6 }}>
        {/* Hero section */}
        <Paper 
          elevation={0}
          sx={{ 
            p: 4, 
            mb: 6, 
            backgroundColor: '#4fc3f7',
            borderRadius: 2,
            color: 'white'
          }}
        >
          <Typography variant="h3" component="h1" gutterBottom fontWeight="bold">
            Welcome to CareerCompass
          </Typography>
          <Typography variant="h6" sx={{ mb: 3 }}>
            Streamlining your job search process with intelligent tools and organized workflows
          </Typography>
          <Button 
            variant="contained" 
            size="large" 
            color="error"
            startIcon={<WorkIcon />}
            onClick={handleExploreJobs}
            sx={{ fontWeight: 'bold', px: 4, py: 1.5 }}
          >
            Explore Jobs
          </Button>
        </Paper>

        {/* Feature Cards */}
        <Typography variant="h4" component="h2" gutterBottom sx={{ mb: 3 }}>
          Key Features
        </Typography>
        <Grid container spacing={3} sx={{ mb: 6 }}>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Box sx={{ mb: 2, display: 'flex', justifyContent: 'center' }}>
                  <SearchIcon color="primary" sx={{ fontSize: 48 }} />
                </Box>
                <Typography variant="h6" component="h3" gutterBottom align="center">
                  Smart Job Search
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Find relevant job opportunities with advanced filtering across multiple platforms in one convenient location.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Box sx={{ mb: 2, display: 'flex', justifyContent: 'center' }}>
                  <FavoriteIcon color="primary" sx={{ fontSize: 48 }} />
                </Box>
                <Typography variant="h6" component="h3" gutterBottom align="center">
                  Application Tracking
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Save favorite jobs and track your application status from initial interest to offer with our intuitive workflow system.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={4}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                <Box sx={{ mb: 2, display: 'flex', justifyContent: 'center' }}>
                  <PeopleIcon color="primary" sx={{ fontSize: 48 }} />
                </Box>
                <Typography variant="h6" component="h3" gutterBottom align="center">
                  User-Friendly Interface
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Enjoy a clean, modern design that makes managing your job search efficient and stress-free.
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>

        {/* About Section */}
        <Paper sx={{ p: 4, mb: 6, borderRadius: 2 }}>
          <Typography variant="h4" component="h2" gutterBottom>
            About CareerCompass
          </Typography>
          <Typography variant="body1" paragraph>
            CareerCompass is a web-based job management platform designed to streamline the job search process. 
            In today's competitive job market, keeping track of applications across multiple platforms can be 
            overwhelming. Our solution provides a centralized dashboard where you can discover opportunities, 
            save interesting positions, and monitor your progress.
          </Typography>
          <Typography variant="body1" paragraph>
            Built with modern technologies including React, Fast Api and Spring Boot, CareerCompass offers 
            a responsive and intuitive user experience that helps job seekers stay organized and focused 
            on their career goals.
          </Typography>
          <Box sx={{ mt: 3 }}>
            <Button 
              variant="outlined" 
              startIcon={<GitHubIcon />}
              href="https://github.com/TriByteGenius/CareerCompass"
              target="_blank"
              rel="noopener noreferrer"
            >
              View on GitHub
            </Button>
          </Box>
        </Paper>

        {/* Footer */}
        <Divider sx={{ mb: 4 }} />
        <Typography variant="body2" color="text.secondary" align="center">
          © 2025 Developed by TriByteGenius
        </Typography>
      </Box>
    </Container>
  );
};

export default Home;