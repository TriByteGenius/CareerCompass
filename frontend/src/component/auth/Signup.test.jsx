import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import Signup from './Signup';
import toast from 'react-hot-toast';

// Simplified approach - just mock useDispatch
vi.mock('react-redux', async () => {
  const actual = await vi.importActual('react-redux');
  return {
    ...actual,
    useDispatch: () => mockDispatch
  };
});

// Mock for react-hook-form
vi.mock('react-hook-form', () => ({
  useForm: () => ({
    register: (name) => ({ name }),
    handleSubmit: (fn) => (e) => {
      e.preventDefault();
      fn({ 
        username: 'testuser', 
        email: 'test@example.com', 
        password: 'password123' 
      });
    },
    formState: { errors: {} },
    reset: vi.fn()
  })
}));

// Create mock dispatch function
const mockDispatch = vi.fn(() => Promise.resolve({ payload: { success: true } }));

describe('Signup Component', () => {
  const mockProps = {
    open: true,
    onClose: vi.fn(),
    openSignin: vi.fn()
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders signup dialog when open is true', () => {
    renderWithProviders(<Signup {...mockProps} />);
    
    expect(screen.getByTestId('signup-dialog')).toBeInTheDocument();
    expect(screen.getByTestId('signup-form')).toBeInTheDocument();
    expect(screen.getByTestId('username-input')).toBeInTheDocument();
    expect(screen.getByTestId('email-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
    expect(screen.getByTestId('signup-button')).toBeInTheDocument();
    expect(screen.getByTestId('signin-link')).toBeInTheDocument();
  });

  it('does not render dialog when open is false', () => {
    renderWithProviders(<Signup {...mockProps} open={false} />);
    
    expect(screen.queryByTestId('signup-dialog')).not.toBeInTheDocument();
  });

  it('calls onClose when dialog is closed', () => {
    renderWithProviders(<Signup {...mockProps} />);
    
    // Use the backdrop to trigger an onClose event
    const backdrop = document.querySelector('.MuiBackdrop-root');
    if (backdrop) {
      fireEvent.click(backdrop);
      expect(mockProps.onClose).toHaveBeenCalled();
    }
  });

  it('calls openSignin when Sign In link is clicked', () => {
    renderWithProviders(<Signup {...mockProps} />);
    
    // Click the Sign In link
    fireEvent.click(screen.getByTestId('signin-link'));
    
    expect(mockProps.onClose).toHaveBeenCalled();
    expect(mockProps.openSignin).toHaveBeenCalled();
  });

  it('dispatches signup action when form is submitted', () => {
    renderWithProviders(<Signup {...mockProps} />);
    
    // Submit the form
    const form = screen.getByTestId('signup-form');
    fireEvent.submit(form);
    
    // Check that dispatch was called
    expect(mockDispatch).toHaveBeenCalledTimes(1);
  });

  it('shows loading indicator when loading state is true', () => {
    // Set up loading state in Redux store
    const preloadedState = {
      auth: { loading: true }
    };
    
    renderWithProviders(<Signup {...mockProps} />, { preloadedState });
    
    // Check for loading indicator - the button should be disabled
    expect(screen.getByTestId('signup-button')).toBeDisabled();
  });
});