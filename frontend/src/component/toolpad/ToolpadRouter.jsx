import * as React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout } from '../../redux/slices/authSlice';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { Toaster } from 'react-hot-toast'
import Theme from './Theme'
import Navigation from './Navigation';
import AppTitle from './AppTitle';
import SidebarFooter from './SidebarFooter';
import JobPage from '../job/JobPage'
import Home from '../home/Home'
import Favorite from '../user/Favorite'
import Login from '../auth/Login'
import Register from '../auth/Register'

function ToolpadRouter() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useSelector(state => state.auth);
  
  const session = isAuthenticated && user ? {
    user: {
      name: user.username,
      email: user.email,
      image: user.avatar,
    }
  } : null;

  const authentication = {
    signIn: () => {
      navigate('/login');
    },
    signOut: () => {
      dispatch(logout())
      navigate('/');
    }
  };

  return (
    <ReactRouterAppProvider
      navigation={Navigation}
      theme={Theme}
      session={session}
      authentication={authentication}
    >
      <DashboardLayout
        slots={{
          appTitle: AppTitle,
          sidebarFooter: SidebarFooter,
        }}
      >
        <Routes>
          <Route path='' element={<Home/>}/>
          <Route path='jobs' element={<JobPage />}/>
          <Route path='favorites' element={<Favorite />}/>
          <Route path='login' element={<Login />}/>
          <Route path='register' element={<Register />}/>
        </Routes>
        <Toaster position='top-center'/>
      </DashboardLayout>
    </ReactRouterAppProvider>
  );
}

export default ToolpadRouter;