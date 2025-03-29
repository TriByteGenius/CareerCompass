import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import JobCard from './JobCard';

// Mock required util functions to simplify tests
vi.mock('../../util/getFreshness', () => ({
  default: () => ({ color: '#4caf50', text: 'Just Posted', textColor: 'white' })
}));

vi.mock('../../util/formatDate', () => ({
  default: () => 'Mar 28, 2025'
}));

// Mock redux for predictable auth state
vi.mock('react-redux', async () => {
  const actual = await vi.importActual('react-redux');
  return {
    ...actual,
    useDispatch: () => vi.fn(),
    useSelector: () => ({ isAuthenticated: false })
  };
});

// Create a mock version of window.open instead of overwriting it
const mockOpen = vi.fn();
vi.stubGlobal('open', mockOpen);

describe('JobCard Component', () => {
  // Sample job data for testing
  const mockJob = {
    id: '123',
    name: 'Software Engineer',
    company: 'Tech Company',
    type: 'Full-time',
    location: 'Remote',
    time: '2025-03-28T12:00:00Z',
    url: 'https://example.com/job',
    website: 'LINKEDIN',
    status: 'new'
  };

  // Reset mocks before each test
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders job card with basic information', () => {
    renderWithProviders(<JobCard {...mockJob} />);

    // Check just the essential information to avoid brittle tests
    expect(screen.getByTestId('job-title')).toHaveTextContent('Software Engineer');
    expect(screen.getByTestId('company-name')).toHaveTextContent('Tech Company');
    expect(screen.getByTestId('job-website')).toHaveTextContent('LINKEDIN');
  });

  it('shows "Save Job" when not favorited and "Saved" when favorited', () => {
    // First render as unfavorited
    const { rerender } = renderWithProviders(
      <JobCard {...mockJob} isFavorite={false} />
    );
    expect(screen.getByTestId('favorite-button')).toHaveTextContent('Save Job');
    
    // Re-render as favorited
    rerender(
      <JobCard {...mockJob} isFavorite={true} />
    );
    expect(screen.getByTestId('favorite-button')).toHaveTextContent('Saved');
  });

  it('opens the job URL when Apply Now button is clicked', () => {
    renderWithProviders(<JobCard {...mockJob} />);
    
    // Click the Apply Now button
    fireEvent.click(screen.getByTestId('apply-button'));
    
    // Verify window.open was called with the correct URL
    expect(mockOpen).toHaveBeenCalledWith('https://example.com/job', '_blank');
  });

  it('conditionally renders application status button', () => {
    // First render without application status
    const { rerender } = renderWithProviders(
      <JobCard {...mockJob} showApplicationStatus={false} />
    );
    expect(screen.queryByTestId('status-button')).not.toBeInTheDocument();
    
    // Now render with application status
    rerender(
      <JobCard 
        {...mockJob} 
        showApplicationStatus={true} 
        applicationStatus="applied" 
      />
    );
    const statusButton = screen.getByTestId('status-button');
    expect(statusButton).toBeInTheDocument();
    expect(statusButton).toHaveTextContent('Applied');
  });

  it('shows job location when available', () => {
    renderWithProviders(<JobCard {...mockJob} />);
    expect(screen.getByTestId('job-location')).toHaveTextContent('Remote');
  });

  it('shows job type information', () => {
    renderWithProviders(<JobCard {...mockJob} />);
    expect(screen.getByTestId('job-type')).toHaveTextContent('Full-time');
  });

  it('shows formatted date', () => {
    renderWithProviders(<JobCard {...mockJob} />);
    expect(screen.getByTestId('job-date')).toHaveTextContent('Posted: Mar 28, 2025');
  });
});