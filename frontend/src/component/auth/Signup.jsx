import React from 'react';
import { useForm } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { signup, clearError } from '../../redux/slices/authSlice';
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

const Signup = ({ open, onClose, openSignin }) => {
  const dispatch = useDispatch();
  const { loading } = useSelector(state => state.auth);

  const { 
    register, 
    handleSubmit, 
    formState: { errors },
    reset
  } = useForm({
    defaultValues: {
      username: '',
      email: '',
      password: ''
    }
  });

  const onSubmit = async (data) => {
    try {
      const resultAction = await dispatch(signup(data));
      if (signup.fulfilled.match(resultAction)) {
        toast.success('Registration successful! Please sign in to continue.');
        reset();
        onClose();
        openSignin();
      } else {
        toast.error(resultAction.payload || 'Registration failed. Please try again.');
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

  const handleSigninClick = () => {
    reset();
    onClose();
    openSignin();
  };

  return (
    <Dialog 
      open={open} 
      onClose={handleClose}
      maxWidth="xs"
      fullWidth
      data-testid="signup-dialog"
    >
      <DialogTitle>
        <Typography variant="h5" component="div" sx={{ fontWeight: 'bold', textAlign: 'center' }}>
          Create Account
        </Typography>
      </DialogTitle>

      <DialogContent sx={{ px: 6, pb: 4 }}>
        <form onSubmit={handleSubmit(onSubmit)} data-testid="signup-form">
          <TextField
            label="Username"
            variant="outlined"
            fullWidth
            margin="normal"
            {...register('username', { 
              required: 'Username is required',
              minLength: {
                value: 2,
                message: 'Username must be at least 2 characters'
              },
              maxLength: {
                value: 20,
                message: 'Username cannot exceed 20 characters'
              }
            })}
            error={!!errors.username}
            helperText={errors.username?.message}
            data-testid="username-input"
          />

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
              },
              maxLength: {
                value: 50,
                message: 'Email cannot exceed 50 characters'
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
            data-testid="signup-button"
          >
            {loading ? <CircularProgress size={24} /> : 'Sign Up'}
          </Button>
        </form>

        <Typography variant="body2" sx={{ mt: 2, textAlign: 'center' }}>
          Already have an account?{' '}
          <Button 
            color="primary" 
            sx={{ p: 0, minWidth: 'auto', fontWeight: 'bold', textTransform: 'none' }}
            onClick={handleSigninClick}
            data-testid="signin-link"
          >
            Sign In
          </Button>
        </Typography>
      </DialogContent>
    </Dialog>
  );
};

export default Signup;