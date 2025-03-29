import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import Favorite from './Favorite';

// Simplified approach - mock useDispatch
const mockDispatch = vi.fn();
vi.mock('react-redux', async () => {
  const actual = await vi.importActual('react-redux');
  return {
    ...actual,
    useDispatch: () => mockDispatch
  };
});

// Mock the status filter component to simplify testing
vi.mock('./StatusFilter', () => ({
  default: ({ value, onChange, counts }) => (
    <div data-testid="mock-status-filter">
      <button 
        data-testid="status-all-button"
        onClick={(e) => onChange(e, 'all')}>
        All ({counts.all || 0})
      </button>
      <button 
        data-testid="status-applied-button"
        onClick={(e) => onChange(e, 'applied')}>
        Applied ({counts.applied || 0})
      </button>
    </div>
  )
}));

// Mock the JobCard component
vi.mock('../job/JobCard', () => ({
  default: (props) => (
    <div data-testid={`job-card-${props.id}`}>
      <div data-testid="job-title">{props.name}</div>
      <div data-testid="job-company">{props.company}</div>
      <div data-testid="job-status">{props.applicationStatus}</div>
    </div>
  )
}));

describe('Favorite Component', () => {
  // Sample favorite jobs data for testing
  const mockFavoriteJobs = [
    { 
      id: '1', 
      status: 'new',
      job: { 
        id: '101', 
        name: 'Frontend Developer', 
        company: 'Tech Inc', 
        type: 'Full-time',
        location: 'Remote', 
        time: '2025-03-20T12:00:00Z',
        url: 'https://example.com/job1',
        website: 'LINKEDIN',
        status: 'new'
      }
    },
    { 
      id: '2', 
      status: 'applied',
      job: { 
        id: '102', 
        name: 'Backend Developer', 
        company: 'Code Corp', 
        type: 'Full-time',
        location: 'New York', 
        time: '2025-03-25T12:00:00Z',
        url: 'https://example.com/job2',
        website: 'INDEED',
        status: 'new'
      }
    }
  ];

  // Reset mocks before each test
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows loading indicator when loading favorites', () => {
    // Set up loading state in Redux store
    const preloadedState = {
      auth: { isAuthenticated: true },
      favorites: { loading: true, favoriteJobs: [] }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    expect(screen.getByTestId('loading-indicator')).toBeInTheDocument();
  });

  it('shows authentication warning when user is not authenticated', () => {
    // Set up unauthenticated state in Redux store
    const preloadedState = {
      auth: { isAuthenticated: false },
      favorites: { loading: false, favoriteJobs: [] }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    expect(screen.getByTestId('unauthenticated-alert')).toBeInTheDocument();
    expect(screen.getByText('You need to be logged in to view your favorites.')).toBeInTheDocument();
  });

  it('shows empty favorites message when authenticated but no favorites', () => {
    // Set up authenticated state with no favorites
    const preloadedState = {
      auth: { isAuthenticated: true },
      favorites: { loading: false, favoriteJobs: [] }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    expect(screen.getByTestId('no-favorites-alert')).toBeInTheDocument();
    expect(screen.getByText('You haven\'t saved any jobs to your favorites yet.')).toBeInTheDocument();
  });

  it('displays favorite jobs when available', () => {
    // Set up state with favorite jobs
    const preloadedState = {
      auth: { isAuthenticated: true },
      favorites: { loading: false, favoriteJobs: mockFavoriteJobs }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    // Check job count
    expect(screen.getByTestId('job-count')).toHaveTextContent('Found 2 jobs');
    
    // Check that job cards are rendered
    expect(screen.getByTestId('job-card-101')).toBeInTheDocument();
    expect(screen.getByTestId('job-card-102')).toBeInTheDocument();
  });

  it('dispatches action when component mounts and user is authenticated', () => {
    // Set up authenticated state
    const preloadedState = {
      auth: { isAuthenticated: true },
      favorites: { loading: false, favoriteJobs: [] }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    // Verify that dispatch was called on mount
    expect(mockDispatch).toHaveBeenCalledTimes(1);
  });

  it('dispatches action when status filter changes', () => {
    // Set up authenticated state with favorite jobs
    const preloadedState = {
      auth: { isAuthenticated: true },
      favorites: { loading: false, favoriteJobs: mockFavoriteJobs }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    // Clear the initial dispatch call count
    mockDispatch.mockClear();
    
    // Click on the Applied filter button
    const appliedButton = screen.getByTestId('status-applied-button');
    fireEvent.click(appliedButton);
    
    // Verify that dispatch was called when filter changed
    expect(mockDispatch).toHaveBeenCalledTimes(1);
  });
  
  it('displays correct favorite job details', () => {
    // Set up state with favorite jobs
    const preloadedState = {
      auth: { isAuthenticated: true },
      favorites: { loading: false, favoriteJobs: mockFavoriteJobs }
    };
    
    renderWithProviders(<Favorite />, { preloadedState });
    
    // Check that job details are correctly displayed
    expect(screen.getByText('Frontend Developer')).toBeInTheDocument();
    expect(screen.getByText('Tech Inc')).toBeInTheDocument();
    expect(screen.getByText('Backend Developer')).toBeInTheDocument();
    expect(screen.getByText('Code Corp')).toBeInTheDocument();
  });
});