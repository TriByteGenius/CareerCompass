import * as React from 'react';
import Typography from '@mui/material/Typography';

function SidebarFooter({ mini }) {
  return (
    <Typography
      variant="caption"
      sx={{ m: 1, whiteSpace: 'nowrap', overflow: 'hidden' }}
    >
      {mini ? 'TriByteGenius' : `© 2025 Developed by TriByteGenius`}
    </Typography>
  );
}

export default SidebarFooter;