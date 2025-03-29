import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import Filter from './Filter';

// Mock react-router-dom hooks
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useLocation: () => ({ pathname: '/jobs' }),
    useSearchParams: () => [
      { 
        get: (key) => null, 
        toString: () => ''
      }, 
      vi.fn()
    ]
  };
});

describe('Filter Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders all filter elements correctly', () => {
    renderWithProviders(<Filter />);
    
    // Check for presence of all filter elements
    expect(screen.getByTestId('filter-container')).toBeInTheDocument();
    expect(screen.getByTestId('search-input')).toBeInTheDocument();
    expect(screen.getByTestId('website-select')).toBeInTheDocument();
    expect(screen.getByTestId('status-select')).toBeInTheDocument();
    expect(screen.getByTestId('time-select')).toBeInTheDocument();
    expect(screen.getByTestId('sort-button')).toBeInTheDocument();
    expect(screen.getByTestId('clear-filter-button')).toBeInTheDocument();
  });

  it('navigates to base path when clear filter button is clicked', () => {
    renderWithProviders(<Filter />);
    
    // Find and click the clear filter button
    const clearButton = screen.getByTestId('clear-filter-button');
    fireEvent.click(clearButton);
    
    // Verify navigation to base path
    expect(mockNavigate).toHaveBeenCalledWith('/jobs');
  });

  it('handles sort button click', () => {
    renderWithProviders(<Filter />);
    
    // Find and click the sort button
    const sortButton = screen.getByTestId('sort-button');
    fireEvent.click(sortButton);
    
    // Verify that navigate was called (specific URL params would be set in the component)
    expect(mockNavigate).toHaveBeenCalled();
  });
  
  it('renders website selection dropdown correctly', () => {
    renderWithProviders(<Filter />);
    const websiteSelect = screen.getByTestId('website-select');
    expect(websiteSelect).toBeInTheDocument();
  });
  
  it('renders status selection dropdown correctly', () => {
    renderWithProviders(<Filter />);
    const statusSelect = screen.getByTestId('status-select');
    expect(statusSelect).toBeInTheDocument();
  });
  
  it('renders time period selection dropdown correctly', () => {
    renderWithProviders(<Filter />);
    const timeSelect = screen.getByTestId('time-select');
    expect(timeSelect).toBeInTheDocument();
  });
});