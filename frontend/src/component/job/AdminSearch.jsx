import React, { useState } from 'react';
import { 
  Box,
  Typography,
  TextField,
  IconButton,
  Paper,
  CircularProgress
} from "@mui/material";
import SearchIcon from '@mui/icons-material/Search';
import { useDispatch, useSelector } from 'react-redux';
import { searchJobsAdmin } from '../../redux/slices/jobSlice';
import { toast } from 'react-hot-toast';

const AdminSearch = () => {
  const dispatch = useDispatch();
  const [searchTerm, setSearchTerm] = useState("");
  const { adminSearchLoading } = useSelector(state => state.job);

  // Handle search button click
  const handleSearch = () => {
    if (searchTerm.trim()) {
      dispatch(searchJobsAdmin(searchTerm));
      toast.success(`Searching database for: ${searchTerm}`);
    }
  };

  // Handle key press in search field - allow Enter key to trigger search
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <Paper 
      elevation={1} 
      sx={{ 
        p: 2, 
        mb: 3, 
        display: 'flex', 
        flexDirection: { xs: 'column', sm: 'row' }, 
        alignItems: 'center', 
        gap: 2,
        backgroundColor: 'text.disabled'
      }}
      data-testid="admin-search-container"
    >
      <Typography 
        variant="subtitle1" 
        sx={{ 
          fontWeight: 'medium',
          flex: 1
        }}
        data-testid="admin-search-text"
      >
        Try admin search to fetch new jobs
      </Typography>
      
      <TextField
        placeholder="Search for new jobs..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        onKeyPress={handleKeyPress}
        variant="outlined"
        size="small"
        sx={{ 
          width: { xs: '100%', sm: '320px' },
          "& .MuiOutlinedInput-root": {
            borderRadius: 1,
            height: '40px'
          }
        }}
        InputProps={{
          endAdornment: (
            adminSearchLoading ? (
              <CircularProgress size={24} />
            ) : (
              <IconButton 
                onClick={handleSearch}
                edge="end"
                aria-label="search"
                data-testid="admin-search-button"
              >
                <SearchIcon />
              </IconButton>
            )
          )
        }}
        disabled={adminSearchLoading}
        data-testid="admin-search-input"
      />
    </Paper>
  );
};

export default AdminSearch;