import React from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { login, clearError } from '../../redux/slices/authSlice';
import { 
  Button, 
  TextField, 
  Typography, 
  Dialog,
  DialogContent,
  DialogTitle,
  CircularProgress
} from '@mui/material';
import toast from 'react-hot-toast';

const Signin = ({ open, onClose, openSignup }) => {
  const dispatch = useDispatch();
  const { loading } = useSelector(state => state.auth);

  const { 
    register, 
    handleSubmit, 
    formState: { errors },
    reset
  } = useForm({
    defaultValues: {
      email: '',
      password: ''
    }
  });

  const onSubmit = async (data) => {
    try {
      const resultAction = await dispatch(login(data));
      if (login.fulfilled.match(resultAction)) {
        toast.success('Login successful!');
        reset();
        onClose();
      } else {
        toast.error(resultAction.payload || 'Login failed. Please check your credentials.');
      }
    } catch (err) {
      toast.error('An error occurred. Please try again.');
      dispatch(clearError());
    }
  };

  const handleClose = () => {
    reset();
    onClose();
  };

  const handleSignupClick = () => {
    reset();
    onClose();
    openSignup();
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleClose}
      maxWidth="xs"
      fullWidth
      data-testid="signin-dialog"
    >
      <DialogTitle>
        <Typography variant="h5" component="div" sx={{ fontWeight: 'bold', textAlign: 'center' }}>
          Sign In
        </Typography>
      </DialogTitle>

      <DialogContent sx={{ px: 6, pb: 4 }}>
        <form onSubmit={handleSubmit(onSubmit)} data-testid="signin-form">
          <TextField
            label="Email"
            variant="outlined"
            fullWidth
            margin="normal"
            {...register('email', { 
              required: 'Email is required',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Invalid email address'
              }
            })}
            error={!!errors.email}
            helperText={errors.email?.message}
            data-testid="email-input"
          />

          <TextField
            label="Password"
            variant="outlined"
            type="password"
            fullWidth
            margin="normal"
            {...register('password', { 
              required: 'Password is required',
              minLength: {
                value: 6,
                message: 'Password must be at least 6 characters'
              },
              maxLength: {
                value: 20,
                message: 'Password cannot exceed 20 characters'
              }
            })}
            error={!!errors.password}
            helperText={errors.password?.message}
            data-testid="password-input"
          />

          <Button 
            type="submit" 
            variant="contained" 
            color="primary" 
            fullWidth 
            sx={{ mt: 3, mb: 2, py: 1.2 }}
            disabled={loading}
            data-testid="signin-button"
          >
            {loading ? <CircularProgress size={24} /> : 'Sign In'}
          </Button>
        </form>

        <Typography variant="body2" sx={{ mt: 2, textAlign: 'center' }}>
          Don't have an account?{' '}
          <Button 
            color="primary" 
            sx={{ p: 0, minWidth: 'auto', fontWeight: 'bold', textTransform: 'none' }}
            onClick={handleSignupClick}
            data-testid="signup-link"
          >
            Sign Up
          </Button>
        </Typography>
      </DialogContent>
    </Dialog>
  );
};

export default Signin;