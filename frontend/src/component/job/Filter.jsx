import React, { useEffect, useState } from 'react';
import { 
  FormControl, 
  InputLabel, 
  MenuItem, 
  Select, 
  Tooltip, 
  Button,
  Box,
  InputAdornment,
  TextField,
  Paper
} from "@mui/material";
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import RefreshIcon from '@mui/icons-material/Refresh';
import SearchIcon from '@mui/icons-material/Search';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import FilterListIcon from '@mui/icons-material/FilterList';
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";

const Filter = () => {
  const [searchParams] = useSearchParams();
  const params = new URLSearchParams(searchParams);
  const pathname = useLocation().pathname;
  const navigate = useNavigate();
  
  const [website, setWebsite] = useState("all");
  const [status, setStatus] = useState("all");
  const [timeInDays, setTimeInDays] = useState("all");
  const [sortOrder, setSortOrder] = useState("asc");
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const currentWebsite = searchParams.get("website") || "all";
    const currentStatus = searchParams.get("status") || "all";
    const currentTimeInDays = searchParams.get("timeInDays") || "all";
    const currentSortOrder = searchParams.get("sortby") || "asc";
    const currentSearchTerm = searchParams.get("keyword") || "";

    setWebsite(currentWebsite);
    setStatus(currentStatus);
    setTimeInDays(currentTimeInDays);
    setSortOrder(currentSortOrder);
    setSearchTerm(currentSearchTerm);
  }, [searchParams]);

  // Handle search term change with debounce
  useEffect(() => { 
    const handler = setTimeout(() => {
      if (searchTerm) {
        params.set("keyword", searchTerm);
      } else {
        params.delete("keyword");
      }
      navigate(`${pathname}?${params.toString()}`);
    }, 700);

    return () => {
      clearTimeout(handler);
    };
  }, [searchTerm, navigate, pathname, params]);

  const handleWebsiteChange = (event) => {
    const selectedWebsite = event.target.value;

    if (selectedWebsite === "all") {
      params.delete("website");
    } else {
      params.set("website", selectedWebsite);
    }
    navigate(`${pathname}?${params}`);
    setWebsite(selectedWebsite);
  };

  const handleStatusChange = (event) => {
    const selectedStatus = event.target.value;

    if (selectedStatus === "all") {
      params.delete("status");
    } else {
      params.set("status", selectedStatus);
    }
    navigate(`${pathname}?${params}`);
    setStatus(selectedStatus);
  };

  const handleTimeInDaysChange = (event) => {
    const selectedTimeInDays = event.target.value;

    if (selectedTimeInDays === "all") {
      params.delete("timeInDays");
    } else {
      params.set("timeInDays", selectedTimeInDays);
    }
    navigate(`${pathname}?${params}`);
    setTimeInDays(selectedTimeInDays);
  };

  const toggleSortOrder = () => {
    setSortOrder((prevOrder) => {
      const newOrder = (prevOrder === "asc") ? "desc" : "asc";
      params.set("sortby", newOrder);
      navigate(`${pathname}?${params}`);
      return newOrder;
    });
  };

  const handleClearFilters = () => {
    navigate(pathname);
  };

  return (
    <Paper elevation={0}>
      <Box
        sx={{
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          gap: 2
        }}
      >
        {/* Search Field */}
        <TextField
          placeholder="Search Jobs"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          variant="outlined"
          size="small"
          sx={{ 
            width: { xs: '100%', sm: '220px' },
            "& .MuiOutlinedInput-root": {
              borderRadius: 1,
              height: '40px'
            }
          }}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
        />

        {/* Website Selection */}
        <FormControl size="small" sx={{ width: { xs: '100%', sm: '120px' } }}>
          <InputLabel id="website-select-label">Website</InputLabel>
          <Select
            labelId="website-select-label"
            value={website}
            onChange={handleWebsiteChange}
            label="Website"
            sx={{ height: '40px' }}
          >
            <MenuItem value="all">All</MenuItem>
            <MenuItem value="LINKEDIN">LinkedIn</MenuItem>
            <MenuItem value="INDEED">Indeed</MenuItem>
            <MenuItem value="IRISHJOBS">Irish Jobs</MenuItem>
            <MenuItem value="JOBS">Jobs.ie</MenuItem>
          </Select>
        </FormControl>

        {/* Status Selection */}
        <FormControl size="small" sx={{ width: { xs: '100%', sm: '120px' } }}>
          <InputLabel id="status-select-label">Status</InputLabel>
          <Select
            labelId="status-select-label"
            value={status}
            onChange={handleStatusChange}
            label="Status"
            sx={{ height: '40px' }}
            startAdornment={
              <InputAdornment position="start">
                <FilterListIcon fontSize="small" />
              </InputAdornment>
            }
          >
            <MenuItem value="all">All</MenuItem>
            <MenuItem value="new">New</MenuItem>
            <MenuItem value="applied">Applied</MenuItem>
            <MenuItem value="interview">Interview</MenuItem>
            <MenuItem value="rejected">Rejected</MenuItem>
            <MenuItem value="offer">Offer</MenuItem>
          </Select>
        </FormControl>

        {/* Time Period Selection */}
        <FormControl size="small" sx={{ width: { xs: '100%', sm: '150px' } }}>
          <InputLabel id="time-select-label">Time Period</InputLabel>
          <Select
            labelId="time-select-label"
            value={timeInDays}
            onChange={handleTimeInDaysChange}
            label="Time Period"
            sx={{ height: '40px' }}
            startAdornment={
              <InputAdornment position="start">
                <CalendarMonthIcon fontSize="small" />
              </InputAdornment>
            }
          >
            <MenuItem value="all">All Time</MenuItem>
            <MenuItem value="1">Last 24 Hours</MenuItem>
            <MenuItem value="7">Last Week</MenuItem>
            <MenuItem value="30">Last Month</MenuItem>
          </Select>
        </FormControl>

        {/* Sort Button */}
        <Tooltip title={`Sorted by date: ${sortOrder}`}>
          <Button 
            variant="contained" 
            onClick={toggleSortOrder}
            startIcon={sortOrder === "asc" ? <ArrowUpwardIcon /> : <ArrowDownwardIcon />}
            sx={{ 
              height: '40px',
              width: { xs: '100%', sm: 'auto' },
              minWidth: '120px' 
            }}
          >
            Sort By
          </Button>
        </Tooltip>

        {/* Clear Filter Button */}
        <Button 
          variant="contained" 
          color="error"
          startIcon={<RefreshIcon />}
          onClick={handleClearFilters}
          sx={{ 
            height: '40px',
            width: { xs: '100%', sm: 'auto' },
            minWidth: '120px'
          }}
        >
          Clear Filter
        </Button>
      </Box>
    </Paper>
  );
};

export default Filter;