import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import ToolpadRouter from './ToolpadRouter';

// Mock all external dependencies simply
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    BrowserRouter: ({ children }) => <div data-testid="browser-router">{children}</div>,
    Routes: ({ children }) => <div data-testid="routes">{children}</div>,
    Route: ({ path, element }) => <div data-testid={`route-${path || 'home'}`}>{element}</div>,
    useNavigate: () => vi.fn()
  };
});

// Mock redux
vi.mock('react-redux', async () => {
  const actual = await vi.importActual('react-redux');
  return {
    ...actual,
    useSelector: vi.fn().mockImplementation(selector => 
      selector({
        auth: {
          user: null,
          isAuthenticated: false,
          token: null
        }
      })
    ),
    useDispatch: () => vi.fn()
  };
});

// Simple mocks for auth components
vi.mock('../auth/Signin', () => ({
  default: ({ open, onClose, openSignup }) => (
    <div data-testid="signin-dialog" style={{ display: open ? 'block' : 'none' }}>
      <button data-testid="close-signin" onClick={onClose}>Close</button>
      <button data-testid="to-signup" onClick={openSignup}>To Signup</button>
    </div>
  )
}));

vi.mock('../auth/Signup', () => ({
  default: ({ open, onClose, openSignin }) => (
    <div data-testid="signup-dialog" style={{ display: open ? 'block' : 'none' }}>
      <button data-testid="close-signup" onClick={onClose}>Close</button>
      <button data-testid="to-signin" onClick={openSignin}>To Signin</button>
    </div>
  )
}));

// Simple mocks for Toolpad components
vi.mock('@toolpad/core/react-router', () => ({
  ReactRouterAppProvider: ({ children, authentication }) => (
    <div data-testid="react-router-app-provider">
      <button 
        data-testid="signin-button" 
        onClick={authentication.signIn}
      >
        Sign In
      </button>
      <button 
        data-testid="signout-button" 
        onClick={authentication.signOut}
      >
        Sign Out
      </button>
      {children}
    </div>
  )
}));

vi.mock('@toolpad/core/DashboardLayout', () => ({
  DashboardLayout: ({ children }) => (
    <div data-testid="dashboard-layout">
      {children}
    </div>
  )
}));

// Mock page components
vi.mock('../home/Home', () => ({
  default: () => <div data-testid="home-component">Home Page</div>
}));

vi.mock('../job/JobPage', () => ({
  default: () => <div data-testid="job-component">Job Page</div>
}));

vi.mock('../user/Favorite', () => ({
  default: () => <div data-testid="favorite-component">Favorite Page</div>
}));

vi.mock('react-hot-toast', () => ({
  Toaster: () => <div data-testid="toaster">Toaster</div>
}));

describe('ToolpadRouter Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders main layout containers', () => {
    renderWithProviders(<ToolpadRouter />);
    
    // Check base structure
    expect(screen.getByTestId('react-router-app-provider')).toBeInTheDocument();
    expect(screen.getByTestId('dashboard-layout')).toBeInTheDocument();
    expect(screen.getByTestId('toaster')).toBeInTheDocument();
  });

  it('renders auth dialogs initially closed', () => {
    renderWithProviders(<ToolpadRouter />);
    
    // Check that auth dialogs exist but are hidden
    const signinDialog = screen.getByTestId('signin-dialog');
    const signupDialog = screen.getByTestId('signup-dialog');
    
    expect(signinDialog).toHaveStyle('display: none');
    expect(signupDialog).toHaveStyle('display: none');
  });

  it('opens signin dialog when signin button is clicked', () => {
    renderWithProviders(<ToolpadRouter />);
    
    // Click the signin button
    fireEvent.click(screen.getByTestId('signin-button'));
    
    // Check that signin dialog is now visible
    expect(screen.getByTestId('signin-dialog')).toHaveStyle('display: block');
    expect(screen.getByTestId('signup-dialog')).toHaveStyle('display: none');
  });

  it('closes signin dialog when close button is clicked', () => {
    renderWithProviders(<ToolpadRouter />);
    
    // Open signin dialog
    fireEvent.click(screen.getByTestId('signin-button'));
    
    // Close it
    fireEvent.click(screen.getByTestId('close-signin'));
    
    // Check that it's closed
    expect(screen.getByTestId('signin-dialog')).toHaveStyle('display: none');
  });

  it('switches from signin to signup dialog', () => {
    renderWithProviders(<ToolpadRouter />);
    
    // Open signin dialog
    fireEvent.click(screen.getByTestId('signin-button'));
    
    // Switch to signup
    fireEvent.click(screen.getByTestId('to-signup'));
    
    // Check that signin is closed and signup is open
    expect(screen.getByTestId('signin-dialog')).toHaveStyle('display: none');
    expect(screen.getByTestId('signup-dialog')).toHaveStyle('display: block');
  });

  it('switches from signup to signin dialog', () => {
    renderWithProviders(<ToolpadRouter />);
    
    // First open signin
    fireEvent.click(screen.getByTestId('signin-button'));
    
    // Switch to signup
    fireEvent.click(screen.getByTestId('to-signup'));
    
    // Then switch back to signin
    fireEvent.click(screen.getByTestId('to-signin'));
    
    // Check that signup is closed and signin is open
    expect(screen.getByTestId('signup-dialog')).toHaveStyle('display: none');
    expect(screen.getByTestId('signin-dialog')).toHaveStyle('display: block');
  });
});