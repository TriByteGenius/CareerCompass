import * as React from 'react';
import HomeIcon from '@mui/icons-material/Home';
import WorkIcon from '@mui/icons-material/Work';
import FavoriteIcon from '@mui/icons-material/Favorite';

const Navigation = [
  {
    kind: 'header',
    title: 'Main items',
  },
  {
    segment: '',
    title: 'Home',
    icon: <HomeIcon />,
  },
  {
    segment: 'jobs',
    title: 'Jobs',
    icon: <WorkIcon />,
  },
  {
    kind: 'divider',
  },
  {
    kind: 'header',
    title: 'User',
  },
  {
    segment: 'favorites',
    title: 'Favorite Jobs',
    icon: <FavoriteIcon />,
  },
];

export default Navigation;