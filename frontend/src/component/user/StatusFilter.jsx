import React from 'react';
import { 
  Box, 
  Tabs, 
  Tab, 
  Badge,
  useMediaQuery,
  useTheme
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import SendIcon from '@mui/icons-material/Send';
import WorkIcon from '@mui/icons-material/Work';
import CancelIcon from '@mui/icons-material/Cancel';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import FavoriteIcon from '@mui/icons-material/Favorite';

// Status options with icons for tabs
const statusTabs = [
  { value: 'all', label: 'All', icon: <FavoriteIcon /> },
  { value: 'new', label: 'New', icon: <CheckCircleIcon /> },
  { value: 'applied', label: 'Applied', icon: <SendIcon /> },
  { value: 'interview', label: 'Interview', icon: <WorkIcon /> },
  { value: 'offer', label: 'Offer', icon: <EmojiEventsIcon /> },
  { value: 'rejected', label: 'Rejected', icon: <CancelIcon /> },
];

const StatusFilter = ({ value, onChange, counts = {} }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <Box 
      sx={{ 
        width: '100%', 
        mb: 3,
        borderBottom: 1,
        borderColor: 'divider'
      }}
      data-testid="status-filter-container"
    >
      <Tabs
        value={value}
        onChange={onChange}
        variant={isMobile ? "scrollable" : "standard"}
        scrollButtons={isMobile ? "auto" : false}
        allowScrollButtonsMobile
        centered={!isMobile}
        sx={{
          '& .MuiTab-root': {
            textTransform: 'none',
            minWidth: isMobile ? 'auto' : 100,
          }
        }}
        data-testid="status-tabs"
      >
        {statusTabs.map((tab) => (
          <Tab
            key={tab.value}
            value={tab.value}
            icon={tab.icon}
            data-testid={`status-tab-${tab.value}`}
            label={
              counts[tab.value] ? (
                <Badge
                  color="primary"
                  sx={{ mr: 1 }}
                  data-testid={`badge-count-${tab.value}`}
                >
                  {tab.label}
                </Badge>
              ) : (
                tab.label
              )}
          />
        ))}
      </Tabs>
    </Box>
  );
};

export default StatusFilter;