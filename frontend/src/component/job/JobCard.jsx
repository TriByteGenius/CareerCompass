import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { toggleFavorite, updateJobStatus } from '../../redux/slices/favoriteSlice';
import { 
  Card, 
  CardContent, 
  Typography, 
  Button, 
  Chip, 
  Box,
  Stack,
  Divider,
  CircularProgress,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText
} from '@mui/material';
import getFreshnessBanner from '../../util/getFreshness';
import formatDate from '../../util/formatDate';
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
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import SendIcon from '@mui/icons-material/Send';
import CancelIcon from '@mui/icons-material/Cancel';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import toast from 'react-hot-toast';

// Status options with icons and colors
const statusOptions = [
  { value: 'new', label: 'New', icon: <CheckCircleIcon />, color: 'success' },
  { value: 'applied', label: 'Applied', icon: <SendIcon />, color: 'primary' },
  { value: 'interview', label: 'Interview', icon: <WorkIcon />, color: 'info' },
  { value: 'offer', label: 'Offer', icon: <EmojiEventsIcon />, color: 'warning' },
  { value: 'rejected', label: 'Rejected', icon: <CancelIcon />, color: 'error' },
];

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

// Get status option info by value
const getStatusOption = (statusValue) => {
  return statusOptions.find(option => option.value === statusValue) || statusOptions[0];
};

const JobCard = ({ 
  id,
  name, 
  company, 
  type, 
  location, 
  time, 
  status = 'new', 
  applicationStatus = 'new',
  url, 
  website,
  isFavorite = false,
  showApplicationStatus,
}) => {
  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector(state => state.auth);
  const [favoriteLoading, setFavoriteLoading] = useState(false);
  const [statusLoading, setStatusLoading] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const freshness = getFreshnessBanner(time);
  
  // Find current status option
  const currentStatus = getStatusOption(applicationStatus);

  const handleToggleFavorite = async () => {
    // Prevent API calls if user is not authenticated
    if (!isAuthenticated) {
      toast.error('Please sign in to save jobs to favorites');
      return;
    }

    setFavoriteLoading(true);
    try {
      await dispatch(toggleFavorite(id)).unwrap();
      toast.success(isFavorite ? 'Removed from favorites' : 'Added to favorites');
    } catch (error) {
      toast.error(error || 'Failed to update favorites');
    } finally {
      setFavoriteLoading(false);
    }
  };

  // Handle opening status menu
  const handleStatusClick = (event) => {
    if (!isAuthenticated) {
      toast.error('Please sign in to update job status');
      return;
    }
    setAnchorEl(event.currentTarget);
  };

  // Handle closing status menu
  const handleStatusClose = () => {
    setAnchorEl(null);
  };

  // Handle selecting a new status
  const handleStatusChange = async (newStatus) => {
    if (newStatus === applicationStatus) {
      handleStatusClose();
      return;
    }

    setStatusLoading(true);
    try {
      await dispatch(updateJobStatus({ jobId: id, status: newStatus })).unwrap();
      toast.success(`Status updated to ${newStatus}`);
    } catch (error) {
      toast.error(error || 'Failed to update status');
    } finally {
      setStatusLoading(false);
      handleStatusClose();
    }
  };

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
      data-testid="job-card"
    >
      <Box sx={{ position: 'relative' }}>
        {/* Company and website banner */}
        <Box 
          sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'space-between',
            p: 2,
            bgcolor: 'text.disabled'
          }}
          data-testid="company-banner"
        >
          <Stack direction="row" spacing={1} alignItems="center">
            <BusinessIcon fontSize="small" color="action" />
            <Typography variant="subtitle1" fontWeight="bold" fontSize={"medium"} sx={{ mr: 1 }}
              data-testid="company-name">
              {company || 'Company'}
            </Typography>
          </Stack>
          
          {website && (
            <Chip
              icon={getWebsiteIcon(website)}
              label={website}
              size="medium"
              variant="outlined"
              data-testid="job-website"
            />
          )}
        </Box>
      </Box>

      <CardContent sx={{ pt: 2 }}>
        {/* Main job details */}
        <Box sx={{ mb: 2 }}>
          <Typography variant="h6" fontWeight="bold" gutterBottom data-testid="job-title">
            {name || 'Job Name'}
          </Typography>
          
          <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
            <WorkIcon fontSize="small" color="action" />
            <Typography variant="body2" color="text.secondary" data-testid="job-type">
              {type || 'Job Type'}
            </Typography>
          </Stack>
          
          {location && (
            <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
              <LocationOnIcon fontSize="small" color="action" />
              <Typography variant="body2" color="text.secondary" data-testid="job-location">
                {location || 'Job Location'}
              </Typography>
            </Stack>
          )}

          <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
            <AccessTimeIcon fontSize="small" color="action" />
            <Typography variant="body2" color="text.secondary" data-testid="job-date">
              Posted: {formatDate(time)}
            </Typography>
          </Stack>
        </Box>

        {/* Status and freshness badges */}
        <Stack direction="row" spacing={1} sx={{ my: 2 }} alignItems="center">
          {/* Freshness badge - always shown */}
          <Chip
            label={freshness.text}
            size="small"
            sx={{ 
              bgcolor: freshness.color,
              color: freshness.textColor,
              fontWeight: 'bold'
            }}
            data-testid="freshness-badge"
          />

          {status && (
            <Chip
              label={status}
              size="small"
              color={status.toLowerCase() === 'new' ? 'success' : 'default'}
              variant="outlined"
              data-testid="status-badge"
            />
          )}
          
          {/* Only show application status button in Favorites page */}
          {showApplicationStatus && (
            <Button
              variant="outlined"
              size="small"
              color={currentStatus.color}
              onClick={handleStatusClick}
              startIcon={statusLoading ? <CircularProgress size={16} /> : currentStatus.icon}
              endIcon={<ArrowDropDownIcon />}
              disabled={statusLoading || !isAuthenticated}
              data-testid="status-button"
            >
              {currentStatus.label}
            </Button>
          )}
          
          {/* Status selection menu */}
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleStatusClose}
            data-testid="status-menu"
          >
            {statusOptions.map((option) => (
              <MenuItem 
                key={option.value} 
                onClick={() => handleStatusChange(option.value)}
                selected={applicationStatus === option.value}
                data-testid={`status-option-${option.value}`}
              >
                <ListItemIcon>
                  {React.cloneElement(option.icon, { color: option.color })}
                </ListItemIcon>
                <ListItemText>{option.label}</ListItemText>
              </MenuItem>
            ))}
          </Menu>
        </Stack>

        <Divider sx={{ my: 2 }} />

        {/* Action buttons */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
          <Button
            variant="outlined"
            size="small"
            onClick={handleToggleFavorite}
            startIcon={favoriteLoading ? <CircularProgress size={16} /> : 
              (isFavorite ? <FavoriteIcon /> : <FavoriteBorderIcon />)}
            color={isFavorite ? "error" : "primary"}
            disabled={favoriteLoading}
            data-testid="favorite-button"
          >
            {isFavorite ? "Saved" : "Save Job"}
          </Button>
          
          <Button 
            variant="contained" 
            color="primary" 
            size="small" 
            endIcon={<LaunchIcon />}
            onClick={() => window.open(url, '_blank')}
            data-testid="apply-button"
          >
            Apply Now
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
};

export default JobCard;