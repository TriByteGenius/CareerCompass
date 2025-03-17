import * as React from 'react';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import GitHubIcon from '@mui/icons-material/GitHub';

function CustomAppTitle() {
  return (
    <Stack direction="row" alignItems="center" spacing={2}>
      <GitHubIcon fontSize="large" />
      <Typography variant="h6">Career Compass</Typography>
    </Stack>
  );
}

export default CustomAppTitle;