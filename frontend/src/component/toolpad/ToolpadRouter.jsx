import * as React from 'react';
import { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout, getCurrentUser } from '../../redux/slices/authSlice';
import { ReactRouterAppProvider } from '@toolpad/core/react-router';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { Toaster } from 'react-hot-toast'
import toast from 'react-hot-toast';
import Theme from './Theme'
import Navigation from './Navigation';
import AppTitle from './AppTitle';
import SidebarFooter from './SidebarFooter';
import JobPage from '../job/JobPage'
import Home from '../home/Home'
import Favorite from '../user/Favorite'
import Signin from '../auth/Signin'
import Signup from '../auth/Signup'

function ToolpadRouter() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { user, isAuthenticated, token } = useSelector(state => state.auth);
  const [signinOpen, setSigninOpen] = useState(false);
  const [signupOpen, setSignupOpen] = useState(false);
  
  const session = isAuthenticated && user ? {
    user: {
      name: user.username,
      email: user.email,
      image: user.avatar,
    }
  } : null;

  const authentication = {
    signIn: () => {
      openSignin();
    },
    signOut: () => {
      dispatch(logout());
      toast.success("You have been logged out successfully");
      navigate('/');
    }
  };

  const openSignin = () => {
    setSigninOpen(true);
    setSignupOpen(false);
  };

  const closeSignin = () => {
    setSigninOpen(false);
  };

  const openSignup = () => {
    setSignupOpen(true);
    setSigninOpen(false);
  };

  const closeSignup = () => {
    setSignupOpen(false);
  };

  useEffect(() => {
    if (token && !user) {
      dispatch(getCurrentUser());
    }
  }, [dispatch, token, user]);

  return (
    <>
      <Signin
        open={signinOpen} 
        onClose={closeSignin} 
        openSignup={openSignup} 
      />
      <Signup
        open={signupOpen} 
        onClose={closeSignup} 
        openSignin={openSignin} 
      />
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
          </Routes>
          <Toaster position='top-center'/>
        </DashboardLayout>
      </ReactRouterAppProvider>
    </>
  );
}

export default ToolpadRouter;