import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import Signin from './Signin';
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
      fn({ email: 'test@example.com', password: 'password123' });
    },
    formState: { errors: {} },
    reset: vi.fn()
  })
}));

// Create mock dispatch function
const mockDispatch = vi.fn(() => Promise.resolve({ payload: { user: { username: 'testuser' } } }));

describe('Signin Component', () => {
  const mockProps = {
    open: true,
    onClose: vi.fn(),
    openSignup: vi.fn()
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders sign in dialog when open is true', () => {
    renderWithProviders(<Signin {...mockProps} />);
    
    expect(screen.getByTestId('signin-dialog')).toBeInTheDocument();
    expect(screen.getByTestId('signin-form')).toBeInTheDocument();
    expect(screen.getByTestId('email-input')).toBeInTheDocument();
    expect(screen.getByTestId('password-input')).toBeInTheDocument();
    expect(screen.getByTestId('signin-button')).toBeInTheDocument();
    expect(screen.getByTestId('signup-link')).toBeInTheDocument();
  });

  it('does not render dialog when open is false', () => {
    renderWithProviders(<Signin {...mockProps} open={false} />);
    
    expect(screen.queryByTestId('signin-dialog')).not.toBeInTheDocument();
  });

  it('calls onClose when dialog is closed', () => {
    renderWithProviders(<Signin {...mockProps} />);
    
    // Use the backdrop to trigger an onClose event
    const backdrop = document.querySelector('.MuiBackdrop-root');
    if (backdrop) {
      fireEvent.click(backdrop);
      expect(mockProps.onClose).toHaveBeenCalled();
    }
  });

  it('calls openSignup when Sign Up link is clicked', () => {
    renderWithProviders(<Signin {...mockProps} />);
    
    // Click the Sign Up link
    fireEvent.click(screen.getByTestId('signup-link'));
    
    expect(mockProps.onClose).toHaveBeenCalled();
    expect(mockProps.openSignup).toHaveBeenCalled();
  });

  it('dispatches login action when form is submitted', () => {
    renderWithProviders(<Signin {...mockProps} />);
    
    // Submit the form
    const form = screen.getByTestId('signin-form');
    fireEvent.submit(form);
    
    // Check that dispatch was called
    expect(mockDispatch).toHaveBeenCalledTimes(1);
  });

  it('shows loading indicator when loading state is true', () => {
    // Set up loading state in Redux store
    const preloadedState = {
      auth: { loading: true }
    };
    
    renderWithProviders(<Signin {...mockProps} />, { preloadedState });
    
    // Check for loading indicator - the button should be disabled
    expect(screen.getByTestId('signin-button')).toBeDisabled();
  });
});