import React from 'react';
import { 
  Card, 
  CardContent, 
  Typography, 
  Button, 
  Chip, 
  Box,
  Stack,
  Divider
} from '@mui/material';
import WorkIcon from '@mui/icons-material/Work';
import BusinessIcon from '@mui/icons-material/Business';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import LaunchIcon from '@mui/icons-material/Launch';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import WebIcon from '@mui/icons-material/Web';
import IndeedIcon from '@mui/icons-material/Description';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import AccessTimeIcon from '@mui/icons-material/AccessTime';

// Helper function to determine badge color and text based on freshness
const getFreshnessBanner = (timeString) => {
  if (!timeString) {
    return { color: '#e0e0e0', text: 'Unknown date' };
  }
  
  try {
    const jobDate = new Date(timeString);
    const now = new Date();
    const diffTime = Math.abs(now - jobDate);
    const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffHours < 24) {
      return { color: '#4caf50', text: 'Just Posted', textColor: 'white' }; // Green
    } else if (diffDays < 3) {
      return { color: '#2196f3', text: `${diffDays}d ago`, textColor: 'white' }; // Blue
    } else if (diffDays < 7) { 
      return { color: '#ff9800', text: `${diffDays}d ago`, textColor: 'white' }; // Orange
    } else {
      return { color: '#757575', text: `${diffDays}d ago`, textColor: 'white' }; // Gray
    }
  } catch (error) {
    return { color: '#e0e0e0', text: 'Unknown date', textColor: 'black' };
  }
};

// Get website icon based on website name
const getWebsiteIcon = (website) => {
  switch (website?.toUpperCase()) {
    case 'LINKEDIN':
      return <LinkedInIcon style={{ color: '#0A66C2' }} />;
    case 'INDEED':
      return <IndeedIcon style={{ color: '#2164f3' }} />;
    default:
      return <WebIcon />;
  }
};

// Format date for display,transform iso to user friendly format
const formatDate = (timeString) => {
  if (!timeString) return 'Unknown date';
  
  try {
    const date = new Date(timeString);
    return new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit', 
      minute: '2-digit', 
      hour12: true
    }).format(date);
  } catch (error) {
    return 'Unknown date';
  }
};

// This function would be implemented later to add/remove job from favorites
const handleToggleFavorite = () => {
  console.log("Toggle favorite for job:", name);
};

const JobCard = ({ 
  name, 
  company, 
  type, 
  location, 
  time, 
  status, 
  url, 
  website,
  isFavorite = false
}) => {
  const freshness = getFreshnessBanner(time);
  return (
    <Card 
      elevation={2} 
      sx={{ 
        mb: 3, 
        borderRadius: '10px',
        transition: 'transform 0.2s, box-shadow 0.2s',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: 6
        }
      }}
    >
      <Box sx={{ position: 'relative' }}>
        {/* Company and website banner */}
        <Box 
          sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'space-between',
            p: 2,
            bgcolor: '#f5f5f5' 
          }}
        >
          <Stack direction="row" spacing={1} alignItems="center">
            <BusinessIcon fontSize="small" color="action" />
            <Typography variant="subtitle1" fontWeight="bold" fontSize={"medium"} sx={{ mr: 1 }}>
              {company || 'Company'}
            </Typography>
          </Stack>
          
          {website && (
            <Chip
              icon={getWebsiteIcon(website)}
              label={website}
              size="medium"
              variant="outlined"
            />
          )}
        </Box>
      </Box>

      <CardContent sx={{ pt: 2 }}>
        {/* Main job details */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="h6" fontWeight="bold" gutterBottom>
            {name || 'Job Position'}
          </Typography>
          
          <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
            <WorkIcon fontSize="small" color="action" />
            <Typography variant="body2" color="text.secondary">
              {type || 'Position Type'}
            </Typography>
          </Stack>
          
          {location && (
            <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
              <LocationOnIcon fontSize="small" color="action" />
              <Typography variant="body2" color="text.secondary">
                {location}
              </Typography>
            </Stack>
          )}

          <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
            <AccessTimeIcon fontSize="small" color="action" />
            <Typography variant="body2" color="text.secondary">
              Posted: {formatDate(time)}
            </Typography>
          </Stack>
        </Box>

        {/* Status and freshness badges */}
        <Stack direction="row" spacing={1} sx={{ my: 2 }}>
          <Chip
            label={freshness.text}
            size="small"
            sx={{ 
              bgcolor: freshness.color,
              color: freshness.textColor,
              fontWeight: 'bold'
            }}
          />
          
          {status && (
            <Chip
              label={status}
              size="small"
              color={status.toLowerCase() === 'new' ? 'success' : 'default'}
              variant="outlined"
            />
          )}
        </Stack>

        <Divider sx={{ my: 2 }} />

        {/* Action buttons */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
          <Button
            variant="outlined"
            size="small"
            onClick={handleToggleFavorite}
            startIcon={isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />}
            color={isFavorite ? "error" : "primary"}
          >
            {isFavorite ? "Saved" : "Save Job"}
          </Button>
          
          <Button 
            variant="contained" 
            color="primary" 
            size="small" 
            endIcon={<LaunchIcon />}
            onClick={() => window.open(url, '_blank')}
          >
            Apply Now
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default JobCard;